package com.shopsphere.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInventoryRequest {

    @NotNull(message = "Product ID is required")
    @JsonProperty("product_id")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Long quantity;

    @Min(value = 0, message = "Low stock threshold must be non-negative")
    @JsonProperty("low_stock_threshold")
    private Long lowStockThreshold;
}
