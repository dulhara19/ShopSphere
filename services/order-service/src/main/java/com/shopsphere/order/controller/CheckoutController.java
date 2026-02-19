package com.shopsphere.order.controller;

import com.shopsphere.order.dto.CartValidationDto;
import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderTotalsDto;
import com.shopsphere.order.dto.request.CheckoutRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.service.CartService;
import com.shopsphere.order.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "Cart validation and checkout operations")
public class CheckoutController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    @PostMapping("/cart/validate")
    @Operation(summary = "Validate cart", description = "Validates cart items for availability and pricing before checkout")
    public ResponseEntity<ApiResponse<CartValidationDto>> validateCart(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        CartValidationDto validation = cartService.validateCart(userId);
        return ResponseEntity.ok(ApiResponse.success(validation));
    }

    @GetMapping("/cart/totals")
    @Operation(summary = "Calculate totals", description = "Calculates order totals including tax, shipping, and discounts")
    public ResponseEntity<ApiResponse<OrderTotalsDto>> calculateTotals(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Shipping address ID for delivery calculation") @RequestParam(required = false) UUID shippingAddressId,
        @Parameter(description = "Coupon code to apply") @RequestParam(required = false) String couponCode
    ) {
        OrderTotalsDto totals = checkoutService.calculateTotals(userId, shippingAddressId, couponCode);
        return ResponseEntity.ok(ApiResponse.success(totals));
    }

    @PostMapping("/orders/checkout")
    @Operation(summary = "Checkout", description = "Creates an order from the user's cart")
    public ResponseEntity<ApiResponse<OrderDto>> checkout(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Valid @RequestBody CheckoutRequest request
    ) {
        OrderDto order = checkoutService.checkout(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
    }
}
