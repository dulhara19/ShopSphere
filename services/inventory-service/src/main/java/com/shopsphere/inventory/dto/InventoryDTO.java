package com.shopsphere.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.Inventory;
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
public class InventoryDTO {

    private UUID id;

    @JsonProperty("product_id")
    private UUID productId;

    private Long quantity;

    @JsonProperty("reserved_quantity")
    private Long reservedQuantity;

    @JsonProperty("available_quantity")
    private Long availableQuantity;

    @JsonProperty("low_stock_threshold")
    private Long lowStockThreshold;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_updated")
    private LocalDateTime lastUpdated;

    public static InventoryDTO fromEntity(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .status(inventory.getStatus().toString())
                .createdAt(inventory.getCreatedAt())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }
}
