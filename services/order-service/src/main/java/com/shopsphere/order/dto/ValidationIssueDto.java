package com.shopsphere.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationIssueDto {

    private UUID productId;
    private String productName;
    private IssueType issue;
    private Integer currentStock;
    private Integer requestedQuantity;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;

    public enum IssueType {
        OUT_OF_STOCK,
        INSUFFICIENT_STOCK,
        PRICE_CHANGED,
        PRODUCT_UNAVAILABLE
    }
}
