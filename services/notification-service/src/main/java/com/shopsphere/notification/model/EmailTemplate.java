package com.shopsphere.notification.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "email_templates")
public class EmailTemplate {
    @Id
    private String id;
    private String name; // e.g., 'order-confirmation'
    private String subject;
    private String htmlContent;
}