package com.shopsphere.analytics.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @NotBlank(message = "User ID is required")
    private String userId;

    private String productId;

    private String orderId;

    private String categoryId;

    private String sessionId;

    private JsonNode properties;

    private String source;

    private String version;
}
