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
 * Epic 1.5.5 — Review event listener
 * Consumed events: review.created
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.REVIEW_EVENT_QUEUE)
    public void handleReviewEvent(Map<String, Object> event) {
        String eventType = (String) event.getOrDefault("eventType", "unknown");
        String sellerId = (String) event.getOrDefault("sellerId", "");
        String productName = (String) event.getOrDefault("productName", "Unknown Product");
        Object rating = event.getOrDefault("rating", "N/A");

        log.info("⭐ Review event received: {} for seller: {}", eventType, sellerId);

        if ("review.created".equals(eventType)) {
            notificationService.sendMultiChannel(sellerId, List.of("IN_APP"),
                    "review-reminder", "New Review on Your Product",
                    "Your product \"" + productName + "\" received a " + rating + "-star review.",
                    event, "/seller/reviews");
        }
    }
}
