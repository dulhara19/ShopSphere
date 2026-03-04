package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_metrics", indexes = {
    @Index(name = "idx_um_user_id", columnList = "user_id"),
    @Index(name = "idx_um_metric_date", columnList = "metric_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDate metricDate;

    @Column(nullable = false)
    @Builder.Default
    private Long totalPurchases = 0L;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal lifetimeValue = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Long lastPurchaseDay = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long sessionCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long pageViewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = false;

    @Column
    private String segment; // High-value, Regular, At-risk, Churned

    @Column
    private String activityLevel; // High, Medium, Low, Inactive

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();

    @Column
    private Long updatedAt;

    @Column
    private LocalDate registrationDate;

    @Column
    private LocalDate lastActivityDate;
}
