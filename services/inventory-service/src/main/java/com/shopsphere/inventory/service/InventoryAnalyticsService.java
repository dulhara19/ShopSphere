package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.response.AnalyticsTurnoverResponse;
import com.shopsphere.inventory.dto.response.DaysRemainingResponse;
import com.shopsphere.inventory.dto.response.DeadStockResponse;
import com.shopsphere.inventory.dto.response.RestockRecommendationResponse;
import com.shopsphere.inventory.dto.response.ValuationResponse;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.repository.StockMovementLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryAnalyticsService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementLogRepository stockMovementLogRepository;

    @Transactional(readOnly = true)
    public List<AnalyticsTurnoverResponse> getTurnover() {
        return inventoryRepository.findAll().stream()
                .map(inv -> {
                    long salesQty = stockMovementLogRepository
                            .findByProductIdAndChangeTypeAndCreatedAtAfter(
                                    inv.getProductId(),
                                    StockMovementLog.ChangeType.SALE,
                                    LocalDateTime.now().minusDays(30))
                            .stream()
                            .mapToLong(logEntry -> Math.abs(logEntry.getQuantityChange()))
                            .sum();

                    double averageInventory = Math.max(1.0, inv.getQuantity().doubleValue());
                    double turnover = salesQty / averageInventory;

                    return AnalyticsTurnoverResponse.builder()
                            .productId(inv.getProductId())
                            .salesQuantity(salesQty)
                            .averageInventory(averageInventory)
                            .turnoverRate(turnover)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DaysRemainingResponse> getDaysRemaining() {
        return inventoryRepository.findAll().stream()
                .map(inv -> {
                    double avgDailySales = calculateAvgDailySales(inv.getProductId(), 30);
                    double daysRemaining = avgDailySales <= 0 ? Double.POSITIVE_INFINITY : inv.getQuantity() / avgDailySales;
                    return DaysRemainingResponse.builder()
                            .productId(inv.getProductId())
                            .currentQuantity(inv.getQuantity())
                            .avgDailySales(avgDailySales)
                            .daysRemaining(daysRemaining)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DeadStockResponse> getDeadStock(Integer thresholdDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(thresholdDays == null ? 30 : thresholdDays);

        return inventoryRepository.findAll().stream()
                .map(inv -> {
                    LocalDateTime lastMovementAt = stockMovementLogRepository.findByProductId(inv.getProductId()).stream()
                            .map(StockMovementLog::getCreatedAt)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);
                    return Map.entry(inv, lastMovementAt);
                })
                .filter(entry -> entry.getKey().getQuantity() > 0)
                .filter(entry -> entry.getValue() == null || entry.getValue().isBefore(cutoff))
                .map(entry -> DeadStockResponse.builder()
                        .productId(entry.getKey().getProductId())
                        .quantity(entry.getKey().getQuantity())
                        .lastMovementAt(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ValuationResponse> getValuation(Double defaultUnitCost) {
        double unitCost = defaultUnitCost == null ? 1.0 : defaultUnitCost;
        return inventoryRepository.findAll().stream()
                .map(inv -> ValuationResponse.builder()
                        .productId(inv.getProductId())
                        .quantity(inv.getQuantity())
                        .unitCost(unitCost)
                        .value(inv.getQuantity() * unitCost)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestockRecommendationResponse getRestockRecommendation(UUID productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId));

        double avgDailySales = calculateAvgDailySales(productId, 14);
        long leadTimeDays = 7;
        long safetyStock = 5;
        long reorderPoint = (long) Math.ceil(avgDailySales * leadTimeDays) + safetyStock;
        long recommendedQty = Math.max(0, reorderPoint - inventory.getQuantity() + (long) Math.ceil(avgDailySales * 7));

        LocalDate reorderDate = inventory.getQuantity() <= reorderPoint
                ? LocalDate.now()
                : LocalDate.now().plusDays(Math.max(1, Math.round((inventory.getQuantity() - reorderPoint) / Math.max(avgDailySales, 1.0))));

        return RestockRecommendationResponse.builder()
                .productId(productId)
                .currentQuantity(inventory.getQuantity())
                .avgDailySales(avgDailySales)
                .recommendedReorderQuantity(recommendedQty)
                .recommendedReorderDate(reorderDate)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RestockRecommendationResponse> getForecasts() {
        return inventoryRepository.findAll().stream()
                .map(Inventory::getProductId)
                .map(this::getRestockRecommendation)
                .sorted(Comparator.comparing(RestockRecommendationResponse::getRecommendedReorderDate))
                .collect(Collectors.toList());
    }

    private double calculateAvgDailySales(UUID productId, int daysWindow) {
        long sales = stockMovementLogRepository
                .findByProductIdAndChangeTypeAndCreatedAtAfter(
                        productId,
                        StockMovementLog.ChangeType.SALE,
                        LocalDateTime.now().minusDays(daysWindow))
                .stream()
                .mapToLong(logEntry -> Math.abs(logEntry.getQuantityChange()))
                .sum();
        return sales / (double) daysWindow;
    }
}
