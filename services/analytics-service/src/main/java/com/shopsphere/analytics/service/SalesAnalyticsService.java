package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.SalesMetricDTO;
import com.shopsphere.analytics.dto.SalesSummaryDTO;
import com.shopsphere.analytics.model.SalesMetric;
import com.shopsphere.analytics.repository.SalesMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SalesAnalyticsService {

    private final SalesMetricRepository salesMetricRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SALES_CACHE_KEY = "sales:summary:";
    private static final long CACHE_TTL_MINUTES = 60;

    public SalesAnalyticsService(SalesMetricRepository salesMetricRepository, 
                                Optional<RedisTemplate<String, Object>> redisTemplate) {
        this.salesMetricRepository = salesMetricRepository;
        this.redisTemplate = redisTemplate.orElse(null);
    }    @Transactional
    public SalesSummaryDTO getSalesSummary(LocalDate from, LocalDate to) {
        String cacheKey = SALES_CACHE_KEY + from + ":" + to;

        try {
            SalesSummaryDTO cached = (SalesSummaryDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache hit for sales summary: {}", cacheKey);
                return cached;
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable, proceeding without cache: {}", e.getMessage());
        }

        List<SalesMetric> metrics = salesMetricRepository.findByMetricDateBetween(from, to);

        BigDecimal totalRevenue = metrics.stream()
            .map(SalesMetric::getTotalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalOrders = metrics.stream()
            .mapToLong(SalesMetric::getTotalOrders)
            .sum();

        Long totalItems = metrics.stream()
            .mapToLong(SalesMetric::getTotalItems)
            .sum();

        BigDecimal aov = totalOrders > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
            : BigDecimal.ZERO;

        // Calculate previous period for comparison
        int daysBetween = (int) java.time.temporal.ChronoUnit.DAYS.between(from, to);
        LocalDate previousFrom = from.minusDays(daysBetween + 1);
        LocalDate previousTo = from.minusDays(1);

        List<SalesMetric> previousMetrics = salesMetricRepository.findByMetricDateBetween(previousFrom, previousTo);
        BigDecimal previousRevenue = previousMetrics.stream()
            .map(SalesMetric::getTotalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long previousOrders = previousMetrics.stream()
            .mapToLong(SalesMetric::getTotalOrders)
            .sum();

        String revenueChange = calculatePercentageChange(previousRevenue, totalRevenue);
        String ordersChange = calculatePercentageChange(BigDecimal.valueOf(previousOrders), BigDecimal.valueOf(totalOrders));

        SalesSummaryDTO summary = SalesSummaryDTO.builder()
            .totalRevenue(totalRevenue)
            .totalOrders(totalOrders)
            .averageOrderValue(aov)
            .totalItems(totalItems)
            .comparisonPeriod(SalesSummaryDTO.ComparisonPeriodDTO.builder()
                .revenueChange(revenueChange)
                .ordersChange(ordersChange)
                .previousPeriodRevenue(previousRevenue)
                .previousPeriodOrders(previousOrders)
                .build())
            .build();

        try {
            redisTemplate.opsForValue().set(cacheKey, summary, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return summary;
    }

    public List<SalesMetricDTO> getSalesByDateRange(LocalDate from, LocalDate to, String granularity) {
        List<SalesMetric> metrics = salesMetricRepository.findByMetricDateBetweenAndGranularity(from, to, granularity);
        return metrics.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<SalesMetricDTO> getSalesByCategory(LocalDate from, LocalDate to) {
        List<SalesMetric> metrics = salesMetricRepository.findByMetricDateBetween(from, to);
        return metrics.stream()
            .filter(m -> m.getCategoryId() != null)
            .collect(Collectors.groupingBy(SalesMetric::getCategoryId))
            .values().stream()
            .flatMap(List::stream)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<SalesMetricDTO> getTopProducts(int limit, LocalDate from, LocalDate to) {
        List<SalesMetric> topProducts = salesMetricRepository.findTopProducts(from, to, limit);
        return topProducts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<SalesMetricDTO> getTopCategories(int limit, LocalDate from, LocalDate to) {
        List<SalesMetric> topCategories = salesMetricRepository.findTopCategories(from, to, limit);
        return topCategories.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void aggregateDailySalesMetrics(LocalDate date) {
        log.info("Aggregating sales metrics for {}", date);
        // This would typically be called by a scheduler to aggregate event data into metrics
    }

    private String calculatePercentageChange(BigDecimal previous, BigDecimal current) {
        if (previous.equals(BigDecimal.ZERO)) {
            return current.equals(BigDecimal.ZERO) ? "0%" : "+100%";
        }
        BigDecimal change = current.subtract(previous)
            .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
            .multiply(BigDecimal.valueOf(100));
        String sign = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sign + change.setScale(2, BigDecimal.ROUND_HALF_UP) + "%";
    }

    private SalesMetricDTO convertToDTO(SalesMetric metric) {
        return SalesMetricDTO.builder()
            .metricDate(metric.getMetricDate())
            .granularity(metric.getGranularity())
            .categoryId(metric.getCategoryId())
            .productId(metric.getProductId())
            .totalRevenue(metric.getTotalRevenue())
            .totalOrders(metric.getTotalOrders())
            .totalItems(metric.getTotalItems())
            .averageOrderValue(metric.getAverageOrderValue())
            .viewCount(metric.getViewCount())
            .addToCartCount(metric.getAddToCartCount())
            .checkoutCount(metric.getCheckoutCount())
            .purchaseCount(metric.getPurchaseCount())
            .seller(metric.getSeller())
            .build();
    }
}
