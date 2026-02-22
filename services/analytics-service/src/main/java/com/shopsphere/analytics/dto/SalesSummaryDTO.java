package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSummaryDTO {

    private BigDecimal totalRevenue;

    private Long totalOrders;

    private BigDecimal averageOrderValue;

    private Long totalItems;

    private ComparisonPeriodDTO comparisonPeriod;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComparisonPeriodDTO {
        private String revenueChange;
        private String ordersChange;
        private String itemsChange;
        private BigDecimal previousPeriodRevenue;
        private Long previousPeriodOrders;
    }
}
