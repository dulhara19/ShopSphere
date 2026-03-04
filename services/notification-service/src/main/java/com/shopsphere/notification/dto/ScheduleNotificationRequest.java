package com.shopsphere.notification.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleNotificationRequest {
    private String userId;
    private List<String> channels;
    private String templateId;
    private String title;
    private String message;
    private Map<String, Object> data;
    private LocalDateTime scheduledAt;
}
