package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductValidationResponseDTO {
    private boolean allValid; 
    private Map<String, Boolean> results; 
}