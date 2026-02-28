package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * Search autocomplete / suggestion entry
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "search_suggestions")
public class SearchSuggestion {

    @Id
    private String id;

    private String term;
    private long usageCount;
    private Instant lastUsed;
}
