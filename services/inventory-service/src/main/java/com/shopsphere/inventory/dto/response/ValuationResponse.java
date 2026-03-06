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
public class ValuationResponse {

    @JsonProperty("product_id")
    private UUID productId;

    private Long quantity;

    @JsonProperty("unit_cost")
    private Double unitCost;

    private Double value;
}
