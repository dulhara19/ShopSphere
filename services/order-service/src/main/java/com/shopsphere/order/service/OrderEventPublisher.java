package com.shopsphere.order.service;

import com.shopsphere.order.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    public void publishOrderCreated(Order order) {
        publishEvent("order.created", order, "Order created");
    }

    public void publishOrderConfirmed(Order order) {
        publishEvent("order.confirmed", order, "Order confirmed after payment");
    }

    public void publishOrderShipped(Order order) {
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("trackingNumber", order.getTrackingNumber());
        publishEvent("order.shipped", order, "Order shipped", additionalData);
    }

    public void publishOrderDelivered(Order order) {
        publishEvent("order.delivered", order, "Order delivered");
    }

    public void publishOrderCancelled(Order order) {
        publishEvent("order.cancelled", order, "Order cancelled");
    }

    public void publishCartUpdated(UUID userId, UUID cartId, int itemCount) {
        Map<String, Object> payload = createEventEnvelope(
            "cart.updated",
            Map.of(
                "userId", userId.toString(),
                "cartId", cartId.toString(),
                "itemCount", itemCount
            ),
            "Cart updated"
        );

        try {
            rabbitTemplate.convertAndSend(exchange, "cart.updated", payload);
            log.debug("Published cart.updated event for user {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish cart.updated event: {}", e.getMessage());
        }
    }

    private void publishEvent(String eventType, Order order, String description) {
        publishEvent(eventType, order, description, new HashMap<>());
    }

    private void publishEvent(String eventType, Order order, String description, Map<String, Object> additionalData) {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", order.getId().toString());
        orderData.put("orderNumber", order.getOrderNumber());
        orderData.put("userId", order.getUserId().toString());
        orderData.put("status", order.getStatus().name());
        orderData.put("totalAmount", order.getTotalAmount().toString());
        orderData.putAll(additionalData);

        Map<String, Object> payload = createEventEnvelope(eventType, orderData, description);

        try {
            rabbitTemplate.convertAndSend(exchange, eventType, payload);
            log.info("Published {} event for order {}", eventType, order.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to publish {} event for order {}: {}", eventType, order.getOrderNumber(), e.getMessage());
        }
    }

    private Map<String, Object> createEventEnvelope(String eventType, Map<String, Object> data, String description) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("eventId", UUID.randomUUID().toString());
        envelope.put("eventType", eventType);
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("source", "order-service");
        envelope.put("correlationId", UUID.randomUUID().toString());
        envelope.put("description", description);
        envelope.put("data", data);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("version", "1.0");
        metadata.put("environment", "development");
        envelope.put("metadata", metadata);

        return envelope;
    }
}
