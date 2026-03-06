package com.shopsphere.notification.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EmailRequest {
    private String to;
    private String templateId;
    private Map<String, Object> data;
}
