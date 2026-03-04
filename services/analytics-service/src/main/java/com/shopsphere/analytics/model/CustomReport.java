package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportName;

    @Column(nullable = false)
    private String authorUserId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String configurationJson; // Stores dimensions, metrics, filters

    private boolean isScheduled;
    private String scheduleCron;
    private String emailRecipients;

    private LocalDateTime createdAt;
    private LocalDateTime lastRunAt;
}
