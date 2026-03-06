package com.shopsphere.inventory.exception;

public class InsufficientStockException extends InventoryException {
    public InsufficientStockException(String productId, Long requested, Long available) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d", 
                productId, requested, available));
    }
}
