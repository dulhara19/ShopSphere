package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "seller_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sellerId;

    @Column(nullable = false)
    private LocalDate metricDate;

    // Sales Performance
    private Long totalOrders;
    private Long totalItemsSold;
    private BigDecimal grossRevenue;

    // Quality & Satisfaction
    private Double averageRating;
    private Long openDisputes;
    private Long returns;

    // Financials
    private BigDecimal commissionOwed;
    private BigDecimal netPayout;
}
