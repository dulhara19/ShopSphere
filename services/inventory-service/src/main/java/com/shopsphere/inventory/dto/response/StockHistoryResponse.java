package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.StockMovementLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistoryResponse {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    @JsonProperty("change_type")
    private StockMovementLog.ChangeType changeType;

    @JsonProperty("quantity_before")
    private Long quantityBefore;

    @JsonProperty("quantity_after")
    private Long quantityAfter;

    @JsonProperty("quantity_change")
    private Long quantityChange;

    private String reason;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("reference_id")
    private UUID referenceId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static StockHistoryResponse fromEntity(StockMovementLog log) {
        return StockHistoryResponse.builder()
                .id(log.getId())
                .productId(log.getProductId())
                .changeType(log.getChangeType())
                .quantityBefore(log.getQuantityBefore())
                .quantityAfter(log.getQuantityAfter())
                .quantityChange(log.getQuantityChange())
                .reason(log.getReason())
                .userId(log.getUserId())
                .referenceId(log.getReferenceId())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
