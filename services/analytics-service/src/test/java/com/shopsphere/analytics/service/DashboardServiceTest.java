package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.DashboardDTO;
import com.shopsphere.analytics.dto.ProductMetricDTO;
import com.shopsphere.analytics.dto.SalesSummaryDTO;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class DashboardServiceTest {

    @Mock
    private SalesAnalyticsService salesAnalyticsService;

    @Mock
    private ProductAnalyticsService productAnalyticsService;

    @Mock
    private UserAnalyticsService userAnalyticsService;

    @Mock
    private ProductMetricRepository productMetricRepository;

    private DashboardService dashboardService;

    private SalesSummaryDTO mockSummary;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        dashboardService = new DashboardService(salesAnalyticsService, productAnalyticsService,
            userAnalyticsService, productMetricRepository, Optional.empty());

        mockSummary = SalesSummaryDTO.builder()
            .totalRevenue(new BigDecimal("1000.00"))
            .totalOrders(50L)
            .averageOrderValue(new BigDecimal("20.00"))
            .totalItems(100L)
            .comparisonPeriod(SalesSummaryDTO.ComparisonPeriodDTO.builder()
                .revenueChange("+10%")
                .ordersChange("+5%")
                .previousPeriodRevenue(new BigDecimal("900.00"))
                .previousPeriodOrders(47L)
                .build())
            .build();
    }

    @Test
    public void testGetAdminDashboard() {
        when(salesAnalyticsService.getSalesSummary(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockSummary);
        when(userAnalyticsService.getNewUsersCount(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(100L);
        when(userAnalyticsService.getMonthlyActiveUsers(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(500L);
        when(productAnalyticsService.getTopSellingProducts(anyInt(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());

        DashboardDTO result = dashboardService.getAdminDashboard();

        assertNotNull(result);
        assertNotNull(result.getSales());
        assertEquals(new BigDecimal("1000.00"), result.getSales().getToday());
        assertNotNull(result.getUsers());
        assertEquals(100L, result.getUsers().getNewUsers());
        assertEquals(500L, result.getUsers().getActiveUsers());
        assertNotNull(result.getOrders());
        assertNotNull(result.getProducts());
    }

    @Test
    public void testGetSellerDashboard() {
        String sellerId = "seller1";

        when(salesAnalyticsService.getSellerSalesSummary(any(LocalDate.class), any(LocalDate.class), eq(sellerId)))
            .thenReturn(mockSummary);
        when(productMetricRepository.findTopSellingProductsBySeller(any(LocalDate.class), any(LocalDate.class), eq(sellerId), anyInt()))
            .thenReturn(Collections.emptyList());

        DashboardDTO result = dashboardService.getSellerDashboard(sellerId);

        assertNotNull(result);
        assertNotNull(result.getSales());
        assertEquals(new BigDecimal("1000.00"), result.getSales().getToday());
        verify(salesAnalyticsService, times(3)).getSellerSalesSummary(any(), any(), eq(sellerId));
    }

    @Test
    public void testGetSellerDashboardWithProducts() {
        String sellerId = "seller1";

        when(salesAnalyticsService.getSellerSalesSummary(any(LocalDate.class), any(LocalDate.class), eq(sellerId)))
            .thenReturn(mockSummary);

        ProductMetric pm = ProductMetric.builder()
            .productId("prod1")
            .categoryId("cat1")
            .metricDate(LocalDate.now())
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

        when(productMetricRepository.findTopSellingProductsBySeller(any(), any(), eq(sellerId), anyInt()))
            .thenReturn(List.of(pm));

        DashboardDTO result = dashboardService.getSellerDashboard(sellerId);

        assertNotNull(result.getProducts());
        assertEquals(1, result.getProducts().getTopSelling().size());
        assertEquals("prod1", result.getProducts().getTopSelling().get(0).getProductId());
    }

    @Test
    public void testInvalidateDashboardCacheWithoutRedis() {
        // Should not throw when redisTemplate is null
        assertDoesNotThrow(() -> dashboardService.invalidateDashboardCache());
    }

    @Test
    public void testGetAdminDashboardNoRedis() {
        when(salesAnalyticsService.getSalesSummary(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(mockSummary);
        when(userAnalyticsService.getNewUsersCount(any(), any())).thenReturn(0L);
        when(userAnalyticsService.getMonthlyActiveUsers(any(), any())).thenReturn(0L);
        when(productAnalyticsService.getTopSellingProducts(anyInt(), any(), any()))
            .thenReturn(Collections.emptyList());

        // Should work fine without Redis
        DashboardDTO result = dashboardService.getAdminDashboard();
        assertNotNull(result);
    }
}
