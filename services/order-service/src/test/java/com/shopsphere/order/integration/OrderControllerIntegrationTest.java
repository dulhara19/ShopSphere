package com.shopsphere.order.integration;

import com.shopsphere.order.dto.request.CancelOrderRequest;
import com.shopsphere.order.model.*;
import com.shopsphere.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    private UUID userId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userId = UUID.randomUUID();

        order = Order.builder()
            .orderNumber("ORD-TEST-12345678")
            .userId(userId)
            .status(OrderStatus.PENDING)
            .shippingAddress(Address.builder()
                .fullName("John Doe")
                .addressLine1("123 Test St")
                .city("Test City")
                .state("TS")
                .postalCode("12345")
                .country("USA")
                .build())
            .billingAddress(Address.builder()
                .fullName("John Doe")
                .addressLine1("123 Test St")
                .city("Test City")
                .state("TS")
                .postalCode("12345")
                .country("USA")
                .build())
            .subtotal(BigDecimal.valueOf(100.00))
            .taxAmount(BigDecimal.valueOf(8.00))
            .shippingAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(BigDecimal.valueOf(108.00))
            .paymentStatus(PaymentStatus.PENDING)
            .build();

        OrderItem item = OrderItem.builder()
            .productId(UUID.randomUUID().toString())
            .productName("Test Product")
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(50.00))
            .totalPrice(BigDecimal.valueOf(100.00))
            .build();
        order.addItem(item);

        OrderStatusHistory history = OrderStatusHistory.builder()
            .status(OrderStatus.PENDING)
            .note("Order created")
            .updatedBy("SYSTEM")
            .build();
        order.addStatusHistory(history);

        order = orderRepository.save(order);
    }

    @Nested
    @DisplayName("GET /api/orders")
    class ListOrdersTests {

        @Test
        @DisplayName("should return paginated orders")
        void shouldReturnPaginatedOrders() throws Exception {
            mockMvc.perform(get("/api/orders")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].orderNumber").value("ORD-TEST-12345678"));
        }

        @Test
        @DisplayName("should filter by status")
        void shouldFilterByStatus() throws Exception {
            mockMvc.perform(get("/api/orders")
                    .param("status", "PENDING")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
        }

        @Test
        @DisplayName("should return empty for non-matching status")
        void shouldReturnEmptyForNonMatchingStatus() throws Exception {
            mockMvc.perform(get("/api/orders")
                    .param("status", "SHIPPED")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId}")
    class GetOrderTests {

        @Test
        @DisplayName("should return order details")
        void shouldReturnOrderDetails() throws Exception {
            mockMvc.perform(get("/api/orders/" + order.getId())
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-TEST-12345678"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.items", hasSize(1)));
        }

        @Test
        @DisplayName("should return 404 for non-existent order")
        void shouldReturn404ForNonExistentOrder() throws Exception {
            mockMvc.perform(get("/api/orders/" + UUID.randomUUID())
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 404 for other user's order")
        void shouldReturn404ForOtherUsersOrder() throws Exception {
            mockMvc.perform(get("/api/orders/" + order.getId())
                    .with(user(UUID.randomUUID().toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/orders/{orderId}/status")
    class GetOrderStatusTests {

        @Test
        @DisplayName("should return order status with history")
        void shouldReturnOrderStatusWithHistory() throws Exception {
            mockMvc.perform(get("/api/orders/" + order.getId() + "/status")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.history", hasSize(greaterThanOrEqualTo(1))));
        }
    }

    @Nested
    @DisplayName("POST /api/orders/{orderId}/cancel")
    class CancelOrderTests {

        @Test
        @DisplayName("should cancel pending order")
        void shouldCancelPendingOrder() throws Exception {
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind")
                .build();

            mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        }

        @Test
        @DisplayName("should reject cancellation of shipped order")
        void shouldRejectCancellationOfShippedOrder() throws Exception {
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);

            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Want to cancel")
                .build();

            mockMvc.perform(post("/api/orders/" + order.getId() + "/cancel")
                    .with(user(userId.toString()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }
}
