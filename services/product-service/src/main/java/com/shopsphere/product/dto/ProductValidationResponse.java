package com.shopsphere.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductValidationResponse {

    private String productId;
    private boolean exists;
    private boolean active;
}
