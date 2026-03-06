package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Stores image embeddings for visual search functionality
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visual_search_images")
public class VisualSearchImage {

    @Id
    private String id;

    private String productId; // if associated with a product
    private String imageUrl;
    private List<Double> vector; // extracted features
    private Instant createdAt;
    private Instant updatedAt;
}
