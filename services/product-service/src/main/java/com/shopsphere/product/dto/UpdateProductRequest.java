package com.shopsphere.product.dto;

import com.shopsphere.product.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private String categoryId;
    private List<String> images;
    private String primaryImage;
    private ProductStatus status;
    private String sku;
    private String brand;
}
