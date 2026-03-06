package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.response.ProductWarehouseStockResponse;
import com.shopsphere.inventory.dto.response.WarehouseResponse;
import com.shopsphere.inventory.dto.response.WarehouseStockResponse;
import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.request.UpdateWarehouseRequest;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.model.Warehouse;
import com.shopsphere.inventory.model.WarehouseInventory;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.repository.WarehouseInventoryRepository;
import com.shopsphere.inventory.repository.WarehouseRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private static final long DEFAULT_LOW_STOCK_THRESHOLD = 5L;

    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryEventService inventoryEventService;
    private final StockHistoryService stockHistoryService;

    @Transactional
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        Objects.requireNonNull(request, "request");
        warehouseRepository.findByCode(request.getCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("Warehouse code already exists: " + request.getCode());
        });

        Warehouse warehouse = Warehouse.builder()
                .code(request.getCode())
                .name(request.getName())
                .location(request.getLocation())
                .address(request.getAddress())
                .build();
        Objects.requireNonNull(warehouse, "warehouse");

        Warehouse saved = Objects.requireNonNull(warehouseRepository.save(warehouse), "warehouse");
        return WarehouseResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(WarehouseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(UUID id) {
        Objects.requireNonNull(id, "id");
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + id));
        return WarehouseResponse.fromEntity(warehouse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, UpdateWarehouseRequest request) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(request, "request");
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + id));

        if (request.getName() != null) {
            warehouse.setName(request.getName());
        }
        if (request.getLocation() != null) {
            warehouse.setLocation(request.getLocation());
        }
        if (request.getAddress() != null) {
            warehouse.setAddress(request.getAddress());
        }
        return WarehouseResponse.fromEntity(warehouseRepository.save(warehouse));
    }

    @Transactional
    public ProductWarehouseStockResponse assignStock(UUID productId, UUID warehouseId, Long quantity) {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(warehouseId, "warehouseId");
        Objects.requireNonNull(quantity, "quantity");
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + warehouseId));

        WarehouseInventory warehouseInventory = warehouseInventoryRepository
                .findByProductIdAndWarehouse(productId, warehouse)
                .orElse(WarehouseInventory.builder()
                        .productId(productId)
                        .warehouse(warehouse)
                        .quantity(0L)
                        .build());

        long before = warehouseInventory.getQuantity();
        warehouseInventory.setQuantity(quantity);
        warehouseInventoryRepository.save(warehouseInventory);

        syncTotalInventory(productId);
        stockHistoryService.logMovement(
                productId,
                StockMovementLog.ChangeType.RESTOCK,
                before,
                quantity,
                "Warehouse assignment update: " + warehouse.getCode(),
                null,
                warehouse.getId()
        );
        return getWarehouseStockForProduct(productId);
    }

    @Transactional
    public ProductWarehouseStockResponse transferStock(UUID productId, UUID fromWarehouseId, UUID toWarehouseId, Long quantity) {
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(fromWarehouseId, "fromWarehouseId");
        Objects.requireNonNull(toWarehouseId, "toWarehouseId");
        Objects.requireNonNull(quantity, "quantity");
        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new IllegalArgumentException("Source and destination warehouse cannot be the same");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be greater than zero");
        }

        Warehouse fromWarehouse = warehouseRepository.findById(fromWarehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + fromWarehouseId));
        Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + toWarehouseId));

        WarehouseInventory from = warehouseInventoryRepository.findByProductIdAndWarehouse(productId, fromWarehouse)
                .orElseThrow(() -> new IllegalArgumentException("No stock found in source warehouse"));

        if (from.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock in source warehouse");
        }

        WarehouseInventory to = warehouseInventoryRepository.findByProductIdAndWarehouse(productId, toWarehouse)
                .orElse(WarehouseInventory.builder()
                        .productId(productId)
                        .warehouse(toWarehouse)
                        .quantity(0L)
                        .build());

        long sourceBefore = from.getQuantity();
        long destBefore = to.getQuantity();

        from.setQuantity(sourceBefore - quantity);
        to.setQuantity(destBefore + quantity);
        warehouseInventoryRepository.save(from);
        warehouseInventoryRepository.save(to);

        syncTotalInventory(productId);

        stockHistoryService.logMovement(
                productId,
                StockMovementLog.ChangeType.ADJUSTMENT,
                sourceBefore,
                from.getQuantity(),
                "Transfer out from " + fromWarehouse.getCode() + " to " + toWarehouse.getCode(),
                null,
                fromWarehouseId
        );
        stockHistoryService.logMovement(
                productId,
                StockMovementLog.ChangeType.ADJUSTMENT,
                destBefore,
                to.getQuantity(),
                "Transfer in from " + fromWarehouse.getCode() + " to " + toWarehouse.getCode(),
                null,
                toWarehouseId
        );

        return getWarehouseStockForProduct(productId);
    }

    @Transactional(readOnly = true)
    public ProductWarehouseStockResponse getWarehouseStockForProduct(UUID productId) {
        Objects.requireNonNull(productId, "productId");
        List<WarehouseStockResponse> entries = warehouseInventoryRepository.findByProductId(productId)
                .stream()
                .map(WarehouseStockResponse::fromEntity)
                .collect(Collectors.toList());

        long total = entries.stream()
                .mapToLong(WarehouseStockResponse::getQuantity)
                .sum();

        return ProductWarehouseStockResponse.builder()
                .productId(productId)
                .totalQuantity(total)
                .warehouses(entries)
                .build();
    }

    private void syncTotalInventory(UUID productId) {
        Objects.requireNonNull(productId, "productId");
        long totalQuantity = warehouseInventoryRepository.sumQuantityByProductId(productId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .quantity(0L)
                        .reservedQuantity(0L)
                        .lowStockThreshold(DEFAULT_LOW_STOCK_THRESHOLD)
                        .build());

        inventory.setQuantity(totalQuantity);
        inventory.updateStatus();
        Inventory saved = inventoryRepository.save(inventory);
        inventoryEventService.publishStockUpdatedEvent(saved);
        log.info("Synchronized aggregate inventory from warehouses for product {}: {}", productId, totalQuantity);
    }
}
