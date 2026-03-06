package com.shopsphere.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.user.dto.AddressDto;
import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.Role;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.AddressRepository;
import com.shopsphere.user.repository.UserRepository;
import com.shopsphere.user.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for AddressController
 *
 * Tests cover:
 * - Creating addresses
 * - Retrieving addresses
 * - Setting default addresses
 * - Deleting addresses
 * - Security and authorization
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AddressController Integration Tests")
@Transactional
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    private User testUser;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        // Clean up
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("address-test@example.com")
            .username("addresstester")
            .firstName("Address")
            .lastName("Tester")
            .passwordHash("hashed_password")
            .roles(new HashSet<>(Collections.singletonList(Role.CUSTOMER)))
            .isEnabled(true)
            .isEmailVerified(true)
            .build();

        userRepository.save(testUser);

        // Create test address DTO
        testAddressDto = AddressDto.builder()
            .street("123 Main Street")
            .city("New York")
            .state("NY")
            .zipCode("10001")
            .country("USA")
            .isDefault(false)
            .build();
    }

    @Test
    @DisplayName("Should return 401 when adding address without authentication")
    void testAddAddressUnauthorized() throws Exception {
        mockMvc.perform(post("/api/users/me/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddressDto))
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should add address successfully with authentication")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testAddAddressSuccessfully() throws Exception {
        mockMvc.perform(post("/api/users/me/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAddressDto))
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.street", equalTo("123 Main Street")))
            .andExpect(jsonPath("$.city", equalTo("New York")))
            .andExpect(jsonPath("$.state", equalTo("NY")))
            .andExpect(jsonPath("$.zipCode", equalTo("10001")))
            .andExpect(jsonPath("$.country", equalTo("USA")))
            .andExpect(jsonPath("$.isDefault", equalTo(true))); // First address should be default
    }

    @Test
    @DisplayName("Should return 400 when adding address with missing required field")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testAddAddressValidationError() throws Exception {
        AddressDto invalidAddressDto = AddressDto.builder()
            .city("New York")
            .state("NY")
            .zipCode("10001")
            .country("USA")
            .build();

        mockMvc.perform(post("/api/users/me/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddressDto))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should retrieve all addresses for authenticated user")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testGetAllAddressesSuccessfully() throws Exception {
        // Create multiple addresses
        addressService.addAddress(testUser, testAddressDto);

        AddressDto secondAddressDto = AddressDto.builder()
            .street("456 Oak Avenue")
            .city("Los Angeles")
            .state("CA")
            .zipCode("90001")
            .country("USA")
            .isDefault(false)
            .build();

        addressService.addAddress(testUser, secondAddressDto);

        // Retrieve all addresses
        mockMvc.perform(get("/api/users/me/addresses")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].street", equalTo("123 Main Street")))
            .andExpect(jsonPath("$[0].isDefault", equalTo(true)))
            .andExpect(jsonPath("$[1].street", equalTo("456 Oak Avenue")))
            .andExpect(jsonPath("$[1].isDefault", equalTo(false)));
    }

    @Test
    @DisplayName("Should return 401 when retrieving addresses without authentication")
    void testGetAllAddressesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me/addresses")
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should set address as default successfully")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testSetDefaultAddressSuccessfully() throws Exception {
        // Create two addresses
        Address firstAddress = addressService.addAddress(testUser, testAddressDto);

        AddressDto secondAddressDto = AddressDto.builder()
            .street("456 Oak Avenue")
            .city("Los Angeles")
            .state("CA")
            .zipCode("90001")
            .country("USA")
            .isDefault(false)
            .build();

        Address secondAddress = addressService.addAddress(testUser, secondAddressDto);

        // Set second address as default
        mockMvc.perform(put("/api/users/me/addresses/{id}/default", secondAddress.getId())
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", equalTo(secondAddress.getId().toString())))
            .andExpect(jsonPath("$.isDefault", equalTo(true)));

        // Verify that first address is no longer default
        mockMvc.perform(get("/api/users/me/addresses")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", equalTo(secondAddress.getId().toString())))
            .andExpect(jsonPath("$[0].isDefault", equalTo(true)))
            .andExpect(jsonPath("$[1].id", equalTo(firstAddress.getId().toString())))
            .andExpect(jsonPath("$[1].isDefault", equalTo(false)));
    }

    @Test
    @DisplayName("Should return 404 when setting non-existent address as default")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testSetDefaultAddressNotFound() throws Exception {
        mockMvc.perform(put("/api/users/me/addresses/{id}/default", UUID.randomUUID())
                .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete address successfully")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testDeleteAddressSuccessfully() throws Exception {
        // Create address
        Address address = addressService.addAddress(testUser, testAddressDto);

        // Delete address
        mockMvc.perform(delete("/api/users/me/addresses/{id}", address.getId())
                .with(csrf()))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/users/me/addresses")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent address")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testDeleteAddressNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/me/addresses/{id}", UUID.randomUUID())
                .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when deleting address without authentication")
    void testDeleteAddressUnauthorized() throws Exception {
        Address address = addressService.addAddress(testUser, testAddressDto);

        mockMvc.perform(delete("/api/users/me/addresses/{id}", address.getId())
                .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should prevent user from accessing another user's address")
    @WithMockUser(username = "address-test@example.com", roles = {"CUSTOMER"})
    void testCannotAccessAnotherUserAddress() throws Exception {
        // Create another user
        User otherUser = User.builder()
            .id(UUID.randomUUID())
            .email("other@example.com")
            .username("otheruser")
            .firstName("Other")
            .lastName("User")
            .passwordHash("hashed_password")
            .roles(new HashSet<>(Collections.singletonList(Role.CUSTOMER)))
            .isEnabled(true)
            .isEmailVerified(true)
            .build();

        userRepository.save(otherUser);

        // Create address for other user
        Address otherUserAddress = addressService.addAddress(otherUser, testAddressDto);

        // Try to delete other user's address
        mockMvc.perform(delete("/api/users/me/addresses/{id}", otherUserAddress.getId())
                .with(csrf()))
            .andExpect(status().isNotFound());
    }
}

