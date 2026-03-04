package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "email_templates")
public class EmailTemplate {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name; // e.g., 'order-confirmation'
    private String subject;
    private String htmlContent;
    private String plainTextContent;
    private List<String> variables; // e.g., ["customerName", "orderNumber"]
    private int version = 1;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}