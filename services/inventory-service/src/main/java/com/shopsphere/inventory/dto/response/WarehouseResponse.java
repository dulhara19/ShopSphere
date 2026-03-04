package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.Warehouse;
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
public class WarehouseResponse {

    private UUID id;
    private String code;
    private String name;
    private String location;
    private String address;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static WarehouseResponse fromEntity(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .address(warehouse.getAddress())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}
