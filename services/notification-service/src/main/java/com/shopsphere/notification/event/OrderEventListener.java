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
 * Epic 1.5.1 — Order event listener
 * Consumed events: order.created, order.confirmed, order.shipped,
 * order.delivered
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_EVENT_QUEUE)
    public void handleOrderEvent(Map<String, Object> event) {
        String eventType = (String) event.getOrDefault("eventType", "unknown");
        String userId = (String) event.getOrDefault("userId", "");
        String orderId = (String) event.getOrDefault("orderId", "");

        log.info("📦 Order event received: {} for user: {}", eventType, userId);

        switch (eventType) {
            case "order.created":
                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "order-confirmation", "Order Confirmed",
                        "Your order #" + orderId + " has been placed successfully!",
                        event, "/orders/" + orderId);
                break;
            case "order.shipped":
                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "order-shipped", "Order Shipped",
                        "Your order #" + orderId + " has been shipped!",
                        event, "/orders/" + orderId);
                break;
            case "order.delivered":
                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "order-delivered", "Order Delivered",
                        "Your order #" + orderId + " has been delivered!",
                        event, "/orders/" + orderId);
                break;
            default:
                log.warn("⚠️ Unhandled order event type: {}", eventType);
        }
    }
}
