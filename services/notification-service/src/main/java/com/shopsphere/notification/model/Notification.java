package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String type; // ORDER, PAYMENT, SHIPPING, PROMO, SYSTEM
    private String title;
    private String message;
    private Map<String, Object> data; // additional JSON context
    private boolean read = false;
    private String link; // optional click-action URL
    private boolean deleted = false; // soft delete
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime readAt;
}