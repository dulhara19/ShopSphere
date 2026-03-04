package com.shopsphere.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.WebhookSubscription;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookRegistrationResponse {

    private UUID id;
    private String url;

    @JsonProperty("event_type")
    private String eventType;

    private Boolean active;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public static WebhookRegistrationResponse fromEntity(WebhookSubscription sub) {
        return WebhookRegistrationResponse.builder()
                .id(sub.getId())
                .url(sub.getUrl())
                .eventType(sub.getEventType())
                .active(sub.getActive())
                .createdAt(sub.getCreatedAt())
                .build();
    }
}
