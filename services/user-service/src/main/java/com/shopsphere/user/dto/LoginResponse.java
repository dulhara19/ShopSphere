package com.shopsphere.user.dto;

import com.shopsphere.user.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * LoginResponse DTO - Response object after successful user login.
 *
 * Returns user details and JWT tokens after authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * JWT access token (15 minute expiration)
     */
    private String accessToken;

    /**
     * JWT refresh token (7 day expiration)
     */
    private String refreshToken;

    /**
     * Token type (e.g., "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";

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
     * User's roles
     */
    private Set<Role> roles;

    /**
     * Token expiration time (in milliseconds)
     */
    private Long expiresIn;
}

