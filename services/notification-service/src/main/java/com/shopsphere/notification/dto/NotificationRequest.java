package com.shopsphere.notification.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String userId;
    private String type;
    private String title;
    private String message;
}
