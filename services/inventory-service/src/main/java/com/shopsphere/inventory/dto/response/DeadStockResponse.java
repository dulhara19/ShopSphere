package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadStockResponse {

    @JsonProperty("product_id")
    private UUID productId;

    private Long quantity;

    @JsonProperty("last_movement_at")
    private LocalDateTime lastMovementAt;
}
