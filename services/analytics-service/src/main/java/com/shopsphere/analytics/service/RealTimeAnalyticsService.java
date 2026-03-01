package com.shopsphere.analytics.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.analytics.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealTimeAnalyticsService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void broadcastEvent(Event event) {
        log.debug("Broadcasting event via WebSocket: {}", event.getEventType());

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", event.getEventType());
            payload.put("timestamp", event.getEventTimestamp());
            payload.put("userId", event.getUserId());
            payload.put("productId", event.getProductId());
            payload.put("orderId", event.getOrderId());

            String destination = "/topic/events";

            // Channel routing based on the specific epic requirements
            if (event.getEventType().startsWith("order.")) {
                destination = "/topic/orders";
                messagingTemplate.convertAndSend(destination, payload);
            } else if (event.getEventType().startsWith("user.")) {
                destination = "/topic/users";
            }

            // Broadcast all to a general firehose stream as well
            messagingTemplate.convertAndSend("/topic/events", payload);

            if (event.getEventType().equals("order.completed")) {
                broadcastSalesUpdate(event);
            }

        } catch (Exception e) {
            log.error("Failed to broadcast real-time event", e);
        }
    }

    private void broadcastSalesUpdate(Event event) {
        // Push sales ticks to /topic/sales
        Map<String, Object> salesTick = new HashMap<>();
        salesTick.put("orderId", event.getOrderId());
        salesTick.put("timestamp", event.getEventTimestamp());
        messagingTemplate.convertAndSend("/topic/sales", salesTick);
    }
}
