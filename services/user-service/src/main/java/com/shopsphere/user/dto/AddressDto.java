package com.shopsphere.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * AddressDto - DTO for address API requests and responses
 *
 * Used for:
 * - Creating new addresses
 * - Updating existing addresses
 * - Returning address information in API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    /**
     * Address unique identifier (UUID)
     */
    private UUID id;

    /**
     * Street address line (required)
     */
    @NotBlank(message = "Street address is required")
    @Size(min = 1, max = 255, message = "Street address must be between 1 and 255 characters")
    private String street;

    /**
     * City name (required)
     */
    @NotBlank(message = "City is required")
    @Size(min = 1, max = 100, message = "City must be between 1 and 100 characters")
    private String city;

    /**
     * State or province (required)
     */
    @NotBlank(message = "State is required")
    @Size(min = 1, max = 100, message = "State must be between 1 and 100 characters")
    private String state;

    /**
     * Postal or ZIP code (required)
     */
    @NotBlank(message = "Zip code is required")
    @Size(min = 1, max = 20, message = "Zip code must be between 1 and 20 characters")
    private String zipCode;

    /**
     * Country name (required)
     */
    @NotBlank(message = "Country is required")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
    private String country;

    /**
     * Flag indicating if this is the user's default address
     */
    @Builder.Default
    private Boolean isDefault = false;
}

