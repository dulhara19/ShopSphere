package com.shopsphere.order.controller;

import com.shopsphere.order.dto.CartDto;
import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.ApplyCouponRequest;
import com.shopsphere.order.dto.request.MergeCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Shopping cart management operations")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user's cart", description = "Retrieves the current user's shopping cart with all items")
    public ResponseEntity<ApiResponse<CartDto>> getCart(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        CartDto cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the user's shopping cart")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Valid @RequestBody AddToCartRequest request
    ) {
        CartDto cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Updates the quantity of an item in the cart")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Cart item ID") @PathVariable UUID itemId,
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartDto cart = cartService.updateCartItem(userId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes an item from the cart")
    public ResponseEntity<ApiResponse<CartDto>> removeCartItem(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Parameter(description = "Cart item ID") @PathVariable UUID itemId
    ) {
        CartDto cart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        CartDto cart = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart", description = "Merges a guest cart with the authenticated user's cart after login")
    public ResponseEntity<ApiResponse<CartDto>> mergeCart(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Valid @RequestBody MergeCartRequest request
    ) {
        CartDto cart = cartService.mergeCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply coupon", description = "Applies a discount coupon to the cart")
    public ResponseEntity<ApiResponse<CartDto>> applyCoupon(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId,
        @Valid @RequestBody ApplyCouponRequest request
    ) {
        CartDto cart = cartService.applyCoupon(userId, request.getCouponCode());
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove coupon", description = "Removes the applied coupon from the cart")
    public ResponseEntity<ApiResponse<CartDto>> removeCoupon(
        @Parameter(hidden = true) @AuthenticationPrincipal UUID userId
    ) {
        CartDto cart = cartService.removeCoupon(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }
}
