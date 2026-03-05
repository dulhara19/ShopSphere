package com.shopsphere.user.service.impl;

import com.shopsphere.user.dto.AddressDto;
import com.shopsphere.user.exception.UserNotFoundException;
import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.User;
import com.shopsphere.user.repository.AddressRepository;
import com.shopsphere.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * AddressServiceImpl - Implementation of AddressService
 *
 * Provides business logic for address management including:
 * - Creating and retrieving addresses
 * - Managing default address state
 * - Validation and persistence
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public Address addAddress(User user, AddressDto addressDto) {
        log.info("Adding new address for user: {}", user.getId());

        // If this is the first address or explicitly marked as default, set as default
        boolean isFirstAddress = addressRepository.countByUser(user) == 0;
        boolean shouldBeDefault = isFirstAddress || Boolean.TRUE.equals(addressDto.getIsDefault());

        Address address = convertToEntity(addressDto, user);
        address.setIsDefault(shouldBeDefault);

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with ID: {}", savedAddress.getId());

        return savedAddress;
    }

    @Override
    public List<Address> getAddressesByUser(User user) {
        log.info("Fetching all addresses for user: {}", user.getId());
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
    }

    @Override
    public Address setDefaultAddress(User user, UUID addressId) {
        log.info("Setting address {} as default for user: {}", addressId, user.getId());

        // Verify the address belongs to the user
        Address address = getAddressById(user, addressId);

        // Reset all other default addresses for this user
        addressRepository.resetDefaultAddresses(user);

        // Set the requested address as default
        address.setIsDefault(true);
        Address updatedAddress = addressRepository.save(address);

        log.info("Address {} set as default for user: {}", addressId, user.getId());
        return updatedAddress;
    }

    @Override
    public void deleteAddress(User user, UUID addressId) {
        log.info("Deleting address {} for user: {}", addressId, user.getId());

        Address address = getAddressById(user, addressId);

        // If this is the default address, set another one as default (if exists)
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> remainingAddresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            addressRepository.delete(address);

            // Set the first remaining address as default
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.stream()
                    .filter(a -> !a.getId().equals(addressId))
                    .findFirst()
                    .orElse(null);

                if (newDefault != null) {
                    newDefault.setIsDefault(true);
                    addressRepository.save(newDefault);
                    log.info("Address {} set as new default after deletion", newDefault.getId());
                }
            }
        } else {
            addressRepository.delete(address);
        }

        log.info("Address {} deleted successfully", addressId);
    }

    @Override
    public Address getAddressById(User user, UUID addressId) {
        log.debug("Fetching address {} for user: {}", addressId, user.getId());

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> {
                log.warn("Address {} not found", addressId);
                return new UserNotFoundException("Address not found with ID: " + addressId);
            });

        // Validate that the address belongs to the requested user
        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to access address {} that doesn't belong to them", user.getId(), addressId);
            throw new UserNotFoundException("Address does not belong to the user");
        }

        return address;
    }

    @Override
    public AddressDto convertToDto(Address address) {
        return AddressDto.builder()
            .id(address.getId())
            .street(address.getStreet())
            .city(address.getCity())
            .state(address.getState())
            .zipCode(address.getZipCode())
            .country(address.getCountry())
            .isDefault(address.getIsDefault())
            .build();
    }

    @Override
    public Address convertToEntity(AddressDto addressDto, User user) {
        return Address.builder()
            .user(user)
            .street(addressDto.getStreet())
            .city(addressDto.getCity())
            .state(addressDto.getState())
            .zipCode(addressDto.getZipCode())
            .country(addressDto.getCountry())
            .isDefault(Boolean.FALSE.equals(addressDto.getIsDefault()) ? false : true)
            .build();
    }
}

