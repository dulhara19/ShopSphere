package com.shopsphere.user.dto;

import com.shopsphere.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * UserResponse DTO - Response object for user profile operations
 *
 * Returns user details without sensitive information (no password hash).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /**
     * User's unique identifier (UUID)
     */
    private UUID id;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's username
     */
    private String username;

    /**
     * User's first name
     */
    private String firstName;

    /**
     * User's last name
     */
    private String lastName;

    /**
     * User's phone number
     */
    private String phone;

    /**
     * User's residential address
     */
    private String address;

    /**
     * City of residence
     */
    private String city;

    /**
     * State/Province of residence
     */
    private String state;

    /**
     * Postal code
     */
    private String postalCode;

    /**
     * Country of residence
     */
    private String country;

    /**
     * User's profile picture URL
     */
    private String profilePictureUrl;

    /**
     * Set of roles assigned to user
     */
    private Set<Role> roles;

    /**
     * Flag indicating if user account is enabled
     */
    private Boolean isEnabled;

    /**
     * Flag indicating if email has been verified
     */
    private Boolean isEmailVerified;

    /**
     * Timestamp when account was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when account was last updated
     */
    private LocalDateTime updatedAt;

    /**
     * Success/info message (optional)
     */
    private String message;
}

