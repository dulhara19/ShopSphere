package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "exports", indexes = {
    @Index(name = "idx_export_status", columnList = "status"),
    @Index(name = "idx_export_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Export {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String exportId; // Unique export identifier

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String type; // SALES, PRODUCTS, USERS, ORDERS

    @Column(nullable = false)
    private String format; // CSV, EXCEL

    @Column(nullable = false)
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

    @Column
    private LocalDate dateFrom;

    @Column
    private LocalDate dateTo;

    @Column
    private String filePath;

    @Column
    private String errorMessage;

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();

    @Column
    private Long completedAt;

    @Column
    private String filters; // JSON string of filters applied

    @Column
    private Long scheduledAt;

    @Column
    @Builder.Default
    private Boolean recurring = false;
}
