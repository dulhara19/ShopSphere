package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.request.AssignWarehouseInventoryRequest;
import com.shopsphere.inventory.dto.request.CreateWarehouseRequest;
import com.shopsphere.inventory.dto.request.TransferStockRequest;
import com.shopsphere.inventory.dto.request.UpdateWarehouseRequest;
import com.shopsphere.inventory.dto.response.ProductWarehouseStockResponse;
import com.shopsphere.inventory.dto.response.WarehouseResponse;
import com.shopsphere.inventory.service.WarehouseService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody CreateWarehouseRequest request) {
        log.info("POST /warehouses - Creating warehouse with code {}", request.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(request));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseResponse>> getWarehouses() {
        return ResponseEntity.ok(warehouseService.getWarehouses());
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponse> getWarehouse(@PathVariable UUID warehouseId) {
        return ResponseEntity.ok(warehouseService.getWarehouse(warehouseId));
    }

    @PutMapping("/warehouses/{warehouseId}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable UUID warehouseId,
            @RequestBody UpdateWarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(warehouseId, request));
    }

    @PostMapping("/inventory/{productId}/warehouses/{warehouseId}")
    public ResponseEntity<ProductWarehouseStockResponse> assignInventoryToWarehouse(
            @PathVariable UUID productId,
            @PathVariable UUID warehouseId,
            @Valid @RequestBody AssignWarehouseInventoryRequest request) {
        return ResponseEntity.ok(warehouseService.assignStock(productId, warehouseId, request.getQuantity()));
    }

    @GetMapping("/inventory/{productId}/warehouses")
    public ResponseEntity<ProductWarehouseStockResponse> getWarehouseStock(@PathVariable UUID productId) {
        return ResponseEntity.ok(warehouseService.getWarehouseStockForProduct(productId));
    }

    @PostMapping("/inventory/transfer")
    public ResponseEntity<ProductWarehouseStockResponse> transferStock(@Valid @RequestBody TransferStockRequest request) {
        return ResponseEntity.ok(warehouseService.transferStock(
                request.getProductId(),
                request.getFromWarehouseId(),
                request.getToWarehouseId(),
                request.getQuantity()
        ));
    }
}
