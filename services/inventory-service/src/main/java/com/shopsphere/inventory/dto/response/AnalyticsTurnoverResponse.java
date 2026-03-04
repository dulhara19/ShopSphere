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
public class AnalyticsTurnoverResponse {

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("sales_quantity")
    private Long salesQuantity;

    @JsonProperty("average_inventory")
    private Double averageInventory;

    @JsonProperty("turnover_rate")
    private Double turnoverRate;
}
