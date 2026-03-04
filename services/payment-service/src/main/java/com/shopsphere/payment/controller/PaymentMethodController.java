package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.CardPaymentMethodRequest;
import com.shopsphere.payment.dto.PaymentMethodResponse;
import com.shopsphere.payment.service.PaymentMethodService;
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
 * Payment Method Controller
 *
 * REST endpoints for payment method (card) operations.
 * Implements Epic 1.3 API endpoints.
 */
@RestController
@RequestMapping("/api/payment-methods")
@Tag(name = "Payment Methods", description = "Saved payment method endpoints")
@Slf4j
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    /**
     * List saved payment methods
     * Story 1.3.2: List saved cards
     * GET /api/payment-methods
     */
    @GetMapping
    @Operation(summary = "List saved payment methods", description = "Get all saved payment methods for a user")
    public ResponseEntity<Page<PaymentMethodResponse>> getPaymentMethods(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching payment methods for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentMethodResponse> methods = paymentMethodService.getUserPaymentMethods(userId, pageable);
        return ResponseEntity.ok(methods);
    }

    /**
     * Save a new payment method
     * Story 1.3.1: Save card for future use
     * POST /api/payment-methods
     */
    @PostMapping
    @Operation(summary = "Save a payment method", description = "Add a new saved payment method for a user")
    public ResponseEntity<PaymentMethodResponse> savePaymentMethod(
            @RequestParam String userId,
            @RequestBody CardPaymentMethodRequest request) {
        log.info("Saving payment method for user: {}", userId);
        PaymentMethodResponse response = paymentMethodService.savePaymentMethod(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete a payment method
     * Story 1.3.4: Remove saved card
     * DELETE /api/payment-methods/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment method", description = "Remove a saved payment method")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable String id) {
        log.info("Deleting payment method: {}", id);
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Set default payment method
     * Story 1.3.5: Set default card
     * PUT /api/payment-methods/{id}/default
     */
    @PutMapping("/{id}/default")
    @Operation(summary = "Set default payment method")
    public ResponseEntity<PaymentMethodResponse> setDefaultPaymentMethod(
            @RequestParam String userId,
            @PathVariable String id) {
        log.info("Setting default payment method for user: {}", userId);
        PaymentMethodResponse response = paymentMethodService.setDefaultPaymentMethod(userId, id);
        return ResponseEntity.ok(response);
    }
}
