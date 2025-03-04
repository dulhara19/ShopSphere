package com.shopsphere.order.integration;

import com.shopsphere.order.dto.request.CheckoutRequest;
import com.shopsphere.order.model.Cart;
import com.shopsphere.order.model.CartItem;
import com.shopsphere.order.model.PaymentMethod;
import com.shopsphere.order.repository.CartRepository;
import com.shopsphere.order.repository.OrderRepository;
import com.shopsphere.order.service.IdempotencyService;
import com.shopsphere.order.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CheckoutControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private IdempotencyService idempotencyService;

    private UUID userId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        userId = UUID.randomUUID();

        cart = Cart.builder()
            .userId(userId)
            .build();

        CartItem item = CartItem.builder()
            .productId(UUID.randomUUID())
            .productName("Test Product")
            .productImage("https://example.com/image.jpg")
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(50.00))
            .build();
        cart.addItem(item);
        cart = cartRepository.save(cart);
    }

    @Nested
    @DisplayName("POST /api/cart/validate")
    class ValidateCartTests {

        @Test
        @DisplayName("should validate cart successfully")
        void shouldValidateCartSuccessfully() throws Exception {
            mockMvc.perform(post("/api/cart/validate")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true));
        }
    }

    @Nested
    @DisplayName("GET /api/cart/totals")
    class CalculateTotalsTests {

        @Test
        @DisplayName("should calculate totals correctly")
        void shouldCalculateTotalsCorrectly() throws Exception {
            mockMvc.perform(get("/api/cart/totals")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.subtotal").value(100.00))
                .andExpect(jsonPath("$.data.taxAmount").value(8.00))
                .andExpect(jsonPath("$.data.shippingAmount").value(0)); // Free shipping over $50
        }

        @Test
        @DisplayName("should apply shipping for orders under threshold")
        void shouldApplyShippingForOrdersUnderThreshold() throws Exception {
            // Create cart with smaller total
            cart.clearItems();
            CartItem smallItem = CartItem.builder()
                .productId(UUID.randomUUID())
                .productName("Small Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(30.00))
                .build();
            cart.addItem(smallItem);
            cartRepository.save(cart);

            mockMvc.perform(get("/api/cart/totals")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shippingAmount").value(5.99));
        }

        @Test
        @DisplayName("should apply discount with coupon")
        void shouldApplyDiscountWithCoupon() throws Exception {
            mockMvc.perform(get("/api/cart/totals")
                    .param("couponCode", "SAVE10")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.discountAmount").value(10.00))
                .andExpect(jsonPath("$.data.discountCode").value("SAVE10"));
        }
    }

    @Nested
    @DisplayName("POST /api/orders/checkout")
    class CheckoutTests {

        @Test
        @DisplayName("should create order successfully")
        void shouldCreateOrderSuccessfully() throws Exception {
            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);
            when(inventoryService.reserveInventory(any(), any())).thenReturn(true);

            CheckoutRequest request = CheckoutRequest.builder()
                .shippingAddressId(UUID.randomUUID())
                .billingAddressId(UUID.randomUUID())
                .paymentMethod(PaymentMethod.CARD)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

            mockMvc.perform(post("/api/orders/checkout")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").exists())
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.items", hasSize(1)));
        }

        @Test
        @DisplayName("should reject checkout with empty cart")
        void shouldRejectCheckoutWithEmptyCart() throws Exception {
            cart.clearItems();
            cartRepository.save(cart);

            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);

            CheckoutRequest request = CheckoutRequest.builder()
                .shippingAddressId(UUID.randomUUID())
                .billingAddressId(UUID.randomUUID())
                .paymentMethod(PaymentMethod.CARD)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

            mockMvc.perform(post("/api/orders/checkout")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should reject checkout when inventory unavailable")
        void shouldRejectCheckoutWhenInventoryUnavailable() throws Exception {
            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);
            when(inventoryService.reserveInventory(any(), any())).thenReturn(false);

            CheckoutRequest request = CheckoutRequest.builder()
                .shippingAddressId(UUID.randomUUID())
                .billingAddressId(UUID.randomUUID())
                .paymentMethod(PaymentMethod.CARD)
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

            mockMvc.perform(post("/api/orders/checkout")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
        }
    }
}
