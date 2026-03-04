package com.shopsphere.product.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryDetailResponseDTO {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private Long productCount;
    private CategoryResponseDTO parentCategory; 
    private List<CategoryResponseDTO> subcategories; 
}