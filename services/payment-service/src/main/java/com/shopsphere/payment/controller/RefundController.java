package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.RefundRequest;
import com.shopsphere.payment.dto.RefundResponse;
import com.shopsphere.payment.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Refund Controller
 *
 * REST endpoints for refund operations.
 * Implements Epic 1.4 API endpoints.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Refunds", description = "Refund processing endpoints")
@Slf4j
public class RefundController {

    @Autowired
    private RefundService refundService;

    /**
     * Process a refund
     * Stories 1.4.1, 1.4.2: Full and partial refunds
     * POST /api/payments/{id}/refund
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "Process a refund", description = "Create a full or partial refund for a payment")
    public ResponseEntity<RefundResponse> processRefund(
            @PathVariable String id,
            @RequestBody RefundRequest request) {
        log.info("Processing refund for payment: {}", id);
        request.setPaymentId(id);
        RefundResponse response = refundService.processRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get refund details
     * Story 1.4.3: Refund status tracking
     * GET /api/refunds/{refundId}
     */
    @GetMapping("/{id}/refunds/{refundId}")
    @Operation(summary = "Get refund details", description = "Retrieve details of a specific refund")
    public ResponseEntity<RefundResponse> getRefund(@PathVariable String refundId) {
        log.info("Fetching refund: {}", refundId);
        RefundResponse response = refundService.getRefund(refundId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all refunds for a payment
     * Story 1.4.3: Refund status tracking
     * GET /api/payments/{id}/refunds
     */
    @GetMapping("/{id}/refunds")
    @Operation(summary = "List refunds for a payment", description = "Get all refunds for a specific payment")
    public ResponseEntity<Page<RefundResponse>> getPaymentRefunds(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching refunds for payment: {}", id);
        Pageable pageable = PageRequest.of(page, size);
        Page<RefundResponse> refunds = refundService.getPaymentRefunds(id, pageable);
        return ResponseEntity.ok(refunds);
    }
}
