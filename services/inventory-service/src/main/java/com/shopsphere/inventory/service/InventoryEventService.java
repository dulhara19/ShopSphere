package com.shopsphere.inventory.service;

import com.shopsphere.inventory.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String STOCK_UPDATED_TOPIC = "stock-updated";
    private static final String STOCK_LOW_TOPIC = "stock-low";
    private static final String STOCK_OUT_TOPIC = "stock-out-of-stock";

    /**
     * Publish stock updated event
     */
    public void publishStockUpdatedEvent(Inventory inventory) {
        try {
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "STOCK_UPDATED");
            kafkaTemplate.send(STOCK_UPDATED_TOPIC, inventory.getProductId().toString(), event);
            log.info("Published stock updated event for product: {}", inventory.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish stock updated event", e);
        }
    }

    /**
     * Publish low stock alert event
     */
    public void publishLowStockEvent(Inventory inventory) {
        try {
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "LOW_STOCK");
            kafkaTemplate.send(STOCK_LOW_TOPIC, inventory.getProductId().toString(), event);
            log.info("Published low stock event for product: {}", inventory.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish low stock event", e);
        }
    }

    /**
     * Publish out of stock event
     */
    public void publishOutOfStockEvent(Inventory inventory) {
        try {
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "OUT_OF_STOCK");
            kafkaTemplate.send(STOCK_OUT_TOPIC, inventory.getProductId().toString(), event);
            log.info("Published out of stock event for product: {}", inventory.getProductId());
        } catch (Exception e) {
            log.error("Failed to publish out of stock event", e);
        }
    }

    /**
     * Create base stock event
     */
    private Map<String, Object> createStockEvent(Inventory inventory) {
        Map<String, Object> event = new HashMap<>();
        event.put("productId", inventory.getProductId());
        event.put("quantity", inventory.getQuantity());
        event.put("reservedQuantity", inventory.getReservedQuantity());
        event.put("availableQuantity", inventory.getAvailableQuantity());
        event.put("status", inventory.getStatus().toString());
        event.put("timestamp", LocalDateTime.now());
        return event;
    }
}
