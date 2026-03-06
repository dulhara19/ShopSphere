package com.shopsphere.recommendation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * CoPurchaseMatrix - Stores products bought together
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "co_purchase_matrix")
public class CoPurchaseMatrix {

    @Id
    private String id;

    private String productId;
    private String associatedProductId;
    private long purchaseCount;        // Times bought together
    private Double coOccurrenceScore;  // Frequency score
    private Instant lastUpdated;
}
