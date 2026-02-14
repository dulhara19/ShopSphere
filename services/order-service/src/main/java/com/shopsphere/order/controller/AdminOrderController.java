package com.shopsphere.order.controller;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderStatusHistoryDto;
import com.shopsphere.order.dto.request.AddInternalNoteRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.dto.response.PaginationMeta;
import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.model.OrderStatusHistory;
import com.shopsphere.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Orders", description = "Administrative order management operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List all orders", description = "Retrieves paginated list of all orders with filtering options")
    public ResponseEntity<ApiResponse<List<OrderDto>>> listAllOrders(
        @Parameter(description = "Filter by order status") @RequestParam(required = false) OrderStatus status,
        @Parameter(description = "Filter by user ID") @RequestParam(required = false) UUID userId,
        @Parameter(description = "Search by order number") @RequestParam(required = false) String orderNumber,
        @Parameter(description = "Filter orders from date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @Parameter(description = "Filter orders to date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") @Min(1) int page,
        @Parameter(description = "Items per page (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        List<OrderDto> orders = orderService.listAllOrders(status, userId, orderNumber, fromDate, toDate, page, limit);
        PaginationMeta meta = orderService.getAdminOrdersPaginationMeta(status, userId, orderNumber, fromDate, toDate, page, limit);
        return ResponseEntity.ok(ApiResponse.success(orders, meta));
    }

    @PostMapping("/{orderId}/notes")
    @Operation(summary = "Add internal note", description = "Adds an internal note to an order (not visible to customer)")
    public ResponseEntity<ApiResponse<OrderDto>> addInternalNote(
        @Parameter(description = "Order ID") @PathVariable UUID orderId,
        @Valid @RequestBody AddInternalNoteRequest request,
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        String adminUser = userDetails != null ? userDetails.getUsername() : "ADMIN";
        OrderDto order = orderService.addInternalNote(orderId, request.getNote(), adminUser);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/{orderId}/audit-trail")
    @Operation(summary = "Get audit trail", description = "Retrieves complete status change history for an order")
    public ResponseEntity<ApiResponse<List<OrderStatusHistoryDto>>> getAuditTrail(
        @Parameter(description = "Order ID") @PathVariable UUID orderId
    ) {
        List<OrderStatusHistory> history = orderService.getOrderAuditTrail(orderId);
        List<OrderStatusHistoryDto> historyDtos = history.stream()
            .map(OrderStatusHistoryDto::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(historyDtos));
    }
}
