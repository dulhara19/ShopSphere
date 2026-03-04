package com.shopsphere.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;

@Data
public class AddToCartRequest {

    @NotBlank(message = "productId is required")
    private String productId;

    private String userId; // optional

    @NotBlank(message = "sessionId is required")
    private String sessionId;

    @NotNull(message = "quantity is required")
    private Integer quantity;

    private Map<String, Object> metadata;
}
