package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.BulkStockUpdateItem;
import com.shopsphere.inventory.dto.InventoryCreateRequest;
import com.shopsphere.inventory.dto.InventoryResponse;
import com.shopsphere.inventory.dto.InventoryUpdateRequest;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody InventoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{productId}")
    public InventoryResponse get(@PathVariable String productId) {
        return service.getByProductId(productId);
    }

    @PutMapping("/{productId}")
    public InventoryResponse updateStock(
        @PathVariable String productId,
        @Valid @RequestBody InventoryUpdateRequest request
    ) {
        return service.updateStock(productId, request);
    }

    @PostMapping("/bulk-update")
    public List<InventoryResponse> bulkUpdate(@Valid @RequestBody List<BulkStockUpdateItem> items) {
        return service.bulkUpdate(items);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String productId) {
        service.deleteByProductId(productId);
    }
}
