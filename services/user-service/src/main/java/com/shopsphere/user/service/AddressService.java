package com.shopsphere.user.service;

import com.shopsphere.user.dto.AddressDto;
import com.shopsphere.user.model.Address;
import com.shopsphere.user.model.User;

import java.util.List;
import java.util.UUID;

/**
 * AddressService Interface - Business logic for address management
 *
 * Responsibilities:
 * - Add new addresses for users
 * - Retrieve addresses for a user
 * - Set default address (ensuring only one per user)
 * - Delete addresses
 */
public interface AddressService {

    /**
     * Add a new address for the authenticated user
     *
     * @param user the user entity
     * @param addressDto the address data transfer object
     * @return the created Address
     */
    Address addAddress(User user, AddressDto addressDto);

    /**
     * Get all addresses for a specific user
     *
     * @param user the user entity
     * @return list of addresses for the user
     */
    List<Address> getAddressesByUser(User user);

    /**
     * Set an address as the default address for a user
     * Ensures only one default address per user
     *
     * @param user the user entity
     * @param addressId the address ID to set as default
     * @return the updated Address
     */
    Address setDefaultAddress(User user, UUID addressId);

    /**
     * Delete an address by ID (belongs to user)
     *
     * @param user the user entity
     * @param addressId the address ID to delete
     */
    void deleteAddress(User user, UUID addressId);

    /**
     * Get an address by ID (with user ownership validation)
     *
     * @param user the user entity
     * @param addressId the address ID
     * @return the Address if found and belongs to the user
     */
    Address getAddressById(User user, UUID addressId);

    /**
     * Convert Address entity to AddressDto
     *
     * @param address the address entity
     * @return the address DTO
     */
    AddressDto convertToDto(Address address);

    /**
     * Convert AddressDto to Address entity
     *
     * @param addressDto the address DTO
     * @param user the user entity
     * @return the address entity
     */
    Address convertToEntity(AddressDto addressDto, User user);
}

