package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInternalResponseDTO {
    private String id;
    private String name;
    private Double price;
    private String status; // active, deleted, etc.
    private Integer stockQuantity; 
    private Boolean isAvailable;
}