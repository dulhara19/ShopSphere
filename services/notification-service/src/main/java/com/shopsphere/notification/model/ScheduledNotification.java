package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "scheduled_notifications")
public class ScheduledNotification {
    @Id
    private String id;
    private String userId;
    private List<String> channels; // EMAIL, IN_APP, SMS, PUSH
    private String templateId;
    private String title;
    private String message;
    private Map<String, Object> data;
    private LocalDateTime scheduledAt;
    private boolean cancelled = false;
    private boolean sent = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}
