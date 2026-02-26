package com.shopsphere.notification.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class BulkNotificationRequest {
    private List<String> userIds;
    private List<String> channels;
    private String templateId;
    private String title;
    private String message;
    private Map<String, Object> data;
}
