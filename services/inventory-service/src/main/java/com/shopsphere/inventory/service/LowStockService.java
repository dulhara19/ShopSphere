package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.exception.ProductNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LowStockService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventService eventService;
    private final NotificationService notificationService;

    @Value("${inventory.low-stock.default-threshold:5}")
    private Long defaultThreshold;

    @Value("${inventory.low-stock.notification-enabled:true}")
    private boolean notificationEnabled;

    /**
     * Epic 1.3.1: Set low stock threshold
     */
    @Transactional
    public InventoryDTO setLowStockThreshold(UUID productId, Long threshold) {
        log.info("Setting low stock threshold for product: {}, threshold: {}", productId, threshold);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        inventory.setLowStockThreshold(threshold);
        inventory.updateStatus();

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Low stock threshold set for product: {}", productId);

        return InventoryDTO.fromEntity(updated);
    }

    /**
     * Epic 1.3.2 & 1.3.3: Detect low stock and send notification
     */
    @Transactional
    public void detectAndNotifyLowStock(UUID productId) {
        log.info("Checking low stock status for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        // Update status if needed
        Inventory.StockStatus previousStatus = inventory.getStatus();
        inventory.updateStatus();
        boolean statusChanged = previousStatus != inventory.getStatus();

        if (statusChanged &&
                (inventory.getStatus() == Inventory.StockStatus.LOW_STOCK ||
                 inventory.getStatus() == Inventory.StockStatus.OUT_OF_STOCK)) {

            inventoryRepository.save(inventory);

            // Publish event
            if (inventory.getStatus() == Inventory.StockStatus.LOW_STOCK) {
                eventService.publishLowStockEvent(inventory);
            } else {
                eventService.publishOutOfStockEvent(inventory);
            }

            // Send notification if enabled
            if (notificationEnabled) {
                sendLowStockNotification(inventory);
            }

            log.info("Low stock detected for product: {}, status: {}", productId, inventory.getStatus());
        }
    }

    /**
     * Epic 1.3.4: Get low stock products
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockProducts() {
        log.debug("Fetching all low stock products");
        return inventoryRepository.findLowStockItems()
                .stream()
                .map(InventoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Epic 1.3.5: Out of stock handling
     */
    @Transactional
    public void handleOutOfStock(UUID productId) {
        log.info("Handling out of stock for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        if (inventory.getQuantity() <= 0) {
            inventory.setStatus(Inventory.StockStatus.OUT_OF_STOCK);
            inventoryRepository.save(inventory);

            // Publish out of stock event
            eventService.publishOutOfStockEvent(inventory);

            // Send out of stock notification
            if (notificationEnabled) {
                sendOutOfStockNotification(inventory);
            }

            log.info("Out of stock handled for product: {}", productId);
        }
    }

    /**
     * Get list of out of stock items
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getOutOfStockProducts() {
        log.debug("Fetching all out of stock products");
        return inventoryRepository.findOutOfStockItems()
                .stream()
                .map(InventoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Send low stock notification to seller via Notification Service
     */
    private void sendLowStockNotification(Inventory inventory) {
        try {
            notificationService.sendLowStockAlert(inventory);
            log.info("Low stock notification sent for product: {}", inventory.getProductId());
        } catch (Exception e) {
            log.error("Failed to send low stock notification for product: {}", inventory.getProductId(), e);
        }
    }

    /**
     * Send out of stock notification to seller
     */
    private void sendOutOfStockNotification(Inventory inventory) {
        try {
            notificationService.sendOutOfStockAlert(inventory);
            log.info("Out of stock notification sent for product: {}", inventory.getProductId());
        } catch (Exception e) {
            log.error("Failed to send out of stock notification for product: {}", inventory.getProductId(), e);
        }
    }
}
