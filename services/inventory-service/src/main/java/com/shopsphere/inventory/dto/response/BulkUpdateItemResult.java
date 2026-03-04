package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.dto.InventoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkUpdateItemResult {

    @JsonProperty("product_id")
    private UUID productId;

    private Boolean success;

    private InventoryDTO inventory;

    private String error;
}
