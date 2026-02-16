package com.shopsphere.inventory.service;

import com.shopsphere.inventory.dto.BulkStockUpdateItem;
import com.shopsphere.inventory.dto.InventoryCreateRequest;
import com.shopsphere.inventory.dto.InventoryResponse;
import com.shopsphere.inventory.dto.InventoryUpdateRequest;

import java.util.List;

public interface InventoryService {
    InventoryResponse create(InventoryCreateRequest request);
    InventoryResponse getByProductId(String productId);
    InventoryResponse updateStock(String productId, InventoryUpdateRequest request);
    List<InventoryResponse> bulkUpdate(List<BulkStockUpdateItem> items);
    void deleteByProductId(String productId);
}
