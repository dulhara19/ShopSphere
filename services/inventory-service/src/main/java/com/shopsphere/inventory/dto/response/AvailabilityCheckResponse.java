package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityCheckResponse {

    @JsonProperty("product_id")
    private UUID productId;

    private Long requestedQuantity;

    @JsonProperty("available_quantity")
    private Long availableQuantity;

    private Boolean available;

    private String message;
}
