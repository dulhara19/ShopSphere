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
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String LOW_STOCK_NOTIFICATION_TOPIC = "low-stock-notification";
    private static final String OUT_OF_STOCK_NOTIFICATION_TOPIC = "out-of-stock-notification";

    /**
     * Send low stock alert to the Notification Service
     */
    public void sendLowStockAlert(Inventory inventory) {
        try {
            UUID productId = requireProductId(inventory);
            Map<String, Object> notification = createNotification(inventory, "LOW_STOCK");
            kafkaTemplate.send(LOW_STOCK_NOTIFICATION_TOPIC, requireProductKey(productId), notification);
            log.info("Low stock notification sent for product: {}", productId);
        } catch (Exception e) {
            log.error("Failed to send low stock notification", e);
        }
    }

    /**
     * Send out of stock alert to the Notification Service
     */
    public void sendOutOfStockAlert(Inventory inventory) {
        try {
            UUID productId = requireProductId(inventory);
            Map<String, Object> notification = createNotification(inventory, "OUT_OF_STOCK");
            kafkaTemplate.send(OUT_OF_STOCK_NOTIFICATION_TOPIC, requireProductKey(productId), notification);
            log.info("Out of stock notification sent for product: {}", productId);
        } catch (Exception e) {
            log.error("Failed to send out of stock notification", e);
        }
    }

    /**
     * Create notification structure
     */
    private Map<String, Object> createNotification(Inventory inventory, String type) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("productId", inventory.getProductId());
        notification.put("notificationType", type);
        notification.put("quantity", inventory.getQuantity());
        notification.put("lowStockThreshold", inventory.getLowStockThreshold());
        notification.put("availableQuantity", inventory.getAvailableQuantity());
        notification.put("timestamp", LocalDateTime.now());
        return notification;
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
