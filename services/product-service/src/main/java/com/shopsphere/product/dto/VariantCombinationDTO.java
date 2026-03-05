package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantCombinationDTO {
    private String sku;
    // Holds the specific combo, e.g., {"Size": "S", "Color": "Red"}
    private Map<String, String> attributes; 
    private Double price;
    // Used by the frontend to disable the "Add to Cart" button if false
    private boolean inStock; 
}