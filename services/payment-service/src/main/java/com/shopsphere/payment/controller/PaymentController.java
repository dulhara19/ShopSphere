package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.*;
import com.shopsphere.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Payment Controller
 *
 * REST endpoints for payment operations.
 * Implements Epic 1.2 API endpoints.
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment processing endpoints")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create payment intent
     * Story 1.2.1: Create payment intent
     * POST /api/payments/create-intent
     */
    @PostMapping("/create-intent")
    @Operation(summary = "Create a payment intent", description = "Create a new payment intent for an order")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
            @RequestBody CreatePaymentIntentRequest request) {
        log.info("Creating payment intent for order: {}", request.getOrderId());
        CreatePaymentIntentResponse response = paymentService.createPaymentIntent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Confirm payment
     * Story 1.2.2: Confirm payment
     * POST /api/payments/{id}/confirm
     */
    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm a payment", description = "Confirm an existing payment intent")
    public ResponseEntity<PaymentStatusResponse> confirmPayment(
            @PathVariable String id,
            @RequestBody ConfirmPaymentRequest request) {
        log.info("Confirming payment: {}", id);
        PaymentStatusResponse response = paymentService.confirmPayment(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment status
     * Story 1.2.4: Payment status check
     * GET /api/payments/{id}/status
     */
    @GetMapping("/{id}/status")
    @Operation(summary = "Get payment status", description = "Check the current status of a payment")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(@PathVariable String id) {
        log.info("Fetching payment status: {}", id);
        PaymentStatusResponse response = paymentService.getPaymentStatus(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retry payment
     * Story 1.2.3: Handle payment failure and allow retry
     * POST /api/payments/{id}/retry
     */
    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry a failed payment")
    public ResponseEntity<PaymentStatusResponse> retryPayment(
            @PathVariable String id,
            @RequestBody ConfirmPaymentRequest request) {
        log.info("Retrying payment: {}", id);
        PaymentStatusResponse response = paymentService.confirmPayment(id, request);
        return ResponseEntity.ok(response);
    }
}
