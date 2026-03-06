package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private SalesDashboardDTO sales;

    private OrdersDashboardDTO orders;

    private UsersDashboardDTO users;

    private ProductsDashboardDTO products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SalesDashboardDTO {
        private BigDecimal today;
        private BigDecimal thisWeek;
        private BigDecimal thisMonth;
        private String change;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrdersDashboardDTO {
        private Long pending;
        private Long processing;
        private Long shipped;
        private Long delivered;
        private Long cancelled;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsersDashboardDTO {
        private Long newUsers;
        private Long activeUsers;
        private Long totalUsers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductsDashboardDTO {
        private List<TopProductDTO> topSelling;
        private Long lowStockCount;
        private Long outOfStockCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopProductDTO {
        private String productId;
        private String productName;
        private Long unitsSold;
        private BigDecimal revenue;
    }
}
