package com.shopsphere.inventory.service.impl;

import com.shopsphere.inventory.dto.BulkStockUpdateItem;
import com.shopsphere.inventory.dto.InventoryCreateRequest;
import com.shopsphere.inventory.dto.InventoryResponse;
import com.shopsphere.inventory.dto.InventoryUpdateRequest;
import com.shopsphere.inventory.dto.StockUpdateMode;
import com.shopsphere.inventory.exception.InventoryNotFoundException;
import com.shopsphere.inventory.model.Inventory;
import com.shopsphere.inventory.model.InventoryStatus;
import com.shopsphere.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public InventoryResponse create(InventoryCreateRequest request) {
        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(request.getLowStockThreshold());
        inventory.setStatus(resolveStatus(inventory));
        return toResponse(repository.save(inventory));
    }

    @Override
    public InventoryResponse getByProductId(String productId) {
        Inventory inventory = repository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));
        return toResponse(inventory);
    }

    @Override
    public InventoryResponse updateStock(String productId, InventoryUpdateRequest request) {
        Inventory inventory = repository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));

        if (request.getMode() == StockUpdateMode.ADD) {
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        } else {
            inventory.setQuantity(request.getQuantity());
        }

        if (inventory.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        inventory.setStatus(resolveStatus(inventory));
        return toResponse(repository.save(inventory));
    }

    @Override
    public List<InventoryResponse> bulkUpdate(List<BulkStockUpdateItem> items) {
        List<InventoryResponse> responses = new ArrayList<>();
        for (BulkStockUpdateItem item : items) {
            InventoryUpdateRequest request = new InventoryUpdateRequest();
            request.setMode(item.getMode());
            request.setQuantity(item.getQuantity());
            responses.add(updateStock(item.getProductId(), request));
        }
        return responses;
    }

    @Override
    public void deleteByProductId(String productId) {
        repository.deleteByProductId(productId);
    }

    private InventoryStatus resolveStatus(Inventory inventory) {
        int available = inventory.getQuantity() - inventory.getReservedQuantity();
        if (available <= 0) {
            return InventoryStatus.OUT_OF_STOCK;
        }
        if (available <= inventory.getLowStockThreshold()) {
            return InventoryStatus.LOW_STOCK;
        }
        return InventoryStatus.IN_STOCK;
    }

    private InventoryResponse toResponse(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setProductId(inventory.getProductId());
        response.setQuantity(inventory.getQuantity());
        response.setReservedQuantity(inventory.getReservedQuantity());
        response.setAvailableQuantity(inventory.getAvailableQuantity());
        response.setLowStockThreshold(inventory.getLowStockThreshold());
        response.setStatus(inventory.getStatus());
        response.setCreatedAt(inventory.getCreatedAt());
        response.setLastUpdated(inventory.getLastUpdated());
        return response;
    }
}
