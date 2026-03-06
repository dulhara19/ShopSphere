package com.shopsphere.notification.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificationRequest {
    private String userId;
    private String type;
    private String title;
    private String message;
    private Map<String, Object> data;
    private String link;
}
