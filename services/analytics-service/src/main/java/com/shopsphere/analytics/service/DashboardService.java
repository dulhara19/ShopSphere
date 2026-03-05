package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.DashboardDTO;
import com.shopsphere.analytics.dto.ProductMetricDTO;
import com.shopsphere.analytics.dto.SalesMetricDTO;
import com.shopsphere.analytics.model.ProductMetric;
import com.shopsphere.analytics.repository.ProductMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DashboardService {

    private final SalesAnalyticsService salesAnalyticsService;
    private final ProductAnalyticsService productAnalyticsService;
    private final UserAnalyticsService userAnalyticsService;
    private final ProductMetricRepository productMetricRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public DashboardService(SalesAnalyticsService salesAnalyticsService, ProductAnalyticsService productAnalyticsService,
                           UserAnalyticsService userAnalyticsService, ProductMetricRepository productMetricRepository,
                           Optional<RedisTemplate<String, Object>> redisTemplate) {
        this.salesAnalyticsService = salesAnalyticsService;
        this.productAnalyticsService = productAnalyticsService;
        this.userAnalyticsService = userAnalyticsService;
        this.productMetricRepository = productMetricRepository;
        this.redisTemplate = redisTemplate.orElse(null);
    }

    private static final String DASHBOARD_CACHE_KEY = "dashboard:";
    private static final long CACHE_TTL_MINUTES = 15;

    public DashboardDTO getAdminDashboard() {
        String cacheKey = DASHBOARD_CACHE_KEY + "admin:" + LocalDate.now();

        try {
            if (redisTemplate != null) {
                DashboardDTO cached = (DashboardDTO) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.info("Dashboard cache hit");
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable for dashboard: {}", e.getMessage());
        }

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);

        // Sales data
        var todaysSales = salesAnalyticsService.getSalesSummary(today, today);
        var weekSales = salesAnalyticsService.getSalesSummary(weekStart, today);
        var monthSales = salesAnalyticsService.getSalesSummary(monthStart, today);

        DashboardDTO.SalesDashboardDTO salesDashboard = DashboardDTO.SalesDashboardDTO.builder()
            .today(todaysSales.getTotalRevenue())
            .thisWeek(weekSales.getTotalRevenue())
            .thisMonth(monthSales.getTotalRevenue())
            .change(monthSales.getComparisonPeriod().getRevenueChange())
            .build();

        // Orders data
        DashboardDTO.OrdersDashboardDTO ordersDashboard = DashboardDTO.OrdersDashboardDTO.builder()
            .pending(0L)
            .processing(0L)
            .shipped(0L)
            .delivered(0L)
            .cancelled(0L)
            .build();

        // Users data
        Long newUsers = userAnalyticsService.getNewUsersCount(monthStart, today);
        Long activeUsers = userAnalyticsService.getMonthlyActiveUsers(monthStart, today);

        DashboardDTO.UsersDashboardDTO usersDashboard = DashboardDTO.UsersDashboardDTO.builder()
            .newUsers(newUsers)
            .activeUsers(activeUsers)
            .totalUsers(0L)
            .build();

        // Products data
        List<ProductMetricDTO> topProducts = productAnalyticsService.getTopSellingProducts(5, monthStart, today);
        List<DashboardDTO.TopProductDTO> topProductsDTOs = topProducts.stream()
            .limit(5)
            .map(p -> DashboardDTO.TopProductDTO.builder()
                .productId(p.getProductId())
                .productName(p.getProductName())
                .unitsSold(p.getUnitsSold())
                .revenue(p.getRevenue())
                .build())
            .toList();

        DashboardDTO.ProductsDashboardDTO productsDashboard = DashboardDTO.ProductsDashboardDTO.builder()
            .topSelling(topProductsDTOs)
            .lowStockCount(0L)
            .outOfStockCount(0L)
            .build();

        DashboardDTO dashboard = DashboardDTO.builder()
            .sales(salesDashboard)
            .orders(ordersDashboard)
            .users(usersDashboard)
            .products(productsDashboard)
            .build();

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, dashboard, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache dashboard to Redis: {}", e.getMessage());
        }
        return dashboard;
    }

    public DashboardDTO getSellerDashboard(String sellerId) {
        String cacheKey = DASHBOARD_CACHE_KEY + "seller:" + sellerId + ":" + LocalDate.now();

        try {
            if (redisTemplate != null) {
                DashboardDTO cached = (DashboardDTO) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable for seller dashboard: {}", e.getMessage());
        }

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);

        // Filter sales by seller
        var todaysSales = salesAnalyticsService.getSellerSalesSummary(today, today, sellerId);
        var weekSales = salesAnalyticsService.getSellerSalesSummary(weekStart, today, sellerId);
        var monthSales = salesAnalyticsService.getSellerSalesSummary(monthStart, today, sellerId);

        DashboardDTO.SalesDashboardDTO salesDashboard = DashboardDTO.SalesDashboardDTO.builder()
            .today(todaysSales.getTotalRevenue())
            .thisWeek(weekSales.getTotalRevenue())
            .thisMonth(monthSales.getTotalRevenue())
            .change(monthSales.getComparisonPeriod().getRevenueChange())
            .build();

        // Filter products by seller
        List<ProductMetric> sellerProducts = productMetricRepository.findTopSellingProductsBySeller(
            monthStart, today, sellerId, 5);
        List<DashboardDTO.TopProductDTO> topProductsDTOs = sellerProducts.stream()
            .map(p -> DashboardDTO.TopProductDTO.builder()
                .productId(p.getProductId())
                .unitsSold(p.getUnitsSold())
                .revenue(p.getRevenue())
                .build())
            .toList();

        DashboardDTO.ProductsDashboardDTO productsDashboard = DashboardDTO.ProductsDashboardDTO.builder()
            .topSelling(topProductsDTOs)
            .build();

        DashboardDTO dashboard = DashboardDTO.builder()
            .sales(salesDashboard)
            .products(productsDashboard)
            .build();

        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, dashboard, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache seller dashboard to Redis: {}", e.getMessage());
        }
        return dashboard;
    }

    public void invalidateDashboardCache() {
        log.info("Invalidating dashboard cache");
        try {
            if (redisTemplate != null) {
                var keys = redisTemplate.keys(DASHBOARD_CACHE_KEY + "*");
                if (keys != null) {
                    keys.forEach(key -> redisTemplate.delete(key));
                }
            }
        } catch (Exception e) {
            log.debug("Could not invalidate dashboard cache: {}", e.getMessage());
        }
    }
}
