package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_metrics", indexes = {
    @Index(name = "idx_pm_product_id", columnList = "product_id"),
    @Index(name = "idx_pm_metric_date", columnList = "metric_date"),
    @Index(name = "idx_pm_category_id", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private LocalDate metricDate;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long uniqueViewers = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long addToCartCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long purchaseCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal revenue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Long unitsSold = 0L;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal conversionRate = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Long reviewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();

    @Column
    private Long updatedAt;
}
