package com.shopsphere.order.integration;

import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.model.Cart;
import com.shopsphere.order.model.CartItem;
import com.shopsphere.order.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    private UUID userId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();
        userId = UUID.randomUUID();

        cart = Cart.builder()
            .userId(userId)
            .build();
        cart = cartRepository.save(cart);
    }

    @Nested
    @DisplayName("GET /api/cart")
    class GetCartTests {

        @Test
        @DisplayName("should return empty cart for new user")
        void shouldReturnEmptyCartForNewUser() throws Exception {
            mockMvc.perform(get("/api/cart")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(0)));
        }

        @Test
        @DisplayName("should return 401 without authentication")
        void shouldReturn401WithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/cart")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/cart/items")
    class AddToCartTests {

        @Test
        @DisplayName("should add item to cart")
        void shouldAddItemToCart() throws Exception {
            AddToCartRequest request = AddToCartRequest.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .build();

            mockMvc.perform(post("/api/cart/items")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(1)))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));
        }

        @Test
        @DisplayName("should reject invalid quantity")
        void shouldRejectInvalidQuantity() throws Exception {
            AddToCartRequest request = AddToCartRequest.builder()
                .productId(UUID.randomUUID())
                .quantity(0)
                .build();

            mockMvc.perform(post("/api/cart/items")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/cart/items/{itemId}")
    class UpdateCartItemTests {

        @Test
        @DisplayName("should update cart item quantity")
        void shouldUpdateCartItemQuantity() throws Exception {
            // Add item first
            CartItem item = CartItem.builder()
                .productId(UUID.randomUUID())
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(10.00))
                .build();
            cart.addItem(item);
            cart = cartRepository.save(cart);

            UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(5)
                .build();

            mockMvc.perform(put("/api/cart/items/" + item.getId())
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].quantity").value(5));
        }
    }

    @Nested
    @DisplayName("DELETE /api/cart/items/{itemId}")
    class RemoveCartItemTests {

        @Test
        @DisplayName("should remove item from cart")
        void shouldRemoveItemFromCart() throws Exception {
            CartItem item = CartItem.builder()
                .productId(UUID.randomUUID())
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(10.00))
                .build();
            cart.addItem(item);
            cart = cartRepository.save(cart);

            mockMvc.perform(delete("/api/cart/items/" + item.getId())
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("DELETE /api/cart")
    class ClearCartTests {

        @Test
        @DisplayName("should clear all items from cart")
        void shouldClearAllItemsFromCart() throws Exception {
            CartItem item1 = CartItem.builder()
                .productId(UUID.randomUUID())
                .productName("Product 1")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(10.00))
                .build();
            CartItem item2 = CartItem.builder()
                .productId(UUID.randomUUID())
                .productName("Product 2")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(20.00))
                .build();
            cart.addItem(item1);
            cart.addItem(item2);
            cart = cartRepository.save(cart);

            mockMvc.perform(delete("/api/cart")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items", hasSize(0)));
        }
    }
}
