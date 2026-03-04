package com.shopsphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * RegisterResponse DTO - Response object after successful user registration.
 *
 * Returns user details after successful account creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    /**
     * Unique user ID (UUID)
     */
    private UUID userId;

    /**
     * User's email address
     */
    private String email;

    /**
     * User's first name
     */
    private String firstName;

    /**
     * User's last name
     */
    private String lastName;

    /**
     * Success message
     */
    private String message;
}

