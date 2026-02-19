package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Internal API endpoints for other microservices
 * Epic 1.4: Internal Service Communication
 */
@RestController
@RequestMapping("/internal/inventory")
@RequiredArgsConstructor
@Slf4j
public class InternalInventoryController {

    private final InventoryService inventoryService;

    /**
     * Epic 1.4.1: Get stock for product (internal)
     * Used by Product Service for product display
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDTO> getStockForProduct(@PathVariable UUID productId) {
        log.debug("Internal Request - GET /internal/inventory/{} - Getting stock", productId);
        InventoryDTO inventory = inventoryService.getStockLevel(productId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Epic 1.4.2: Batch get stock levels
     * For cart/order display - Get stock for multiple products at once
     */
    @PostMapping("/batch")
    public ResponseEntity<List<InventoryDTO>> getStockForMultipleProducts(@RequestBody List<UUID> productIds) {
        log.debug("Internal Request - POST /internal/inventory/batch - Getting stock for {} products", productIds.size());
        List<InventoryDTO> inventories = inventoryService.getInventoryForProducts(productIds);
        return ResponseEntity.ok(inventories);
    }

    /**
     * Check if a product has available stock (for order service)
     */
    @GetMapping("/{productId}/available/{quantity}")
    public ResponseEntity<Boolean> hasAvailableStock(@PathVariable UUID productId, @PathVariable Long quantity) {
        log.debug("Internal Request - GET /internal/inventory/{}/available/{}", productId, quantity);
        try {
            boolean hasStock = inventoryService.hasStock(productId, quantity);
            return ResponseEntity.ok(hasStock);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
