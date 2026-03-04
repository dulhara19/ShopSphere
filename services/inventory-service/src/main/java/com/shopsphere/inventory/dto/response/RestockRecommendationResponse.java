package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestockRecommendationResponse {

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("current_quantity")
    private Long currentQuantity;

    @JsonProperty("avg_daily_sales")
    private Double avgDailySales;

    @JsonProperty("recommended_reorder_quantity")
    private Long recommendedReorderQuantity;

    @JsonProperty("recommended_reorder_date")
    private LocalDate recommendedReorderDate;
}
