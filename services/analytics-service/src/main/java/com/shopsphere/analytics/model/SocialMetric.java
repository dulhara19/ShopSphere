package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "social_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate metricDate;

    // Review metrics
    private Long totalReviews;
    private Double averageRating;
    private Long positiveSentiments;
    private Long negativeSentiments;

    // Social Post & Influencer Metrics
    private String influencerId;
    private String resourceId; // E.g., Post ID, Product ID

    private Long likes;
    private Long shares;
    private Long comments;

    private Long attributedSales;
}
