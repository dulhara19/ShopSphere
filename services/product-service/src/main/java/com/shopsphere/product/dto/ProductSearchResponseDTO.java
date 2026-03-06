package com.shopsphere.product.dto;

import com.shopsphere.product.model.Product;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@Builder
public class ProductSearchResponseDTO {
    private Page<Product> products;
    private Map<String, Long> categoryFacets;
    private Map<String, Long> brandFacets;
}