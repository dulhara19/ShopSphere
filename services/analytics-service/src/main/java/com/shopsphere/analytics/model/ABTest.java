package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ab_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private String description;

    private String metricToTrack; // e.g. "CONVERSION_RATE", "CLICK_THROUGH_RATE"

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String status; // RUNNING, CONCLUDED, DRAFT

    @Column(columnDefinition = "TEXT")
    private String variants; // JSON array of variant definitions e.g. ["A", "B", "C"]

    private String winnerVariant;
    private Double confidenceLevel; // e.g., 95.0
}
