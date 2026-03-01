package com.shopsphere.analytics.service;

import com.shopsphere.analytics.model.SellerMetric;
import com.shopsphere.analytics.repository.SellerMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerAnalyticsService {

        private final SellerMetricRepository sellerMetricRepository;

        public Map<String, Object> getSellerPerformance(String sellerId, LocalDate from, LocalDate to) {
                List<SellerMetric> metrics = sellerMetricRepository.findBySellerIdAndMetricDateBetween(sellerId, from,
                                to);

                long totalOrders = metrics.stream().mapToLong(m -> m.getTotalOrders() != null ? m.getTotalOrders() : 0)
                                .sum();
                long itemsSold = metrics.stream()
                                .mapToLong(m -> m.getTotalItemsSold() != null ? m.getTotalItemsSold() : 0)
                                .sum();

                BigDecimal grossRevenue = metrics.stream()
                                .map(m -> m.getGrossRevenue() != null ? m.getGrossRevenue() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                double avgRating = metrics.stream()
                                .filter(m -> m.getAverageRating() != null)
                                .mapToDouble(SellerMetric::getAverageRating)
                                .average().orElse(0.0);

                return Map.of(
                                "sellerId", sellerId,
                                "period", from.toString() + " to " + to.toString(),
                                "performance", Map.of(
                                                "totalOrders", totalOrders,
                                                "itemsSold", itemsSold,
                                                "grossRevenue", grossRevenue,
                                                "averageRating", avgRating));
        }

        public List<Map<String, Object>> getSellerRanking(LocalDate date) {
                return sellerMetricRepository.findTopByMetricDateOrderByGrossRevenueDesc(date)
                                .stream()
                                .limit(20)
                                .map(m -> {
                                        Map<String, Object> map = new java.util.HashMap<>();
                                        map.put("sellerId", m.getSellerId());
                                        map.put("revenue", m.getGrossRevenue());
                                        map.put("orders", m.getTotalOrders());
                                        return map;
                                })
                                .collect(Collectors.toList());
        }

        public Map<String, Object> getCommissionReport(String sellerId, LocalDate from, LocalDate to) {
                List<SellerMetric> metrics = sellerMetricRepository.findBySellerIdAndMetricDateBetween(sellerId, from,
                                to);

                BigDecimal totalCommission = metrics.stream()
                                .map(m -> m.getCommissionOwed() != null ? m.getCommissionOwed() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal netPayout = metrics.stream()
                                .map(m -> m.getNetPayout() != null ? m.getNetPayout() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return Map.of(
                                "sellerId", sellerId,
                                "period", from.toString() + " to " + to.toString(),
                                "financials", Map.of(
                                                "commissionOwed", totalCommission,
                                                "netPayout", netPayout));
        }
}
