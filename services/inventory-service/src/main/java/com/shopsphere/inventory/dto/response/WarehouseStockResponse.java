package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.WarehouseInventory;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStockResponse {

    @JsonProperty("warehouse_id")
    private UUID warehouseId;

    @JsonProperty("warehouse_code")
    private String warehouseCode;

    @JsonProperty("warehouse_name")
    private String warehouseName;

    private Long quantity;

    public static WarehouseStockResponse fromEntity(WarehouseInventory inventory) {
        return WarehouseStockResponse.builder()
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseCode(inventory.getWarehouse().getCode())
                .warehouseName(inventory.getWarehouse().getName())
                .quantity(inventory.getQuantity())
                .build();
    }
}
