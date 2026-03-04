package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.TransactionResponse;
import com.shopsphere.payment.dto.PaymentStatusResponse;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Transaction Controller
 *
 * REST endpoints for transaction history and reporting.
 * Implements Epic 1.5 requirements.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction history and reporting endpoints")
@Slf4j
public class TransactionController {

    @Autowired
    private PaymentService paymentService;

    /**
     * List user transactions
     * Story 1.5.1: List user transactions with pagination
     * GET /api/transactions
     */
    @GetMapping
    @Operation(summary = "List user transactions", description = "Get paginated list of user transactions (payments)")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching transactions for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> payments = paymentService.getPaymentHistory(userId, pageable);

        Page<TransactionResponse> transactions = payments.map(payment ->
            TransactionResponse.builder()
                .transactionId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .type("PAYMENT")
                .status(payment.getStatus().toString())
                .description(payment.getMetadata())
                .createdAt(payment.getCreatedAt())
                .build()
        );

        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction details
     * Story 1.5.2: Transaction details with full details
     * GET /api/transactions/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction details", description = "Retrieve full details of a specific transaction")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String id) {
        log.info("Fetching transaction: {}", id);
        PaymentStatusResponse paymentStatus = paymentService.getPaymentStatus(id);

        // Map PaymentStatusResponse to TransactionResponse
        TransactionResponse transaction = TransactionResponse.builder()
            .transactionId(paymentStatus.getPaymentId())
            .orderId(paymentStatus.getOrderId())
            .amount(paymentStatus.getAmount())
            .currency(paymentStatus.getCurrency())
            .type("PAYMENT")
            .status(paymentStatus.getStatus())
            .createdAt(paymentStatus.getCreatedAt())
            .build();

        return ResponseEntity.ok(transaction);
    }

    /**
     * Get admin transactions view (all transactions)
     * Story 1.5.3: Admin transaction view
     * GET /api/admin/transactions
     */
    @GetMapping("/admin/transactions")
    @Operation(summary = "Admin transactions view", description = "Admin endpoint: View all transactions in the system")
    public ResponseEntity<String> getAdminTransactions() {
        log.info("Admin request: Fetching all transactions");
        return ResponseEntity.ok("{\"message\":\"Admin transactions endpoint - implement as needed\"}");
    }

    /**
     * Export transactions to CSV
     * Story 1.5.4: Transaction export
     * GET /api/transactions/export
     */
    @GetMapping("/export")
    @Operation(summary = "Export transactions", description = "Export user transactions to CSV format")
    public ResponseEntity<String> exportTransactions(
            @RequestParam String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Exporting transactions for user: {} from {} to {}", userId, startDate, endDate);
        return ResponseEntity.ok("CSV export - implement as needed");
    }
}
