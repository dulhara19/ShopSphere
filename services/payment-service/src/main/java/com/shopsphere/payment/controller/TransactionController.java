package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.TransactionResponse;
import com.shopsphere.payment.dto.PaymentStatusResponse;
import com.shopsphere.payment.dto.RefundResponse;
import com.shopsphere.payment.model.Payment;
import com.shopsphere.payment.service.PaymentService;
import com.shopsphere.payment.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @Autowired
    private RefundService refundService;

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

        // fetch payments and refunds separately then merge
        Page<Payment> payments = paymentService.getPaymentHistory(userId, pageable);
        Page<RefundResponse> refunds = refundService.getUserRefunds(userId, pageable);

        // convert to transaction responses
        List<TransactionResponse> combined = new java.util.ArrayList<>();
        payments.forEach(p -> combined.add(TransactionResponse.builder()
                .transactionId(p.getId())
                .orderId(p.getOrderId())
                .userId(p.getUserId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .type("PAYMENT")
                .status(p.getStatus().toString())
                .description(p.getMetadata())
                .createdAt(p.getCreatedAt())
                .build()));
        refunds.forEach(r -> combined.add(TransactionResponse.builder()
                .transactionId(r.getRefundId())
                .orderId(null)
                .userId(userId)
                .amount(r.getAmount().negate()) // refunds negative value to indicate money back
                .currency(null)
                .type("REFUND")
                .status(r.getStatus())
                .description(r.getReason())
                .createdAt(r.getCreatedAt())
                .build()));

        // sort by date desc
        combined.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), combined.size());
        List<TransactionResponse> pageList = combined.subList(start, end);
        Page<TransactionResponse> resultPage = new PageImpl<>(pageList, pageable, combined.size());

        return ResponseEntity.ok(resultPage);
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
        try {
            PaymentStatusResponse paymentStatus = paymentService.getPaymentStatus(id);
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
        } catch (com.shopsphere.payment.exception.PaymentNotFoundException e) {
            // try refund lookup
            RefundResponse refund = refundService.getRefund(id);
            TransactionResponse transaction = TransactionResponse.builder()
                .transactionId(refund.getRefundId())
                .orderId(null)
                .amount(refund.getAmount().negate())
                .currency(null)
                .type("REFUND")
                .status(refund.getStatus())
                .createdAt(refund.getCreatedAt())
                .build();
            return ResponseEntity.ok(transaction);
        }
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
