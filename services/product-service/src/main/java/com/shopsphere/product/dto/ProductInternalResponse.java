package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInternalResponse {

    private String id;
    private String name;
    private BigDecimal price;
    private String primaryImage;
    private String sellerId;
    private String status;
}
