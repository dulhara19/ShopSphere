package com.shopsphere.inventory.service.impl;

import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Inventory> getByProductId(String productId) {
        return repository.findByProductId(productId);
    }
}
