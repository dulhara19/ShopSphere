package com.shopsphere.notification.event;

import com.shopsphere.notification.config.RabbitMQConfig;
import com.shopsphere.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Epic 1.5.4 — Inventory event listener
 * Consumed events: inventory.low
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InventoryEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_EVENT_QUEUE)
    public void handleInventoryEvent(Map<String, Object> event) {
        String eventType = (String) event.getOrDefault("eventType", "unknown");
        String sellerId = (String) event.getOrDefault("sellerId", "");
        String productName = (String) event.getOrDefault("productName", "Unknown Product");
        Object stockObj = event.getOrDefault("currentStock", 0);

        log.info("📦 Inventory event received: {} for seller: {}", eventType, sellerId);

        if ("inventory.low".equals(eventType)) {
            notificationService.sendMultiChannel(sellerId, List.of("EMAIL", "IN_APP"),
                    "low-stock-alert", "Low Stock Alert",
                    "Your product \"" + productName + "\" is running low on stock (" + stockObj + " remaining).",
                    event, "/seller/inventory");
        }
    }
}
