package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.request.BulkUpdateInventoryRequest;
import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.request.UpdateInventoryRequest;
import com.shopsphere.inventory.dto.response.BulkUpdateItemResult;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ProductNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventService eventService;
    private final StockHistoryService stockHistoryService;

    /**
     * Epic 1.1.1: Initialize inventory for product
     */
    @Transactional
    public InventoryDTO createInventory(CreateInventoryRequest request) {
        log.info("Creating inventory for product: {}", request.getProductId());

        // Check if inventory already exists
        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            log.warn("Inventory already exists for product: {}", request.getProductId());
            throw new IllegalArgumentException("Inventory already exists for product: " + request.getProductId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0L)
                .reservedQuantity(0L)
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 5L)
                .build();

        inventory.updateStatus();
        Inventory saved = inventoryRepository.save(inventory);
        stockHistoryService.logMovement(
                saved.getProductId(),
                com.shopsphere.inventory.model.StockMovementLog.ChangeType.RESTOCK,
                0L,
                saved.getQuantity(),
                "Inventory initialized",
                null,
                null
        );

        log.info("Inventory created for product: {} with quantity: {}", saved.getProductId(), saved.getQuantity());
        return InventoryDTO.fromEntity(saved);
    }

    /**
     * Epic 1.1.2: Get stock level by product ID
     */
    @Transactional(readOnly = true)
    public InventoryDTO getStockLevel(UUID productId) {
        log.debug("Fetching stock level for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        return InventoryDTO.fromEntity(inventory);
    }

    /**
     * Epic 1.1.3: Update stock quantity (Seller)
     */
    @Transactional
    public InventoryDTO updateStockQuantity(UUID productId, UpdateInventoryRequest request) {
        log.info("Updating inventory for product: {} with quantity: {}", productId, request.getQuantity());

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        // Validate non-negative quantity
        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        long previousQuantity = inventory.getQuantity();
        inventory.setQuantity(request.getQuantity());

        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }

        inventory.updateStatus();
        Inventory updated = inventoryRepository.save(inventory);
        stockHistoryService.logMovement(
                productId,
                com.shopsphere.inventory.model.StockMovementLog.ChangeType.ADJUSTMENT,
                previousQuantity,
                updated.getQuantity(),
                "Manual stock update",
                null,
                null
        );

        log.info("Inventory updated for product: {} with new quantity: {}", productId, updated.getQuantity());
        eventService.publishStockUpdatedEvent(updated);

        return InventoryDTO.fromEntity(updated);
    }

    /**
     * Epic 1.1.4: Bulk stock update
     */
    public BulkUpdateResponse bulkUpdateStock(List<BulkUpdateInventoryRequest> updates) {
        log.info("Performing bulk stock update for {} products", updates.size());

        List<BulkUpdateItemResult> results = updates.stream()
                .map(this::processBulkUpdateItem)
                .collect(Collectors.toList());

        int successCount = (int) results.stream()
                .filter(item -> Boolean.TRUE.equals(item.getSuccess()))
                .count();

        return BulkUpdateResponse.builder()
                .total(results.size())
                .successCount(successCount)
                .failureCount(results.size() - successCount)
                .results(results)
                .build();
    }

    private BulkUpdateItemResult processBulkUpdateItem(BulkUpdateInventoryRequest update) {
        try {
            Inventory inventory = inventoryRepository.findByProductId(update.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(update.getProductId().toString()));

            if (update.getQuantity() < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }

            long previousQuantity = inventory.getQuantity();
            inventory.setQuantity(update.getQuantity());
            if (update.getLowStockThreshold() != null) {
                inventory.setLowStockThreshold(update.getLowStockThreshold());
            }

            inventory.updateStatus();
            Inventory saved = inventoryRepository.save(inventory);
            eventService.publishStockUpdatedEvent(saved);
            stockHistoryService.logMovement(
                    saved.getProductId(),
                    com.shopsphere.inventory.model.StockMovementLog.ChangeType.ADJUSTMENT,
                    previousQuantity,
                    saved.getQuantity(),
                    "Bulk stock update",
                    null,
                    null
            );

            return BulkUpdateItemResult.builder()
                    .productId(saved.getProductId())
                    .success(true)
                    .inventory(InventoryDTO.fromEntity(saved))
                    .build();
        } catch (Exception ex) {
            log.error("Bulk update failed for product: {}", update.getProductId(), ex);
            return BulkUpdateItemResult.builder()
                    .productId(update.getProductId())
                    .success(false)
                    .error(ex.getMessage())
                    .build();
        }
    }

    /**
     * Epic 1.1.5: Delete inventory record
     */
    @Transactional
    public void deleteInventory(UUID productId) {
        log.info("Deleting inventory for product: {}", productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        inventoryRepository.delete(Objects.requireNonNull(inventory, "inventory"));
        log.info("Inventory deleted for product: {}", productId);
    }

    /**
     * Get all low stock items
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockItems() {
        log.debug("Fetching all low stock items");
        return inventoryRepository.findLowStockItems()
                .stream()
                .map(InventoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all out of stock items
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getOutOfStockItems() {
        log.debug("Fetching all out of stock items");
        return inventoryRepository.findOutOfStockItems()
                .stream()
                .map(InventoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get inventory for multiple products
     */
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryForProducts(List<UUID> productIds) {
        log.debug("Fetching inventory for {} products", productIds.size());
        return inventoryRepository.findByProductIdIn(productIds)
                .stream()
                .map(InventoryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Check if product has sufficient stock (internal use)
     */
    @Transactional(readOnly = true)
    public boolean hasStock(UUID productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        if (!inventory.hasSufficientStock(quantity)) {
            throw new InsufficientStockException(productId.toString(), quantity, inventory.getAvailableQuantity());
        }
        return true;
    }
}
