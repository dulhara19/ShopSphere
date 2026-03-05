package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.ProductMetricDTO;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductAnalyticsService {

    private final ProductMetricRepository productMetricRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_KEY = "product:";
    private static final long CACHE_TTL_MINUTES = 30;

    public ProductAnalyticsService(ProductMetricRepository productMetricRepository,
                                  Optional<RedisTemplate<String, Object>> redisTemplate) {
        this.productMetricRepository = productMetricRepository;
        this.redisTemplate = redisTemplate.orElse(null);
    }

    public ProductMetricDTO getProductPerformance(String productId, LocalDate from, LocalDate to) {
        List<ProductMetric> metrics = productMetricRepository.findByProductIdAndMetricDateBetween(productId, from, to);

        ProductMetric aggregated = ProductMetric.builder()
            .productId(productId)
            .viewCount(metrics.stream().mapToLong(ProductMetric::getViewCount).sum())
            .uniqueViewers(metrics.stream().mapToLong(ProductMetric::getUniqueViewers).sum())
            .addToCartCount(metrics.stream().mapToLong(ProductMetric::getAddToCartCount).sum())
            .purchaseCount(metrics.stream().mapToLong(ProductMetric::getPurchaseCount).sum())
            .revenue(metrics.stream()
                .map(ProductMetric::getRevenue)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))
            .unitsSold(metrics.stream().mapToLong(ProductMetric::getUnitsSold).sum())
            .reviewCount(metrics.stream().mapToLong(ProductMetric::getReviewCount).sum())
            .build();

        return convertToDTO(aggregated);
    }

    public List<ProductMetricDTO> getTopViewedProducts(int limit, LocalDate from, LocalDate to) {
        String cacheKey = PRODUCT_CACHE_KEY + "top-viewed:" + from + ":" + to + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<ProductMetricDTO> cached = (List<ProductMetricDTO>) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        List<ProductMetric> topProducts = productMetricRepository.findTopViewedProducts(from, to, limit);
        List<ProductMetricDTO> result = topProducts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return result;
    }

    public List<ProductMetricDTO> getTopSellingProducts(int limit, LocalDate from, LocalDate to) {
        String cacheKey = PRODUCT_CACHE_KEY + "top-selling:" + from + ":" + to + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<ProductMetricDTO> cached = (List<ProductMetricDTO>) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        List<ProductMetric> topProducts = productMetricRepository.findTopSellingProducts(from, to, limit);
        List<ProductMetricDTO> result = topProducts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return result;
    }

    public List<ProductMetricDTO> getTopRevenueProducts(int limit, LocalDate from, LocalDate to) {
        String cacheKey = PRODUCT_CACHE_KEY + "top-revenue:" + from + ":" + to + ":" + limit;

        try {
            if (redisTemplate != null) {
                @SuppressWarnings("unchecked")
                List<ProductMetricDTO> cached = (List<ProductMetricDTO>) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        List<ProductMetric> topProducts = productMetricRepository.findTopRevenueProducts(from, to, limit);
        List<ProductMetricDTO> result = topProducts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return result;
    }

    public List<ProductMetricDTO> getCategoryPerformance(String categoryId, LocalDate from, LocalDate to) {
        List<ProductMetric> metrics = productMetricRepository.findByCategoryIdAndMetricDateBetween(categoryId, from, to);
        return metrics.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ProductMetricDTO> getTopProductsByCategory(String categoryId, LocalDate from, LocalDate to) {
        List<ProductMetric> topProducts = productMetricRepository.findTopProductsByCategory(categoryId, from, to);
        return topProducts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void aggregateDailyProductMetrics(LocalDate date) {
        log.info("Aggregating product metrics for {}", date);
    }

    private ProductMetricDTO convertToDTO(ProductMetric metric) {
        return ProductMetricDTO.builder()
            .productId(metric.getProductId())
            .categoryId(metric.getCategoryId())
            .metricDate(metric.getMetricDate())
            .viewCount(metric.getViewCount())
            .uniqueViewers(metric.getUniqueViewers())
            .addToCartCount(metric.getAddToCartCount())
            .purchaseCount(metric.getPurchaseCount())
            .revenue(metric.getRevenue())
            .unitsSold(metric.getUnitsSold())
            .conversionRate(metric.getConversionRate())
            .avgRating(metric.getAvgRating())
            .reviewCount(metric.getReviewCount())
            .build();
    }
}
