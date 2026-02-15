package com.shopsphere.order.controller;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderStatusResponseDto;
import com.shopsphere.order.dto.request.CancelOrderRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.dto.response.PaginationMeta;
import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Order management operations for customers")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "List orders", description = "Retrieves paginated list of user's orders with optional filtering")
    public ResponseEntity<ApiResponse<List<OrderDto>>> listOrders(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Filter by order status") @RequestParam(required = false) OrderStatus status,
        @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sort,
        @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "desc") String order,
        @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") @Min(1) int page,
        @Parameter(description = "Items per page (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {
        List<OrderDto> orders = orderService.listOrders(userId, status, sort, order, page, limit);
        PaginationMeta meta = orderService.getOrdersPaginationMeta(userId, status, page, limit);
        return ResponseEntity.ok(ApiResponse.success(orders, meta));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order", description = "Retrieves detailed order information")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Order ID") @PathVariable UUID orderId
    ) {
        OrderDto order = orderService.getOrder(orderId, userId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/{orderId}/status")
    @Operation(summary = "Get order status", description = "Retrieves current status and history of an order")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> getOrderStatus(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Order ID") @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto status = orderService.getOrderStatus(orderId, userId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancels an order if still cancellable")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Order ID") @PathVariable UUID orderId,
        @Valid @RequestBody CancelOrderRequest request
    ) {
        OrderDto order = orderService.cancelOrder(orderId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
}
