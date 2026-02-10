package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.ReservationDTO;
import com.shopsphere.inventory.dto.request.*;
import com.shopsphere.inventory.dto.response.AvailabilityCheckResponse;
import com.shopsphere.inventory.service.InventoryService;
import com.shopsphere.inventory.service.LowStockService;
import com.shopsphere.inventory.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;
    private final ReservationService reservationService;
    private final LowStockService lowStockService;

    // ==================== Epic 1.1: Basic Inventory Management ====================

    /**
     * Epic 1.1.1: Create inventory for product
     */
    @PostMapping
    public ResponseEntity<InventoryDTO> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        log.info("POST /inventory - Creating inventory for product: {}", request.getProductId());
        InventoryDTO inventory = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    /**
     * Epic 1.1.2: Get stock level by product ID
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDTO> getStockLevel(@PathVariable UUID productId) {
        log.info("GET /inventory/{} - Getting stock level", productId);
        InventoryDTO inventory = inventoryService.getStockLevel(productId);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Epic 1.1.3: Update stock quantity
     */
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryDTO> updateStockQuantity(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateInventoryRequest request) {
        log.info("PUT /inventory/{} - Updating stock quantity", productId);
        InventoryDTO inventory = inventoryService.updateStockQuantity(productId, request);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Epic 1.1.4: Bulk stock update
     */
    @PostMapping("/bulk-update")
    public ResponseEntity<Void> bulkUpdateStock(@Valid @RequestBody List<UpdateInventoryRequest> updates) {
        log.info("POST /inventory/bulk-update - Updating {} products", updates.size());
        // TODO: Implement bulk update with product ID in request
        return ResponseEntity.accepted().build();
    }

    /**
     * Epic 1.1.5: Delete inventory
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable UUID productId) {
        log.info("DELETE /inventory/{} - Deleting inventory", productId);
        inventoryService.deleteInventory(productId);
        return ResponseEntity.noContent().build();
    }

    // ==================== Epic 1.2: Stock Reservation System ====================

    /**
     * Epic 1.2.1: Reserve stock for checkout
     */
    @PostMapping("/reserve")
    public ResponseEntity<ReservationDTO> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        log.info("POST /inventory/reserve - Reserving stock for product: {}", request.getProductId());
        ReservationDTO reservation = reservationService.reserveStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /**
     * Epic 1.2.2: Confirm reservation
     */
    @PostMapping("/confirm")
    public ResponseEntity<ReservationDTO> confirmReservation(@Valid @RequestBody ConfirmReservationRequest request) {
        log.info("POST /inventory/confirm - Confirming reservation: {}", request.getReservationId());
        ReservationDTO reservation = reservationService.confirmReservation(request.getReservationId());
        return ResponseEntity.ok(reservation);
    }

    /**
     * Epic 1.2.3: Release reservation
     */
    @PostMapping("/release")
    public ResponseEntity<ReservationDTO> releaseReservation(@Valid @RequestBody ReleaseReservationRequest request) {
        log.info("POST /inventory/release - Releasing reservation: {}", request.getReservationId());
        ReservationDTO reservation = reservationService.releaseReservation(request.getReservationId());
        return ResponseEntity.ok(reservation);
    }

    /**
     * Epic 1.2.4: Check availability for single product or batch
     */
    @PostMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@Valid @RequestBody Object request) {
        log.info("POST /inventory/check-availability - Checking availability");

        // Handle both single and batch requests
        if (request instanceof CheckAvailabilityRequest) {
            CheckAvailabilityRequest singleRequest = (CheckAvailabilityRequest) request;
            boolean available = reservationService.checkAvailability(
                    singleRequest.getProductId(),
                    singleRequest.getQuantity()
            );

            InventoryDTO inventory = inventoryService.getStockLevel(singleRequest.getProductId());
            AvailabilityCheckResponse response = AvailabilityCheckResponse.builder()
                    .productId(singleRequest.getProductId())
                    .requestedQuantity(singleRequest.getQuantity())
                    .availableQuantity(inventory.getAvailableQuantity())
                    .available(available)
                    .message(available ? "Available" : "Insufficient stock")
                    .build();

            return ResponseEntity.ok(response);
        } else if (request instanceof List) {
            List<?> items = (List<?>) request;
            // Implement batch checking if needed
            return ResponseEntity.ok(items);
        }

        return ResponseEntity.badRequest().build();
    }

    // ==================== Epic 1.3: Low Stock Alerts ====================

    /**
     * Epic 1.3.1: Set low stock threshold
     */
    @PutMapping("/{productId}/threshold")
    public ResponseEntity<InventoryDTO> setLowStockThreshold(
            @PathVariable UUID productId,
            @RequestParam Long threshold) {
        log.info("PUT /inventory/{}/threshold - Setting threshold to {}", productId, threshold);
        InventoryDTO inventory = lowStockService.setLowStockThreshold(productId, threshold);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Epic 1.3.4: Get low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockProducts() {
        log.info("GET /inventory/low-stock - Fetching low stock products");
        List<InventoryDTO> lowStockItems = lowStockService.getLowStockProducts();
        return ResponseEntity.ok(lowStockItems);
    }

    /**
     * Epic 1.3.5: Get out of stock products
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryDTO>> getOutOfStockProducts() {
        log.info("GET /inventory/out-of-stock - Fetching out of stock products");
        List<InventoryDTO> outOfStockItems = lowStockService.getOutOfStockProducts();
        return ResponseEntity.ok(outOfStockItems);
    }

    // ==================== Monitoring & Utilities ====================

    /**
     * Get reservations for a product
     */
    @GetMapping("/{productId}/reservations")
    public ResponseEntity<List<ReservationDTO>> getProductReservations(@PathVariable UUID productId) {
        log.info("GET /inventory/{}/reservations - Getting reservations", productId);
        List<ReservationDTO> reservations = reservationService.getReservationsByProduct(productId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Get reservations for an order
     */
    @GetMapping("/order/{orderId}/reservations")
    public ResponseEntity<List<ReservationDTO>> getOrderReservations(@PathVariable UUID orderId) {
        log.info("GET /inventory/order/{}/reservations - Getting reservations", orderId);
        List<ReservationDTO> reservations = reservationService.getReservationsByOrder(orderId);
        return ResponseEntity.ok(reservations);
    }
}
