package com.shopsphere.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

@Data
public class PurchaseRequest {

    @NotBlank(message = "orderId is required")
    private String orderId;

    private String userId; // optional

    @NotBlank(message = "sessionId is required")
    private String sessionId;

    private Map<String, Object> metadata;
}
