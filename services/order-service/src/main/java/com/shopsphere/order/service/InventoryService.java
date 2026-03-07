package com.shopsphere.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    public boolean reserveInventory(UUID orderId, Map<String, Integer> productQuantities) {
        // In production: Call Inventory Service via HTTP/gRPC to reserve stock
        // For MVP: Mock implementation - always succeeds
        log.info("Reserving inventory for order {}: {} products", orderId, productQuantities.size());

        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            log.debug("Reserving {} units of product {}", entry.getValue(), entry.getKey());
        }

        // Mock: Simulate reservation success
        return true;
    }

    public void releaseInventory(UUID orderId, Map<String, Integer> productQuantities) {
        // In production: Call Inventory Service to release reserved stock (e.g., on order cancellation)
        log.info("Releasing inventory for order {}: {} products", orderId, productQuantities.size());

        for (Map.Entry<UUID, Integer> entry : productQuantities.entrySet()) {
            log.debug("Releasing {} units of product {}", entry.getValue(), entry.getKey());
        }
    }

    public boolean checkAvailability(String productId, int quantity) {
        // In production: Call Inventory Service to check stock
        // For MVP: Mock - always available
        log.debug("Checking availability for product {}: {} units", productId, quantity);
        return true;
    }
}
