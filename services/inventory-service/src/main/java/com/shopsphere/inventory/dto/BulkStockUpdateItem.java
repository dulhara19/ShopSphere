package com.shopsphere.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BulkStockUpdateItem {
    @NotBlank
    private String productId;

    @NotNull
    private StockUpdateMode mode;

    @Min(0)
    private int quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

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
