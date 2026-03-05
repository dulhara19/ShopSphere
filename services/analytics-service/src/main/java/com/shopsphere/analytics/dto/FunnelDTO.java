package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunnelDTO {

    private List<FunnelStepDTO> steps;
    private BigDecimal overallConversionRate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FunnelStepDTO {
        private String stepName;
        private Long count;
        private BigDecimal conversionRate;
    }
}
