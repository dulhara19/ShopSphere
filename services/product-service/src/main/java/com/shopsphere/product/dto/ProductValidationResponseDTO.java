package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Story 1.5.3: DTO for product validation across internal services.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductValidationResponseDTO {
    
    // Indicates if all requested product IDs are valid and ACTIVE
    private Boolean allValid;
    
    // Mapping of each product ID to its specific validation status
    private Map<String, Boolean> results;
}