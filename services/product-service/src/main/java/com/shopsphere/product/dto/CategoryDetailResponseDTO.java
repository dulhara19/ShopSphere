package com.shopsphere.product.dto;

import lombok.Data;
import java.util.List;

/**
 * Story 1.2.2 & 1.3.2: Detailed Response DTO for Category
 * This includes hierarchical information like parent and child categories.
 */
@Data
public class CategoryDetailResponseDTO {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private Long productCount;
    
    // Story 1.3.2: Parent category information
    private CategoryResponseDTO parentCategory; 
    
    // Story 1.3.2: List of child categories (Subcategories)
    private List<CategoryResponseDTO> subcategories; 
}