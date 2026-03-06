package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.InventoryDTO;
import com.shopsphere.inventory.dto.ReservationDTO;
import com.shopsphere.inventory.dto.request.*;
import com.shopsphere.inventory.dto.response.AvailabilityCheckResponse;
import com.shopsphere.inventory.dto.response.BulkUpdateResponse;
import com.shopsphere.inventory.dto.response.StockHistoryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.inventory.model.StockMovementLog;
import com.shopsphere.inventory.service.InventoryService;
import com.shopsphere.inventory.service.LowStockService;
import com.shopsphere.inventory.service.ReservationService;
import com.shopsphere.inventory.service.StockStreamService;
import com.shopsphere.inventory.service.StockHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
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
    private final StockHistoryService stockHistoryService;
    private final StockStreamService stockStreamService;
    private final ObjectMapper objectMapper;

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
    public ResponseEntity<BulkUpdateResponse> bulkUpdateStock(
            @RequestBody @NotEmpty(message = "Bulk update payload cannot be empty")
            List<@Valid BulkUpdateInventoryRequest> updates) {
        log.info("POST /inventory/bulk-update - Updating {} products", updates.size());
        BulkUpdateResponse response = inventoryService.bulkUpdateStock(updates);
        HttpStatus status = response.getFailureCount() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
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
    public ResponseEntity<?> checkAvailability(@RequestBody JsonNode requestBody) {
        log.info("POST /inventory/check-availability - Checking availability");

        if (requestBody == null || requestBody.isNull() || requestBody.isMissingNode()) {
            throw new IllegalArgumentException("Request body is required");
        }

        if (requestBody.isArray()) {
            if (requestBody.size() == 0) {
                throw new IllegalArgumentException("Batch request cannot be empty");
            }

            List<CheckAvailabilityRequest> requests = objectMapper.convertValue(
                    requestBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CheckAvailabilityRequest.class)
            );

            List<AvailabilityCheckResponse> responses = requests.stream()
                    .peek(this::validateCheckAvailabilityRequest)
                    .map(this::buildAvailabilityResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        }

        CheckAvailabilityRequest request = objectMapper.convertValue(requestBody, CheckAvailabilityRequest.class);
        validateCheckAvailabilityRequest(request);
        return ResponseEntity.ok(buildAvailabilityResponse(request));
    }

    private void validateCheckAvailabilityRequest(CheckAvailabilityRequest request) {
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (request.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (request.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
    }

    private AvailabilityCheckResponse buildAvailabilityResponse(CheckAvailabilityRequest request) {
        boolean available = reservationService.checkAvailability(
                request.getProductId(),
                request.getQuantity()
        );

        InventoryDTO inventory = inventoryService.getStockLevel(request.getProductId());
        return AvailabilityCheckResponse.builder()
                .productId(request.getProductId())
                .requestedQuantity(request.getQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .available(available)
                .message(available ? "Available" : "Insufficient stock")
                .build();
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

    /**
     * Epic 2.2.2: Get stock history by product
     */
    @GetMapping("/{productId}/history")
    public ResponseEntity<List<StockHistoryResponse>> getStockHistory(
            @PathVariable UUID productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) StockMovementLog.ChangeType changeType) {
        log.info("GET /inventory/{}/history - Fetching stock history", productId);
        List<StockHistoryResponse> history = stockHistoryService.getHistory(productId, from, to, changeType);
        return ResponseEntity.ok(history);
    }

    /**
     * Epic 2.2.3: Stock adjustment endpoint
     */
    @PostMapping("/{productId}/adjustment")
    public ResponseEntity<InventoryDTO> adjustInventory(
            @PathVariable UUID productId,
            @Valid @RequestBody InventoryAdjustmentRequest request) {
        log.info("POST /inventory/{}/adjustment - Applying stock adjustment", productId);
        InventoryDTO updated = stockHistoryService.adjustStock(productId, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping(value = "/audit-report", produces = "text/csv")
    public ResponseEntity<String> getAuditReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) StockMovementLog.ChangeType changeType) {
        String csv = stockHistoryService.exportAuditReport(from, to, changeType);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv);
    }

    /**
     * Epic 2.5.1: Real-time stock sync stream (SSE)
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter stockStream() {
        log.info("GET /inventory/stream - Subscribing to stock stream");
        return stockStreamService.subscribe();
    }
}
