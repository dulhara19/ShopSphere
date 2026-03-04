package com.shopsphere.product.dto;

import lombok.Data;
import java.util.List;

@Data
public class CategoryResponseDTO {
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private Long productCount; 
    private List<CategoryResponseDTO> subcategories; 
}