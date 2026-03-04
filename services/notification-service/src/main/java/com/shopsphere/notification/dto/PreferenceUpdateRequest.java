package com.shopsphere.notification.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PreferenceUpdateRequest {
    private Map<String, Boolean> email;
    private Map<String, Boolean> inApp;
    private Map<String, Boolean> sms;
    private Map<String, Boolean> push;
}
