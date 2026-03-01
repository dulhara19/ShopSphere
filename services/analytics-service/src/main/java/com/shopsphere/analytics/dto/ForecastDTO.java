package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDTO {

    private String metric; // e.g. "revenue" or "sales"
    private int forecastPeriodDays;
    private List<ForecastDataPoint> historicalData;
    private List<ForecastDataPoint> predictedData;
    private BigDecimal expectedTotal;
    private BigDecimal expectedLowerBound;
    private BigDecimal expectedUpperBound;
    private Double confidenceScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastDataPoint {
        private LocalDate date;
        private BigDecimal value;
        private BigDecimal lowerBound;
        private BigDecimal upperBound;
    }
}
