package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricDTO {

    private String userId;

    private LocalDate metricDate;

    private Long totalPurchases;

    private BigDecimal lifetimeValue;

    private Long lastPurchaseDay;

    private Long sessionCount;

    private Long pageViewCount;

    private Boolean active;

    private String segment;

    private String activityLevel;

    private LocalDate registrationDate;

    private LocalDate lastActivityDate;
}
