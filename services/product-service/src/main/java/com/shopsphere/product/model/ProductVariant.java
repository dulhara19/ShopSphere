package com.shopsphere.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;
import java.util.Map;

/**
 * Story 2.2.2: Product Variant Model
 * Represents a specific variation of a parent product (e.g., Red-Large)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariant {
    
    // Each variant must have its own unique SKU
    @Field(type = FieldType.Keyword)
    private String sku; 
    
    // Specific attributes for this variant (e.g., {"Size": "L", "Color": "Red"})
    private Map<String, String> attributes;
    
    // Variant specific price (Can be different from the base product price)
    @Field(type = FieldType.Double)
    private Double price;
    
    // Variant specific images (e.g., Pictures of the red t-shirt)
    private List<String> images;
    
    // Story 2.2.3: Preparing for inventory tracking
    private Integer stockQuantity;
    
    private boolean isAvailable;
}