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
 * Epic 1.5.2 — Payment event listener
 * Consumed events: payment.succeeded, payment.failed
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_EVENT_QUEUE)
    public void handlePaymentEvent(Map<String, Object> event) {
        String eventType = (String) event.getOrDefault("eventType", "unknown");
        String userId = (String) event.getOrDefault("userId", "");
        String orderId = (String) event.getOrDefault("orderId", "");

        log.info("💳 Payment event received: {} for user: {}", eventType, userId);

        switch (eventType) {
            case "payment.succeeded":
                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "payment-receipt", "Payment Successful",
                        "Payment for order #" + orderId + " was successful.",
                        event, "/orders/" + orderId);
                break;
            case "payment.failed":
                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "payment-failed", "Payment Failed",
                        "Payment for order #" + orderId + " has failed. Please update your payment method.",
                        event, "/orders/" + orderId + "/payment");
                break;
            default:
                log.warn("⚠️ Unhandled payment event type: {}", eventType);
        }
    }
}
