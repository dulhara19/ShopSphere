package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.SimilarProduct;
import com.shopsphere.recommendation.repository.SimilarProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

/**
 * Epic 1.4: Similar Products Service
 * Find products similar to a given product based on category, attributes, price range, etc.
 */
@Service
@RequiredArgsConstructor
public class SimilarProductService {

    private static final Logger logger = LoggerFactory.getLogger(SimilarProductService.class);

    private final SimilarProductRepository similarProductRepository;

    /**
     * Get similar products for a given product
     * @param productId Source product ID
     * @param limit Number of similar products to return
     * @return List of similar products
     */
    public List<SimilarProduct> getSimilarProducts(String productId, int limit) {
        logger.debug("Getting {} similar products for productId={}", limit, productId);

        Pageable pageable = PageRequest.of(0, limit);
        return similarProductRepository.findBySourceProductIdOrderBySimilarityScoreDesc(productId, pageable);
    }

    /**
     * Add a similar product relationship (excluded out of stock)
     * @param sourceProductId Source product
     * @param similarProductId Similar product
     * @param similarityScore Similarity score (0-100)
     * @param category Category (must match)
     * @param priceRange Price range
     * @param matchedAttributes Attributes that match
     */
    public void addSimilarProduct(String sourceProductId, String similarProductId, 
                                   Double similarityScore, String category,
                                   String priceRange, Map<String, Object> matchedAttributes) {
        SimilarProduct similar = SimilarProduct.builder()
            .sourceProductId(sourceProductId)
            .similarProductId(similarProductId)
            .similarityScore(similarityScore)
            .categoryMatch(category)
            .priceRange(priceRange)
            .matchedAttributes(matchedAttributes)
            .lastUpdated(Instant.now())
            .build();

        similarProductRepository.save(similar);
        logger.debug("Added similar product: {} -> {}", sourceProductId, similarProductId);
    }

    /**
     * Update similarity scores for a product
     * @param sourceProductId Source product
     * @param similarProducts List of updated similar products
     */
    public void updateSimilarProducts(String sourceProductId, List<SimilarProduct> similarProducts) {
        // Delete existing relationships
        similarProductRepository.deleteBySourceProductId(sourceProductId);

        // Save new relationships
        similarProductRepository.saveAll(similarProducts);
        logger.info("Updated {} similar products for productId={}", similarProducts.size(), sourceProductId);
    }
}
