package com.shopsphere.recommendation.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;

@Data
public class SearchQueryRequest {

    private String userId; // optional

    @NotBlank(message = "sessionId is required")
    private String sessionId;

    @NotBlank(message = "searchTerm is required")
    private String searchTerm;

    private Map<String, Object> metadata;
}
