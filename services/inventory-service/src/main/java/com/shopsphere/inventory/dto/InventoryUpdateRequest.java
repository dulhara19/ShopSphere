package com.shopsphere.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryUpdateRequest {
    @NotNull
    private StockUpdateMode mode;

    @Min(0)
    private int quantity;

    public StockUpdateMode getMode() {
        return mode;
    }

    public void setMode(StockUpdateMode mode) {
        this.mode = mode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
