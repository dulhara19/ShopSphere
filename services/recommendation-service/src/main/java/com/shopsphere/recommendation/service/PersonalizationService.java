package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.UserPreference;
import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.model.EventType;
import com.shopsphere.recommendation.repository.UserPreferenceRepository;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;

/**
 * Epic 1.6: Basic Personalization Service
 * Track user preferences and provide personalized recommendations
 * - Category affinity
 * - Price range preferences
 * - Homepage and "For You" recommendations
 */
@Service
@RequiredArgsConstructor
public class PersonalizationService {

    private static final Logger logger = LoggerFactory.getLogger(PersonalizationService.class);

    private final UserPreferenceRepository userPreferenceRepository;
    private final EventTrackingRepository eventTrackingRepository;

    /**
     * Get or create user preference
     */
    public UserPreference getUserPreference(String userId) {
        return userPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> UserPreference.builder()
                .userId(userId)
                .categoryAffinity(new HashMap<>())
                .totalViewCount(0)
                .totalPurchaseCount(0)
                .lastUpdated(Instant.now())
                .build());
    }

    /**
     * Track user viewing a product
     */
    public void trackProductView(String userId, String categoryId) {
        UserPreference preference = getUserPreference(userId);
        preference.addCategoryView(categoryId);
        preference.addView();
        preference.setLastUpdated(Instant.now());
        userPreferenceRepository.save(preference);

        logger.debug("Tracked view for userId={}, categoryId={}", userId, categoryId);
    }

    /**
     * Track user purchase
     */
    public void trackPurchase(String userId, Integer priceAmount) {
        UserPreference preference = getUserPreference(userId);
        preference.addPurchase();

        // Update price preferences
        if (priceAmount != null) {
            if (preference.getMinPricePreference() == null) {
                preference.setMinPricePreference(priceAmount);
                preference.setMaxPricePreference(priceAmount);
            } else {
                preference.setMinPricePreference(Math.min(preference.getMinPricePreference(), priceAmount));
                preference.setMaxPricePreference(Math.max(preference.getMaxPricePreference(), priceAmount));
            }
        }

        preference.setLastUpdated(Instant.now());
        userPreferenceRepository.save(preference);

        logger.debug("Tracked purchase for userId={}, price={}", userId, priceAmount);
    }

    /**
     * Get most preferred categories for a user
     */
    public List<String> getPreferredCategories(String userId, int limit) {
        UserPreference preference = getUserPreference(userId);

        if (preference.getCategoryAffinity() == null || preference.getCategoryAffinity().isEmpty()) {
            return Collections.emptyList();
        }

        return preference.getCategoryAffinity().entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(limit)
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Get user's typical price range
     */
    public Map<String, Integer> getUserPriceRange(String userId) {
        UserPreference preference = getUserPreference(userId);

        Map<String, Integer> priceRange = new HashMap<>();
        priceRange.put("min", preference.getMinPricePreference() != null ? preference.getMinPricePreference() : 0);
        priceRange.put("max", preference.getMaxPricePreference() != null ? preference.getMaxPricePreference() : Integer.MAX_VALUE);

        return priceRange;
    }

    /**
     * Get homepage recommendations (popular for anonymous, personalized for logged-in)
     * For now, returns top categories based on affinity
     */
    public List<String> getHomepageRecommendations(String userId, int limit) {
        logger.debug("Getting homepage recommendations for userId={}", userId);

        if (userId == null) {
            // Anonymous user - return generic popular products
            return List.of(); // Would integrate with TrendingProductService
        }

        // Logged-in user - return preferred categories
        return getPreferredCategories(userId, limit);
    }

    /**
     * Get "For You" recommendations based on browsing history
     * Mix of different categories with secondary recommendations
     */
    public List<String> getForYouRecommendations(String userId, int limit) {
        logger.debug("Getting 'For You' recommendations for userId={}", userId);

        UserPreference preference = getUserPreference(userId);

        if (preference.getCategoryAffinity() == null || preference.getCategoryAffinity().isEmpty()) {
            return Collections.emptyList();
        }

        // Get diverse categories (top 3 main + suggestions)
        List<String> recommendations = new ArrayList<>();

        // Sort by affinity score
        preference.getCategoryAffinity().entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(Math.min(3, limit / 2))
            .forEach(e -> recommendations.add(e.getKey()));

        // Add secondary recommendations (lower affinity)
        preference.getCategoryAffinity().entrySet().stream()
            .sorted((a, b) -> a.getValue().compareTo(b.getValue()))
            .limit(Math.max(0, limit - recommendations.size()))
            .forEach(e -> {
                if (!recommendations.contains(e.getKey())) {
                    recommendations.add(e.getKey());
                }
            });

        return recommendations.stream().limit(limit).toList();
    }

    /**
     * Merge session preferences into user account (on login)
     */
    public void mergeSessionPreferences(String userId, String sessionId) {
        logger.debug("Merging session {} preferences into userId={}", sessionId, userId);

        // In a full implementation, this would merge session tracking data
        // For now, just update timestamp
        UserPreference preference = getUserPreference(userId);
        preference.setLastUpdated(Instant.now());
        userPreferenceRepository.save(preference);
    }
}
