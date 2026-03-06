package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Embedding vector for a product used in content-based recommendations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_embeddings")
public class ProductEmbedding {

    @Id
    private String id;

    private String productId;
    private List<Double> vector; // numeric embedding
    private Instant createdAt;
    private Instant updatedAt;
}
