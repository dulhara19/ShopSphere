package com.shopsphere.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Story 2.2.1: Define variation attributes
 * Admin can create global attributes like Size, Color, Material
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "variation_attributes")
public class VariationAttribute {
    @Id
    private String id;
    
    // Name of the attribute (e.g., "Size", "Color")
    private String name; 
    
    private String description;
    
    // Default options available (e.g., ["S", "M", "L", "XL"] or ["Red", "Blue", "Green"])
    private List<String> defaultValues; 
}