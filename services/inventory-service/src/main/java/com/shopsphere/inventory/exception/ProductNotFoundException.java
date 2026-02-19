package com.shopsphere.inventory.exception;

public class ProductNotFoundException extends InventoryException {
    public ProductNotFoundException(String productId) {
        super("Inventory not found for product: " + productId);
    }
}
