package com.shopsphere.product.service.integration;

import com.shopsphere.product.dto.ReviewSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class ReviewIntegrationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${services.review.url:http://localhost:8083/api/reviews}")
    private String reviewServiceUrl;

    /**
     * Story 2.5.1: Aggregate from Review Service and Cache rating data
     */
    public ReviewSummaryDTO getProductRatingSummary(String productId) {
        String cacheKey = "rating_summary_" + productId;
        
        // 1. Check if the rating is already cached in Redis
        ReviewSummaryDTO summary = (ReviewSummaryDTO) redisTemplate.opsForValue().get(cacheKey);

        if (summary == null) {
            try {
                // 2. If not in cache, fetch from the external Review Service
                String url = reviewServiceUrl + "/product/" + productId + "/summary";
                summary = restTemplate.getForObject(url, ReviewSummaryDTO.class);
                
                // 3. Cache the fetched data for 30 minutes to reduce network calls
                if (summary != null) {
                    redisTemplate.opsForValue().set(cacheKey, summary, 30, TimeUnit.MINUTES);
                }
            } catch (Exception e) {
                // Fallback gracefully if Review Service is down
                System.err.println("Failed to fetch reviews for product " + productId + ": " + e.getMessage());
                summary = new ReviewSummaryDTO(0.0, 0);
            }
        }
        return summary;
    }
}