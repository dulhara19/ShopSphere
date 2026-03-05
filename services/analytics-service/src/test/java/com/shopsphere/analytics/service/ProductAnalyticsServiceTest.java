package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.ProductMetricDTO;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductAnalyticsServiceTest {

    @Mock
    private ProductMetricRepository productMetricRepository;

    private ProductAnalyticsService productAnalyticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        productAnalyticsService = new ProductAnalyticsService(productMetricRepository, Optional.empty());
    }

    @Test
    public void testGetProductPerformance() {
        String productId = "prod1";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric metric = ProductMetric.builder()
            .productId(productId)
            .categoryId("cat1")
            .metricDate(from)
            .viewCount(100L)
            .uniqueViewers(80L)
            .addToCartCount(50L)
            .purchaseCount(20L)
            .revenue(new BigDecimal("500.00"))
            .unitsSold(25L)
            .reviewCount(5L)
            .conversionRate(BigDecimal.ZERO)
            .avgRating(BigDecimal.ZERO)
            .build();

        when(productMetricRepository.findByProductIdAndMetricDateBetween(productId, from, to))
            .thenReturn(List.of(metric));

        ProductMetricDTO result = productAnalyticsService.getProductPerformance(productId, from, to);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(100L, result.getViewCount());
        assertEquals(new BigDecimal("500.00"), result.getRevenue());
    }

    @Test
    public void testGetTopViewedProducts() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric metric = ProductMetric.builder()
            .productId("prod1")
            .categoryId("cat1")
            .metricDate(from)
            .viewCount(500L)
            .uniqueViewers(400L)
            .addToCartCount(100L)
            .purchaseCount(50L)
            .revenue(new BigDecimal("1000.00"))
            .unitsSold(50L)
            .conversionRate(new BigDecimal("10.00"))
            .avgRating(new BigDecimal("4.50"))
            .reviewCount(20L)
            .build();

        when(productMetricRepository.findTopViewedProducts(from, to, 5)).thenReturn(List.of(metric));

        List<ProductMetricDTO> result = productAnalyticsService.getTopViewedProducts(5, from, to);

        assertEquals(1, result.size());
        assertEquals(500L, result.get(0).getViewCount());
    }

    @Test
    public void testGetTopSellingProducts() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric metric = ProductMetric.builder()
            .productId("prod1")
            .categoryId("cat1")
            .metricDate(from)
            .viewCount(100L)
            .uniqueViewers(80L)
            .addToCartCount(50L)
            .purchaseCount(200L)
            .revenue(new BigDecimal("5000.00"))
            .unitsSold(200L)
            .conversionRate(BigDecimal.ZERO)
            .avgRating(BigDecimal.ZERO)
            .reviewCount(0L)
            .build();

        when(productMetricRepository.findTopSellingProducts(from, to, 5)).thenReturn(List.of(metric));

        List<ProductMetricDTO> result = productAnalyticsService.getTopSellingProducts(5, from, to);

        assertEquals(1, result.size());
        assertEquals(200L, result.get(0).getPurchaseCount());
    }

    @Test
    public void testGetTopRevenueProducts() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric metric = ProductMetric.builder()
            .productId("prod1")
            .categoryId("cat1")
            .metricDate(from)
            .viewCount(100L)
            .uniqueViewers(80L)
            .addToCartCount(50L)
            .purchaseCount(20L)
            .revenue(new BigDecimal("10000.00"))
            .unitsSold(20L)
            .conversionRate(BigDecimal.ZERO)
            .avgRating(BigDecimal.ZERO)
            .reviewCount(0L)
            .build();

        when(productMetricRepository.findTopRevenueProducts(from, to, 5)).thenReturn(List.of(metric));

        List<ProductMetricDTO> result = productAnalyticsService.getTopRevenueProducts(5, from, to);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("10000.00"), result.get(0).getRevenue());
    }

    @Test
    public void testGetCategoryPerformance() {
        String categoryId = "cat1";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric metric = ProductMetric.builder()
            .productId("prod1")
            .categoryId(categoryId)
            .metricDate(from)
            .viewCount(100L)
            .uniqueViewers(80L)
            .addToCartCount(50L)
            .purchaseCount(20L)
            .revenue(new BigDecimal("500.00"))
            .unitsSold(20L)
            .conversionRate(BigDecimal.ZERO)
            .avgRating(BigDecimal.ZERO)
            .reviewCount(0L)
            .build();

        when(productMetricRepository.findByCategoryIdAndMetricDateBetween(categoryId, from, to))
            .thenReturn(List.of(metric));

        List<ProductMetricDTO> result = productAnalyticsService.getCategoryPerformance(categoryId, from, to);

        assertEquals(1, result.size());
        assertEquals(categoryId, result.get(0).getCategoryId());
    }

    @Test
    public void testGetProductPerformanceMultipleMetrics() {
        String productId = "prod1";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        ProductMetric m1 = ProductMetric.builder()
            .productId(productId).categoryId("cat1").metricDate(from)
            .viewCount(100L).uniqueViewers(80L).addToCartCount(50L).purchaseCount(20L)
            .revenue(new BigDecimal("500.00")).unitsSold(25L).reviewCount(5L)
            .conversionRate(BigDecimal.ZERO).avgRating(BigDecimal.ZERO)
            .build();

        ProductMetric m2 = ProductMetric.builder()
            .productId(productId).categoryId("cat1").metricDate(from.plusDays(1))
            .viewCount(200L).uniqueViewers(150L).addToCartCount(70L).purchaseCount(30L)
            .revenue(new BigDecimal("800.00")).unitsSold(35L).reviewCount(3L)
            .conversionRate(BigDecimal.ZERO).avgRating(BigDecimal.ZERO)
            .build();

        when(productMetricRepository.findByProductIdAndMetricDateBetween(productId, from, to))
            .thenReturn(Arrays.asList(m1, m2));

        ProductMetricDTO result = productAnalyticsService.getProductPerformance(productId, from, to);

        assertEquals(300L, result.getViewCount());
        assertEquals(new BigDecimal("1300.00"), result.getRevenue());
        assertEquals(60L, result.getUnitsSold());
    }
}
