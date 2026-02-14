package com.shopsphere.order.controller;

import com.shopsphere.order.dto.CartDto;
import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.dto.response.ApiResponse;
import com.shopsphere.order.service.GuestCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/guest/cart")
@RequiredArgsConstructor
@Tag(name = "Guest Cart", description = "Shopping cart operations for unauthenticated users")
public class GuestCartController {

    private final GuestCartService guestCartService;

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get guest cart", description = "Retrieves a guest cart by session ID")
    public ResponseEntity<ApiResponse<CartDto>> getCart(
        @Parameter(description = "Guest session ID") @PathVariable String sessionId
    ) {
        CartDto cart = guestCartService.getOrCreateCart(sessionId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/{sessionId}/items")
    @Operation(summary = "Add item to guest cart", description = "Adds a product to the guest cart")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
        @Parameter(description = "Guest session ID") @PathVariable String sessionId,
        @Valid @RequestBody AddToCartRequest request
    ) {
        CartDto cart = guestCartService.addToCart(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PutMapping("/{sessionId}/items/{itemId}")
    @Operation(summary = "Update guest cart item", description = "Updates the quantity of an item in the guest cart")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
        @Parameter(description = "Guest session ID") @PathVariable String sessionId,
        @Parameter(description = "Cart item ID") @PathVariable UUID itemId,
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
        CartDto cart = guestCartService.updateCartItem(sessionId, itemId, request);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/{sessionId}/items/{itemId}")
    @Operation(summary = "Remove item from guest cart", description = "Removes an item from the guest cart")
    public ResponseEntity<ApiResponse<CartDto>> removeCartItem(
        @Parameter(description = "Guest session ID") @PathVariable String sessionId,
        @Parameter(description = "Cart item ID") @PathVariable UUID itemId
    ) {
        CartDto cart = guestCartService.removeCartItem(sessionId, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Clear guest cart", description = "Removes all items from the guest cart")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(
        @Parameter(description = "Guest session ID") @PathVariable String sessionId
    ) {
        CartDto cart = guestCartService.clearCart(sessionId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }
}
