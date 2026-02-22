package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_metrics", indexes = {
    @Index(name = "idx_metric_date", columnList = "metric_date"),
    @Index(name = "idx_category_id", columnList = "category_id"),
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_date_range", columnList = "metric_date,category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate metricDate;

    @Column(nullable = false)
    private String granularity; // day, week, month

    @Column
    private String categoryId;

    @Column
    private String productId;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Long totalOrders = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long totalItems = 0L;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal averageOrderValue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long addToCartCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long checkoutCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long purchaseCount = 0L;

    @Column
    private String seller;

    @Column(nullable = false)
    @Builder.Default
    private Boolean processed = false;

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();

    @Column
    private Long updatedAt;
}
