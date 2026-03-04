package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.PaymentStatusResponse;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal Payment Controller
 *
 * Internal-only endpoints for inter-service communication.
 * Implements Epic 1.6 requirements.
 */
@RestController
@RequestMapping("/internal/payments")
@Tag(name = "Internal Payments", description = "Internal payment endpoints for inter-service communication")
@Slf4j
public class InternalPaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Get payment details by order ID
     * Story 1.6.2: Get payment for order
     * GET /internal/payments/order/{orderId}
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Internal endpoint: Get payment details by order ID")
    public ResponseEntity<PaymentStatusResponse> getPaymentByOrderId(@PathVariable String orderId) {
        log.info("Internal request: Getting payment for order: {}", orderId);
        Payment payment = paymentService.getPaymentByOrderId(orderId);

        PaymentStatusResponse response = PaymentStatusResponse.builder()
            .paymentId(payment.getId())
            .orderId(payment.getOrderId())
            .status(payment.getStatus().toString())
            .amount(payment.getAmount())
            .currency(payment.getCurrency())
            .paymentMethod(payment.getPaymentMethod().toString())
            .failureReason(payment.getFailureReason())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Payment status callback
     * Story 1.6.1: Payment status callback - called by Order Service
     * POST /internal/payments/{id}/status-callback
     */
    @PostMapping("/{id}/status-callback")
    @Operation(summary = "Payment status callback", description = "Internal endpoint: Callback from Order Service to update payment state")
    public ResponseEntity<Void> paymentStatusCallback(@PathVariable String id) {
        log.info("Internal request: Payment status callback for payment: {}", id);
        // Acknowledge the callback
        return ResponseEntity.ok().build();
    }
}
