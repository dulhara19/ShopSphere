package com.shopsphere.notification.event;

import com.shopsphere.notification.config.RabbitMQConfig;
import com.shopsphere.notification.service.NotificationService;
import com.shopsphere.notification.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Epic 1.5.3 — User event listener
 * Consumed events: user.registered, user.password-reset-requested
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {

    private final NotificationService notificationService;
    private final PreferenceService preferenceService;

    @RabbitListener(queues = RabbitMQConfig.USER_EVENT_QUEUE)
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.getOrDefault("eventType", "unknown");
        String userId = (String) event.getOrDefault("userId", "");

        log.info("👤 User event received: {} for user: {}", eventType, userId);

        switch (eventType) {
            case "user.registered":
                // Create default preferences for new user (Epic 1.4.3)
                preferenceService.getPreferences(userId);

                notificationService.sendMultiChannel(userId, List.of("EMAIL", "IN_APP"),
                        "welcome", "Welcome to ShopSphere!",
                        "Welcome to ShopSphere! Start exploring amazing deals.",
                        event, "/");
                break;
            case "user.password-reset-requested":
                notificationService.sendMultiChannel(userId, List.of("EMAIL"),
                        "password-reset", "Password Reset Request",
                        "You have requested a password reset. Click the link to reset your password.",
                        event, null);
                break;
            default:
                log.warn("⚠️ Unhandled user event type: {}", eventType);
        }
    }
}
