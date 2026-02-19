package com.shopsphere.order.controller;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.ShippingDetailsDto;
import com.shopsphere.order.dto.request.UpdateStatusRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
@Tag(name = "Internal Orders", description = "Internal service-to-service order operations")
public class InternalOrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order (internal)", description = "Retrieves order details for internal services")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(
        @Parameter(description = "Order ID") @PathVariable UUID orderId,
        @Parameter(description = "Calling service name") @RequestHeader("X-Service-Name") String serviceName
    ) {
        OrderDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status (internal)", description = "Updates order status from internal services")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
        @Parameter(description = "Order ID") @PathVariable UUID orderId,
        @Valid @RequestBody UpdateStatusRequest request,
        @Parameter(description = "Calling service name") @RequestHeader("X-Service-Name") String serviceName
    ) {
        OrderDto order = orderService.updateOrderStatus(orderId, request, "SERVICE:" + serviceName);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/{orderId}/shipping-details")
    @Operation(summary = "Get shipping details (internal)", description = "Retrieves shipping details for fulfillment services")
    public ResponseEntity<ApiResponse<ShippingDetailsDto>> getShippingDetails(
        @Parameter(description = "Order ID") @PathVariable UUID orderId,
        @Parameter(description = "Calling service name") @RequestHeader("X-Service-Name") String serviceName
    ) {
        ShippingDetailsDto details = orderService.getShippingDetails(orderId);
        return ResponseEntity.ok(ApiResponse.success(details));
    }
}
