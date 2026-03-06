package com.shopsphere.inventory.service;

import com.shopsphere.inventory.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StockStreamService stockStreamService;
    private final WebhookService webhookService;

    private static final String STOCK_UPDATED_TOPIC = "stock-updated";
    private static final String STOCK_LOW_TOPIC = "stock-low";
    private static final String STOCK_OUT_TOPIC = "stock-out-of-stock";

    /**
     * Publish stock updated event
     */
    public void publishStockUpdatedEvent(@NonNull Inventory inventory) {
        try {
            UUID productId = requireProductId(inventory);
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "STOCK_UPDATED");
            kafkaTemplate.send(STOCK_UPDATED_TOPIC, requireProductKey(productId), event);
            stockStreamService.publish(event);
            webhookService.dispatchEvent("STOCK_UPDATED", event);
            log.info("Published stock updated event for product: {}", productId);
        } catch (Exception e) {
            log.error("Failed to publish stock updated event", e);
        }
    }

    /**
     * Publish low stock alert event
     */
    public void publishLowStockEvent(@NonNull Inventory inventory) {
        try {
            UUID productId = requireProductId(inventory);
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "LOW_STOCK");
            kafkaTemplate.send(STOCK_LOW_TOPIC, requireProductKey(productId), event);
            stockStreamService.publish(event);
            webhookService.dispatchEvent("LOW_STOCK", event);
            log.info("Published low stock event for product: {}", productId);
        } catch (Exception e) {
            log.error("Failed to publish low stock event", e);
        }
    }

    /**
     * Publish out of stock event
     */
    public void publishOutOfStockEvent(@NonNull Inventory inventory) {
        try {
            UUID productId = requireProductId(inventory);
            Map<String, Object> event = createStockEvent(inventory);
            event.put("eventType", "OUT_OF_STOCK");
            kafkaTemplate.send(STOCK_OUT_TOPIC, requireProductKey(productId), event);
            stockStreamService.publish(event);
            webhookService.dispatchEvent("OUT_OF_STOCK", event);
            log.info("Published out of stock event for product: {}", productId);
        } catch (Exception e) {
            log.error("Failed to publish out of stock event", e);
        }
    }

    /**
     * Create base stock event
     */
    private Map<String, Object> createStockEvent(@NonNull Inventory inventory) {
        Map<String, Object> event = new HashMap<>();
        event.put("productId", inventory.getProductId());
        event.put("quantity", inventory.getQuantity());
        event.put("reservedQuantity", inventory.getReservedQuantity());
        event.put("availableQuantity", inventory.getAvailableQuantity());
        event.put("status", inventory.getStatus().toString());
        event.put("timestamp", LocalDateTime.now());
        return event;
    }

    private UUID requireProductId(Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory");
        return Objects.requireNonNull(inventory.getProductId(), "inventory.productId");
    }

    @NonNull
    private String requireProductKey(UUID productId) {
        String key = Objects.requireNonNull(productId, "productId").toString();
        return Objects.requireNonNull(key, "productKey");
    }
}
