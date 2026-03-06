package com.shopsphere.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.StockMovementLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAdjustmentRequest {

    @NotNull(message = "Quantity change is required")
    @JsonProperty("quantity_change")
    private Long quantityChange;

    @NotNull(message = "Change type is required")
    @JsonProperty("change_type")
    private StockMovementLog.ChangeType changeType;

    private String reason;

    @JsonProperty("user_id")
    private UUID userId;
}
