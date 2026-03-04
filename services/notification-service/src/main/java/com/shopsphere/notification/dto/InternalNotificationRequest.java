package com.shopsphere.notification.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class InternalNotificationRequest {
    private String userId;
    private List<String> channels; // EMAIL, IN_APP, SMS, PUSH
    private String templateId;
    private String title;
    private String message;
    private Map<String, Object> data;
    private String link;
}
