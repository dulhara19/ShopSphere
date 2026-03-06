package com.shopsphere.product.service.integration;

import com.shopsphere.product.dto.ReviewSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ReviewIntegrationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${services.review.url:http://localhost:8083/api/reviews}")
    private String reviewServiceUrl;

    public ReviewSummaryDTO getProductRatingSummary(String productId) {
        String cacheKey = "rating_summary_" + productId;

        ReviewSummaryDTO summary = (ReviewSummaryDTO) redisTemplate.opsForValue().get(cacheKey);

        if (summary == null) {
            try {
                String url = reviewServiceUrl + "/product/" + productId + "/summary";
                summary = restTemplate.getForObject(url, ReviewSummaryDTO.class);

                if (summary != null) {
                    redisTemplate.opsForValue().set(cacheKey, summary, 30, TimeUnit.MINUTES);
                }
            } catch (Exception e) {
                log.warn("Failed to fetch reviews for product {}: {}", productId, e.getMessage());
                summary = new ReviewSummaryDTO(0.0, 0, new HashMap<>());
            }
        }
        return summary;
    }
}
