package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "email_logs")
public class EmailLog {
    @Id
    private String id;
    private String to;
    private String subject;
    private String templateId;
    private Map<String, Object> templateData;
    private String status; // QUEUED, SENT, DELIVERED, OPENED, BOUNCED, FAILED
    private int attempts = 0;
    private int maxAttempts = 3;
    private String errorMessage;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime openedAt;
}
