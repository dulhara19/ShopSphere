package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesMetricDTO {

    private LocalDate metricDate;

    private String granularity;

    private String categoryId;

    private String productId;

    private BigDecimal totalRevenue;

    private Long totalOrders;

    private Long totalItems;

    private BigDecimal averageOrderValue;

    private Long viewCount;

    private Long addToCartCount;

    private Long checkoutCount;

    private Long purchaseCount;

    private String seller;
}
