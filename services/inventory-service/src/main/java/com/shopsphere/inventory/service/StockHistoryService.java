package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.request.InventoryAdjustmentRequest;
import com.shopsphere.inventory.dto.response.StockHistoryResponse;
import com.shopsphere.inventory.exception.ProductNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.repository.StockMovementLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockHistoryService {

    private final StockMovementLogRepository stockMovementLogRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryEventService eventService;

    @Transactional
    public void logMovement(
            UUID productId,
            StockMovementLog.ChangeType changeType,
            Long quantityBefore,
            Long quantityAfter,
            String reason,
            UUID userId,
            UUID referenceId
    ) {
        StockMovementLog logEntry = StockMovementLog.builder()
                .productId(productId)
                .changeType(changeType)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .quantityChange(quantityAfter - quantityBefore)
                .reason(reason)
                .userId(userId)
                .referenceId(referenceId)
                .build();

        stockMovementLogRepository.save(logEntry);
    }

    @Transactional(readOnly = true)
    public List<StockHistoryResponse> getHistory(
            UUID productId,
            LocalDateTime from,
            LocalDateTime to,
            StockMovementLog.ChangeType changeType
    ) {
        return stockMovementLogRepository.findHistory(productId, from, to, changeType)
                .stream()
                .map(StockHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDTO adjustStock(UUID productId, InventoryAdjustmentRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        long before = inventory.getQuantity();
        long after = before + request.getQuantityChange();

        if (after < 0) {
            throw new IllegalArgumentException("Adjustment results in negative stock");
        }

        inventory.setQuantity(after);
        inventory.updateStatus();
        Inventory updated = inventoryRepository.save(inventory);

        logMovement(
                productId,
                request.getChangeType(),
                before,
                after,
                request.getReason(),
                request.getUserId(),
                null
        );

        eventService.publishStockUpdatedEvent(updated);
        log.info("Stock adjusted for product {} from {} to {}", productId, before, after);
        return InventoryDTO.fromEntity(updated);
    }
}
