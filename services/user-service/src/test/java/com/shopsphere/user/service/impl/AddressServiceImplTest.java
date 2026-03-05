package com.shopsphere.user.service.impl;

import com.shopsphere.user.dto.AddressDto;
import com.shopsphere.user.exception.UserNotFoundException;
import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for AddressServiceImpl
 *
 * Tests cover:
 * - Adding new addresses
 * - Retrieving addresses
 * - Setting default addresses
 * - Deleting addresses
 * - DTO conversions
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Unit Tests")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User testUser;
    private AddressDto testAddressDto;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .username("testuser")
            .firstName("Test")
            .lastName("User")
            .passwordHash("hashed_password")
            .roles(new HashSet<>(Collections.singletonList(Role.CUSTOMER)))
            .isEnabled(true)
            .isEmailVerified(true)
            .build();

        // Create test address DTO
        testAddressDto = AddressDto.builder()
            .street("123 Main Street")
            .city("New York")
            .state("NY")
            .zipCode("10001")
            .country("USA")
            .isDefault(false)
            .build();

        // Create test address entity
        testAddress = Address.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .street("123 Main Street")
            .city("New York")
            .state("NY")
            .zipCode("10001")
            .country("USA")
            .isDefault(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should add address successfully - First address should be set as default")
    void testAddAddressSuccessfully_FirstAddressShouldBeDefault() {
        // Arrange
        when(addressRepository.countByUser(testUser)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        Address result = addressService.addAddress(testUser, testAddressDto);

        // Assert
        assertNotNull(result);
        assertEquals(testAddress.getId(), result.getId());
        assertTrue(result.getIsDefault());
        verify(addressRepository, times(1)).countByUser(testUser);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should add second address without setting as default")
    void testAddAddressSuccessfully_SecondAddressShouldNotBeDefault() {
        // Arrange
        testAddressDto.setIsDefault(false);
        when(addressRepository.countByUser(testUser)).thenReturn(1L);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        Address result = addressService.addAddress(testUser, testAddressDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.getIsDefault());
        verify(addressRepository, times(1)).countByUser(testUser);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should get all addresses for a user in correct order")
    void testGetAddressesByUserSuccessfully() {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(testUser))
            .thenReturn(addresses);

        // Act
        List<Address> result = addressService.getAddressesByUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAddress.getId(), result.get(0).getId());
        verify(addressRepository, times(1)).findByUserOrderByIsDefaultDescCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should return empty list when user has no addresses")
    void testGetAddressesByUserSuccessfully_EmptyList() {
        // Arrange
        when(addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(testUser))
            .thenReturn(Collections.emptyList());

        // Act
        List<Address> result = addressService.getAddressesByUser(testUser);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(addressRepository, times(1)).findByUserOrderByIsDefaultDescCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("Should set address as default and reset previous default")
    void testSetDefaultAddressSuccessfully() {
        // Arrange
        UUID addressId = testAddress.getId();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        // Act
        Address result = addressService.setDefaultAddress(testUser, addressId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsDefault());
        verify(addressRepository, times(1)).resetDefaultAddresses(testUser);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to set non-existent address as default")
    void testSetDefaultAddressThrowsException_AddressNotFound() {
        // Arrange
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> addressService.setDefaultAddress(testUser, addressId));
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName("Should throw exception when address doesn't belong to user")
    void testSetDefaultAddressThrowsException_AddressDoesntBelongToUser() {
        // Arrange
        User otherUser = User.builder()
            .id(UUID.randomUUID())
            .email("other@example.com")
            .username("otheruser")
            .build();

        Address otherUserAddress = Address.builder()
            .id(UUID.randomUUID())
            .user(otherUser)
            .build();

        UUID addressId = otherUserAddress.getId();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(otherUserAddress));

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> addressService.setDefaultAddress(testUser, addressId));
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName("Should delete address successfully")
    void testDeleteAddressSuccessfully() {
        // Arrange
        testAddress.setIsDefault(false);
        when(addressRepository.findById(testAddress.getId())).thenReturn(Optional.of(testAddress));

        // Act
        addressService.deleteAddress(testUser, testAddress.getId());

        // Assert
        verify(addressRepository, times(1)).delete(testAddress);
    }

    @Test
    @DisplayName("Should set another address as default when deleting default address")
    void testDeleteAddressSuccessfully_DefaultAddressReplacement() {
        // Arrange
        testAddress.setIsDefault(true);
        Address secondAddress = Address.builder()
            .id(UUID.randomUUID())
            .user(testUser)
            .street("456 Oak Avenue")
            .city("Los Angeles")
            .state("CA")
            .zipCode("90001")
            .country("USA")
            .isDefault(false)
            .build();

        List<Address> remainingAddresses = Collections.singletonList(secondAddress);
        when(addressRepository.findById(testAddress.getId())).thenReturn(Optional.of(testAddress));
        when(addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(testUser))
            .thenReturn(remainingAddresses);
        when(addressRepository.save(any(Address.class))).thenReturn(secondAddress);

        // Act
        addressService.deleteAddress(testUser, testAddress.getId());

        // Assert
        verify(addressRepository, times(1)).delete(testAddress);
        verify(addressRepository, times(1)).save(any(Address.class));
        assertTrue(secondAddress.getIsDefault());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent address")
    void testDeleteAddressThrowsException_AddressNotFound() {
        // Arrange
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> addressService.deleteAddress(testUser, addressId));
        verify(addressRepository, times(1)).findById(addressId);
    }

    @Test
    @DisplayName("Should throw exception when address doesn't belong to user")
    void testDeleteAddressThrowsException_AddressDoesntBelongToUser() {
        // Arrange
        User otherUser = User.builder()
            .id(UUID.randomUUID())
            .email("other@example.com")
            .username("otheruser")
            .build();

        Address otherUserAddress = Address.builder()
            .id(UUID.randomUUID())
            .user(otherUser)
            .build();

        when(addressRepository.findById(otherUserAddress.getId()))
            .thenReturn(Optional.of(otherUserAddress));

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> addressService.deleteAddress(testUser, otherUserAddress.getId()));
        verify(addressRepository, times(1)).findById(otherUserAddress.getId());
    }

    @Test
    @DisplayName("Should convert Address entity to AddressDto successfully")
    void testConvertToDtoSuccessfully() {
        // Act
        AddressDto result = addressService.convertToDto(testAddress);

        // Assert
        assertNotNull(result);
        assertEquals(testAddress.getId(), result.getId());
        assertEquals(testAddress.getStreet(), result.getStreet());
        assertEquals(testAddress.getCity(), result.getCity());
        assertEquals(testAddress.getState(), result.getState());
        assertEquals(testAddress.getZipCode(), result.getZipCode());
        assertEquals(testAddress.getCountry(), result.getCountry());
        assertEquals(testAddress.getIsDefault(), result.getIsDefault());
    }

    @Test
    @DisplayName("Should convert AddressDto to Address entity successfully")
    void testConvertToEntitySuccessfully() {
        // Act
        Address result = addressService.convertToEntity(testAddressDto, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(testAddressDto.getStreet(), result.getStreet());
        assertEquals(testAddressDto.getCity(), result.getCity());
        assertEquals(testAddressDto.getState(), result.getState());
        assertEquals(testAddressDto.getZipCode(), result.getZipCode());
        assertEquals(testAddressDto.getCountry(), result.getCountry());
    }

    @Test
    @DisplayName("Should get address by ID successfully")
    void testGetAddressByIdSuccessfully() {
        // Arrange
        when(addressRepository.findById(testAddress.getId())).thenReturn(Optional.of(testAddress));

        // Act
        Address result = addressService.getAddressById(testUser, testAddress.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testAddress.getId(), result.getId());
        verify(addressRepository, times(1)).findById(testAddress.getId());
    }

    @Test
    @DisplayName("Should throw exception when address not found")
    void testGetAddressByIdThrowsException_NotFound() {
        // Arrange
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
            () -> addressService.getAddressById(testUser, addressId));
        verify(addressRepository, times(1)).findById(addressId);
    }
}

