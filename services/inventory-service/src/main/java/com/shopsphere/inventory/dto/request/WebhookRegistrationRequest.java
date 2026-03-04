package com.shopsphere.inventory.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookRegistrationRequest {

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Event type is required")
    private String eventType;
}
