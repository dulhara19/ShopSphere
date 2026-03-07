package com.shopsphere.order.service;

import com.shopsphere.order.dto.CartDto;
import com.shopsphere.order.dto.CartValidationDto;
import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.exception.CartNotFoundException;
import com.shopsphere.order.exception.CartValidationException;
import com.shopsphere.order.model.Cart;
import com.shopsphere.order.model.CartItem;
import com.shopsphere.order.repository.CartItemRepository;
import com.shopsphere.order.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cart = Cart.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .items(new ArrayList<>())
            .build();
    }

    @Nested
    @DisplayName("getCart")
    class GetCartTests {

        @Test
        @DisplayName("should return existing cart")
        void shouldReturnExistingCart() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            CartDto result = cartService.getCart(userId);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("should return empty cart for new user")
        void shouldReturnEmptyCartForNewUser() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());

            CartDto result = cartService.getCart(userId);

            assertThat(result).isNotNull();
            assertThat(result.getItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("addToCart")
    class AddToCartTests {

        @Test
        @DisplayName("should add new item to cart")
        void shouldAddNewItemToCart() {
            String productId = UUID.randomUUID().toString();
            AddToCartRequest request = AddToCartRequest.builder()
                .productId(productId)
                .quantity(2)
                .build();

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDto result = cartService.addToCart(userId, request);

            assertThat(result).isNotNull();
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("should update quantity when adding existing item")
        void shouldUpdateQuantityWhenAddingExistingItem() {
            String productId = UUID.randomUUID().toString();
            CartItem existingItem = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(99.99))
                .build();
            cart.addItem(existingItem);

            AddToCartRequest request = AddToCartRequest.builder()
                .productId(productId)
                .quantity(2)
                .build();

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            cartService.addToCart(userId, request);

            assertThat(existingItem.getQuantity()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw exception when exceeding max quantity")
        void shouldThrowExceptionWhenExceedingMaxQuantity() {
            String productId = UUID.randomUUID().toString();
            CartItem existingItem = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .productName("Test Product")
                .quantity(95)
                .unitPrice(BigDecimal.valueOf(99.99))
                .build();
            cart.addItem(existingItem);

            AddToCartRequest request = AddToCartRequest.builder()
                .productId(productId)
                .quantity(10)
                .build();

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> cartService.addToCart(userId, request))
                .isInstanceOf(CartValidationException.class)
                .hasMessageContaining("Maximum quantity");
        }

        @Test
        @DisplayName("should throw exception when exceeding max cart items")
        void shouldThrowExceptionWhenExceedingMaxCartItems() {
            // Add 50 items to cart
            for (int i = 0; i < 50; i++) {
                CartItem item = CartItem.builder()
                    .id(UUID.randomUUID())
                    .productId(UUID.randomUUID().toString())
                    .productName("Product " + i)
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(10))
                    .build();
                cart.addItem(item);
            }

            AddToCartRequest request = AddToCartRequest.builder()
                .productId(UUID.randomUUID().toString())
                .quantity(1)
                .build();

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> cartService.addToCart(userId, request))
                .isInstanceOf(CartValidationException.class)
                .hasMessageContaining("cannot have more than");
        }
    }

    @Nested
    @DisplayName("validateCart")
    class ValidateCartTests {

        @Test
        @DisplayName("should return valid for cart with items")
        void shouldReturnValidForCartWithItems() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(99.99))
                .priceAtAdd(BigDecimal.valueOf(99.99))
                .build();
            cart.addItem(item);

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            CartValidationDto result = cartService.validateCart(userId);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getIssues()).isEmpty();
        }

        @Test
        @DisplayName("should return invalid for empty cart")
        void shouldReturnInvalidForEmptyCart() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            CartValidationDto result = cartService.validateCart(userId);

            assertThat(result.isValid()).isFalse();
        }

        @Test
        @DisplayName("should throw exception for non-existent cart")
        void shouldThrowExceptionForNonExistentCart() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cartService.validateCart(userId))
                .isInstanceOf(CartNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("applyCoupon")
    class ApplyCouponTests {

        @Test
        @DisplayName("should apply valid coupon")
        void shouldApplyValidCoupon() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDto result = cartService.applyCoupon(userId, "SAVE10");

            assertThat(cart.getCouponCode()).isEqualTo("SAVE10");
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("should throw exception for invalid coupon format")
        void shouldThrowExceptionForInvalidCouponFormat() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> cartService.applyCoupon(userId, "invalid"))
                .isInstanceOf(CartValidationException.class)
                .hasMessageContaining("Invalid coupon code");
        }

        @Test
        @DisplayName("should throw exception for empty coupon")
        void shouldThrowExceptionForEmptyCoupon() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> cartService.applyCoupon(userId, ""))
                .isInstanceOf(CartValidationException.class);
        }
    }

    @Nested
    @DisplayName("removeCoupon")
    class RemoveCouponTests {

        @Test
        @DisplayName("should remove coupon from cart")
        void shouldRemoveCouponFromCart() {
            cart.setCouponCode("SAVE10");
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            cartService.removeCoupon(userId);

            assertThat(cart.getCouponCode()).isNull();
            verify(cartRepository).save(cart);
        }
    }

    @Nested
    @DisplayName("clearCart")
    class ClearCartTests {

        @Test
        @DisplayName("should clear all items from cart")
        void shouldClearAllItemsFromCart() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(99.99))
                .build();
            cart.addItem(item);

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            CartDto result = cartService.clearCart(userId);

            assertThat(cart.getItems()).isEmpty();
            verify(cartRepository).save(cart);
        }
    }
}
