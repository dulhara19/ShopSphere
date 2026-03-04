package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "notification_preferences")
public class NotificationPreference {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;

    private Map<String, Boolean> email; // e.g., {"orderUpdates": true, "promotions": false}
    private Map<String, Boolean> inApp;
    private Map<String, Boolean> sms;
    private Map<String, Boolean> push;

    private String unsubscribeToken;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    /** Sensible defaults for a new user */
    public static NotificationPreference createDefault(String userId) {
        NotificationPreference pref = new NotificationPreference();
        pref.setUserId(userId);
        pref.setEmail(Map.of(
                "orderUpdates", true,
                "promotions", true,
                "newsletter", false,
                "systemAlerts", true));
        pref.setInApp(Map.of(
                "orderUpdates", true,
                "promotions", true,
                "systemAlerts", true));
        pref.setSms(Map.of(
                "orderUpdates", false,
                "promotions", false));
        pref.setPush(Map.of(
                "orderUpdates", true,
                "promotions", false));
        pref.setUnsubscribeToken(java.util.UUID.randomUUID().toString());
        return pref;
    }
}
