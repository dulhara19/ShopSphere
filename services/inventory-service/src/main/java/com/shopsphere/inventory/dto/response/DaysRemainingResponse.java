package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DaysRemainingResponse {

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("current_quantity")
    private Long currentQuantity;

    @JsonProperty("avg_daily_sales")
    private Double avgDailySales;

    @JsonProperty("days_remaining")
    private Double daysRemaining;
}
