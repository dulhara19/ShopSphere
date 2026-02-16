package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.entity.Inventory;
import com.shopsphere.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/{productId}")
    public Optional<Inventory> getInventory(@PathVariable String productId) {
        return service.getByProductId(productId);
    }
}
