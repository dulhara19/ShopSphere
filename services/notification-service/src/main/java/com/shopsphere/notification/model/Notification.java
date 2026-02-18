package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String type; // ORDER, SYSTEM, PROMO
    private String title;
    private String message;
    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}