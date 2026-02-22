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
public class ProductMetricDTO {

    private String productId;

    private String categoryId;

    private LocalDate metricDate;

    private Long viewCount;

    private Long uniqueViewers;

    private Long addToCartCount;

    private Long purchaseCount;

    private BigDecimal revenue;

    private Long unitsSold;

    private BigDecimal conversionRate;

    private BigDecimal avgRating;

    private Long reviewCount;

    private String productName;

    private BigDecimal price;
}
