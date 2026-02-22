package com.shopsphere.analytics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_cohorts", indexes = {
    @Index(name = "idx_cohort_date", columnList = "cohort_date"),
    @Index(name = "idx_user_id_cohort", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDate cohortDate; // Month of registration

    @Column(nullable = false)
    private Integer cohortAge; // Number of months in cohort

    @Column(nullable = false)
    private Long activeUsers;

    @Column(nullable = false)
    @Builder.Default
    private Boolean retained = false;

    @Column(nullable = false)
    @Builder.Default
    private Long createdAt = System.currentTimeMillis();
}
