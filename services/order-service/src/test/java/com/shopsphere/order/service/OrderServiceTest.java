package com.shopsphere.order.service;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderStatusResponseDto;
import com.shopsphere.order.dto.request.CancelOrderRequest;
import com.shopsphere.order.dto.request.UpdateStatusRequest;
import com.shopsphere.order.exception.InvalidStatusTransitionException;
import com.shopsphere.order.exception.OrderCancellationException;
import com.shopsphere.order.exception.OrderNotFoundException;
import com.shopsphere.order.model.*;
import com.shopsphere.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        order = Order.builder()
            .id(orderId)
            .orderNumber("ORD-20260214-TEST1234")
            .userId(userId)
            .status(OrderStatus.PENDING)
            .items(new ArrayList<>())
            .statusHistory(new LinkedHashSet<>())
            .shippingAddress(Address.builder().fullName("John Doe").build())
            .billingAddress(Address.builder().fullName("John Doe").build())
            .subtotal(BigDecimal.valueOf(100.00))
            .taxAmount(BigDecimal.valueOf(8.00))
            .shippingAmount(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(BigDecimal.valueOf(108.00))
            .paymentStatus(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Nested
    @DisplayName("getOrder")
    class GetOrderTests {

        @Test
        @DisplayName("should return order for valid user")
        void shouldReturnOrderForValidUser() {
            when(orderRepository.findByIdAndUserIdWithDetails(orderId, userId))
                .thenReturn(Optional.of(order));

            OrderDto result = orderService.getOrder(orderId, userId);

            assertThat(result).isNotNull();
            assertThat(result.getOrderNumber()).isEqualTo("ORD-20260214-TEST1234");
        }

        @Test
        @DisplayName("should throw exception for non-existent order")
        void shouldThrowExceptionForNonExistentOrder() {
            when(orderRepository.findByIdAndUserIdWithDetails(orderId, userId))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrder(orderId, userId))
                .isInstanceOf(OrderNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getOrderStatus")
    class GetOrderStatusTests {

        @Test
        @DisplayName("should return order status")
        void shouldReturnOrderStatus() {
            OrderStatusHistory history = OrderStatusHistory.builder()
                .status(OrderStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .note("Order created")
                .updatedBy("SYSTEM")
                .build();
            order.addStatusHistory(history);

            when(orderRepository.findByIdAndUserIdWithDetails(orderId, userId))
                .thenReturn(Optional.of(order));

            OrderStatusResponseDto result = orderService.getOrderStatus(orderId, userId);

            assertThat(result).isNotNull();
            assertThat(result.getCurrentStatus()).isEqualTo(OrderStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("cancelOrder")
    class CancelOrderTests {

        @Test
        @DisplayName("should cancel pending order")
        void shouldCancelPendingOrder() {
            OrderItem item = OrderItem.builder()
                .productId(UUID.randomUUID())
                .quantity(1)
                .build();
            order.addItem(item);

            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind")
                .build();

            when(orderRepository.findByIdAndUserIdWithDetails(orderId, userId))
                .thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            OrderDto result = orderService.cancelOrder(orderId, userId, request);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            verify(eventPublisher).publishOrderCancelled(order);
            verify(inventoryService).releaseInventory(eq(orderId), any());
        }

        @Test
        @DisplayName("should throw exception when cancelling shipped order")
        void shouldThrowExceptionWhenCancellingShippedOrder() {
            order.setStatus(OrderStatus.SHIPPED);
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind")
                .build();

            when(orderRepository.findByIdAndUserIdWithDetails(orderId, userId))
                .thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.cancelOrder(orderId, userId, request))
                .isInstanceOf(OrderCancellationException.class);
        }
    }

    @Nested
    @DisplayName("updateOrderStatus")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("should update status from PENDING to CONFIRMED")
        void shouldUpdateStatusFromPendingToConfirmed() {
            UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(OrderStatus.CONFIRMED)
                .note("Payment verified")
                .build();

            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            OrderDto result = orderService.updateOrderStatus(orderId, request, "ADMIN");

            assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
            verify(eventPublisher).publishOrderConfirmed(order);
        }

        @Test
        @DisplayName("should update to SHIPPED with tracking number")
        void shouldUpdateToShippedWithTrackingNumber() {
            order.setStatus(OrderStatus.PROCESSING);
            UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(OrderStatus.SHIPPED)
                .note("Shipped via FedEx")
                .trackingNumber("FX123456789")
                .build();

            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            orderService.updateOrderStatus(orderId, request, "ADMIN");

            assertThat(order.getTrackingNumber()).isEqualTo("FX123456789");
            verify(eventPublisher).publishOrderShipped(order);
        }

        @Test
        @DisplayName("should throw exception for invalid status transition")
        void shouldThrowExceptionForInvalidStatusTransition() {
            UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(OrderStatus.DELIVERED)
                .note("Invalid transition")
                .build();

            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, request, "ADMIN"))
                .isInstanceOf(InvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("listOrders")
    class ListOrdersTests {

        @Test
        @DisplayName("should return paginated orders")
        void shouldReturnPaginatedOrders() {
            Page<Order> page = new PageImpl<>(List.of(order));

            when(orderRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

            List<OrderDto> result = orderService.listOrders(userId, null, "createdAt", "desc", 1, 10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrderNumber()).isEqualTo("ORD-20260214-TEST1234");
        }

        @Test
        @DisplayName("should filter by status")
        void shouldFilterByStatus() {
            Page<Order> page = new PageImpl<>(List.of(order));

            when(orderRepository.findByUserIdAndStatus(eq(userId), eq(OrderStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);

            List<OrderDto> result = orderService.listOrders(userId, OrderStatus.PENDING, "createdAt", "desc", 1, 10);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("addInternalNote")
    class AddInternalNoteTests {

        @Test
        @DisplayName("should add internal note to order")
        void shouldAddInternalNoteToOrder() {
            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            OrderDto result = orderService.addInternalNote(orderId, "Customer called about delivery", "ADMIN");

            assertThat(order.getInternalNotes()).contains("Customer called about delivery");
            assertThat(order.getInternalNotes()).contains("ADMIN");
        }

        @Test
        @DisplayName("should append to existing notes")
        void shouldAppendToExistingNotes() {
            order.setInternalNotes("Previous note");

            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            orderService.addInternalNote(orderId, "New note", "ADMIN");

            assertThat(order.getInternalNotes()).contains("Previous note");
            assertThat(order.getInternalNotes()).contains("New note");
        }
    }

    @Nested
    @DisplayName("getOrderAuditTrail")
    class GetOrderAuditTrailTests {

        @Test
        @DisplayName("should return status history")
        void shouldReturnStatusHistory() {
            OrderStatusHistory history1 = OrderStatusHistory.builder()
                .status(OrderStatus.PENDING)
                .timestamp(LocalDateTime.now().minusHours(2))
                .note("Order created")
                .updatedBy("SYSTEM")
                .build();
            OrderStatusHistory history2 = OrderStatusHistory.builder()
                .status(OrderStatus.CONFIRMED)
                .timestamp(LocalDateTime.now())
                .note("Payment verified")
                .updatedBy("ADMIN")
                .build();
            order.addStatusHistory(history1);
            order.addStatusHistory(history2);

            when(orderRepository.findByIdWithDetails(orderId)).thenReturn(Optional.of(order));

            List<OrderStatusHistory> result = orderService.getOrderAuditTrail(orderId);

            assertThat(result).hasSize(2);
        }
    }
}
