package com.shopsphere.inventory.service;

import com.shopsphere.inventory.entity.Inventory;

import java.util.Optional;

public interface InventoryService {
    Optional<Inventory> getByProductId(String productId);
}
