package com.shopsphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * RefreshTokenRequest DTO - Request object for token refresh endpoint.
 *
 * Contains the refresh token that was previously issued during login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    /**
     * JWT refresh token (obtained from login response)
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

