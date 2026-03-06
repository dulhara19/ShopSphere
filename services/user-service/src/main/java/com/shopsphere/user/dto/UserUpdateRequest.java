package com.shopsphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

/**
 * UserUpdateRequest DTO - Request object for updating user profile
 *
 * Handles incoming user profile update data with validation.
 * Only allows updating: firstName, lastName, phone, address, city, state, postalCode, country
 * Protected fields: id, email, password, roles (cannot be updated via this endpoint)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    /**
     * User's first name (optional)
     */
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * User's last name (optional)
     */
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * User's phone number (optional)
     */
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    /**
     * User's residential address (optional)
     */
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    /**
     * City of residence (optional)
     */
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    /**
     * State/Province of residence (optional)
     */
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    /**
     * Postal code (optional)
     */
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    /**
     * Country of residence (optional)
     */
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;
}

