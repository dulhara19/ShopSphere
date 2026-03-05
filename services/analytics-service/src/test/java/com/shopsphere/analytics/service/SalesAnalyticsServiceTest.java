package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.FunnelDTO;
import com.shopsphere.analytics.dto.SalesMetricDTO;
import com.shopsphere.analytics.dto.SalesSummaryDTO;
import com.shopsphere.analytics.model.SalesMetric;
import com.shopsphere.analytics.repository.SalesMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SalesAnalyticsServiceTest {

    @Mock
    private SalesMetricRepository salesMetricRepository;

    private SalesAnalyticsService salesAnalyticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        salesAnalyticsService = new SalesAnalyticsService(salesMetricRepository, Optional.empty());
    }

    @Test
    public void testGetSalesSummary() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SalesMetric metric1 = SalesMetric.builder()
            .totalRevenue(new BigDecimal("100.00"))
            .totalOrders(10L)
            .totalItems(20L)
            .build();
        SalesMetric metric2 = SalesMetric.builder()
            .totalRevenue(new BigDecimal("200.00"))
            .totalOrders(20L)
            .totalItems(40L)
            .build();

        when(salesMetricRepository.findByMetricDateBetween(from, to))
            .thenReturn(Arrays.asList(metric1, metric2));
        when(salesMetricRepository.findByMetricDateBetween(any(LocalDate.class), eq(from.minusDays(1))))
            .thenReturn(Collections.emptyList());

        SalesSummaryDTO result = salesAnalyticsService.getSalesSummary(from, to);

        assertNotNull(result);
        assertEquals(new BigDecimal("300.00"), result.getTotalRevenue());
        assertEquals(30L, result.getTotalOrders());
        assertEquals(60L, result.getTotalItems());
        assertNotNull(result.getComparisonPeriod());
    }

    @Test
    public void testGetSalesSummaryEmptyData() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(salesMetricRepository.findByMetricDateBetween(any(), any()))
            .thenReturn(Collections.emptyList());

        SalesSummaryDTO result = salesAnalyticsService.getSalesSummary(from, to);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalRevenue());
        assertEquals(0L, result.getTotalOrders());
        assertEquals(BigDecimal.ZERO, result.getAverageOrderValue());
    }

    @Test
    public void testGetSalesByDateRange() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SalesMetric metric = SalesMetric.builder()
            .metricDate(from)
            .granularity("day")
            .totalRevenue(new BigDecimal("100.00"))
            .totalOrders(10L)
            .totalItems(20L)
            .averageOrderValue(new BigDecimal("10.00"))
            .viewCount(100L)
            .addToCartCount(50L)
            .checkoutCount(20L)
            .purchaseCount(10L)
            .build();

        when(salesMetricRepository.findByMetricDateBetweenAndGranularity(from, to, "day"))
            .thenReturn(List.of(metric));

        List<SalesMetricDTO> result = salesAnalyticsService.getSalesByDateRange(from, to, "day");

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).getTotalRevenue());
    }

    @Test
    public void testGetTopProducts() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SalesMetric metric = SalesMetric.builder()
            .metricDate(from)
            .granularity("day")
            .productId("prod1")
            .totalRevenue(new BigDecimal("500.00"))
            .totalOrders(50L)
            .totalItems(100L)
            .averageOrderValue(new BigDecimal("10.00"))
            .viewCount(0L)
            .addToCartCount(0L)
            .checkoutCount(0L)
            .purchaseCount(0L)
            .build();

        when(salesMetricRepository.findTopProducts(from, to, 5)).thenReturn(List.of(metric));

        List<SalesMetricDTO> result = salesAnalyticsService.getTopProducts(5, from, to);

        assertEquals(1, result.size());
        assertEquals("prod1", result.get(0).getProductId());
    }

    @Test
    public void testGetTopCategories() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SalesMetric metric = SalesMetric.builder()
            .metricDate(from)
            .granularity("day")
            .categoryId("cat1")
            .totalRevenue(new BigDecimal("1000.00"))
            .totalOrders(100L)
            .totalItems(200L)
            .averageOrderValue(new BigDecimal("10.00"))
            .viewCount(0L)
            .addToCartCount(0L)
            .checkoutCount(0L)
            .purchaseCount(0L)
            .build();

        when(salesMetricRepository.findTopCategories(from, to, 5)).thenReturn(List.of(metric));

        List<SalesMetricDTO> result = salesAnalyticsService.getTopCategories(5, from, to);

        assertEquals(1, result.size());
        assertEquals("cat1", result.get(0).getCategoryId());
    }

    @Test
    public void testGetConversionFunnel() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        Object[] funnelRow = new Object[]{1000L, 500L, 200L, 100L};
        List<Object[]> funnelResults = Collections.singletonList(funnelRow);
        when(salesMetricRepository.sumFunnelMetrics(from, to)).thenReturn(funnelResults);

        FunnelDTO result = salesAnalyticsService.getConversionFunnel(from, to);

        assertNotNull(result);
        assertEquals(4, result.getSteps().size());
        assertEquals("Views", result.getSteps().get(0).getStepName());
        assertEquals(1000L, result.getSteps().get(0).getCount());
        assertEquals("Purchase", result.getSteps().get(3).getStepName());
        assertEquals(100L, result.getSteps().get(3).getCount());
        assertEquals(new BigDecimal("10.00"), result.getOverallConversionRate());
    }

    @Test
    public void testGetConversionFunnelEmptyData() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        Object[] funnelRow = new Object[]{0L, 0L, 0L, 0L};
        List<Object[]> funnelResults = Collections.singletonList(funnelRow);
        when(salesMetricRepository.sumFunnelMetrics(from, to)).thenReturn(funnelResults);

        FunnelDTO result = salesAnalyticsService.getConversionFunnel(from, to);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getOverallConversionRate());
    }

    @Test
    public void testGetSellerSalesSummary() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        String seller = "seller1";

        SalesMetric metric = SalesMetric.builder()
            .totalRevenue(new BigDecimal("500.00"))
            .totalOrders(25L)
            .totalItems(50L)
            .seller(seller)
            .build();

        when(salesMetricRepository.findByMetricDateBetweenAndSeller(from, to, seller))
            .thenReturn(List.of(metric));
        when(salesMetricRepository.findByMetricDateBetweenAndSeller(any(LocalDate.class), eq(from.minusDays(1)), eq(seller)))
            .thenReturn(Collections.emptyList());

        SalesSummaryDTO result = salesAnalyticsService.getSellerSalesSummary(from, to, seller);

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getTotalRevenue());
        assertEquals(25L, result.getTotalOrders());
    }

    @Test
    public void testGetSalesByCategory() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SalesMetric metric = SalesMetric.builder()
            .metricDate(from)
            .granularity("day")
            .categoryId("cat1")
            .totalRevenue(new BigDecimal("100.00"))
            .totalOrders(10L)
            .totalItems(20L)
            .averageOrderValue(new BigDecimal("10.00"))
            .viewCount(0L)
            .addToCartCount(0L)
            .checkoutCount(0L)
            .purchaseCount(0L)
            .build();

        when(salesMetricRepository.findByMetricDateBetween(from, to)).thenReturn(List.of(metric));

        List<SalesMetricDTO> result = salesAnalyticsService.getSalesByCategory(from, to);

        assertFalse(result.isEmpty());
    }
}
