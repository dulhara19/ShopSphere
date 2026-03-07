package com.shopsphere.order.service;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderTotalsDto;
import com.shopsphere.order.dto.request.CheckoutRequest;
import com.shopsphere.order.exception.CartNotFoundException;
import com.shopsphere.order.exception.EmptyCartException;
import com.shopsphere.order.model.*;
import com.shopsphere.order.repository.CartRepository;
import com.shopsphere.order.repository.OrderRepository;
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
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private CheckoutService checkoutService;

    private UUID userId;
    private Cart cart;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cart = Cart.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .items(new ArrayList<>())
            .build();

        checkoutRequest = CheckoutRequest.builder()
            .shippingAddressId(UUID.randomUUID())
            .billingAddressId(UUID.randomUUID())
            .paymentMethod(PaymentMethod.CARD)
            .build();
    }

    @Nested
    @DisplayName("calculateTotals")
    class CalculateTotalsTests {

        @Test
        @DisplayName("should calculate totals correctly")
        void shouldCalculateTotalsCorrectly() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(2)
                .unitPrice(BigDecimal.valueOf(50.00))
                .build();
            cart.addItem(item);

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            OrderTotalsDto result = checkoutService.calculateTotals(userId, UUID.randomUUID(), null);

            assertThat(result).isNotNull();
            assertThat(result.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
            assertThat(result.getTaxAmount()).isEqualByComparingTo(BigDecimal.valueOf(8.00)); // 8% tax
            assertThat(result.getShippingAmount()).isEqualByComparingTo(BigDecimal.ZERO); // Free shipping over $50
        }

        @Test
        @DisplayName("should apply shipping for orders under threshold")
        void shouldApplyShippingForOrdersUnderThreshold() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(30.00))
                .build();
            cart.addItem(item);

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            OrderTotalsDto result = checkoutService.calculateTotals(userId, UUID.randomUUID(), null);

            assertThat(result.getShippingAmount()).isEqualByComparingTo(BigDecimal.valueOf(5.99));
        }

        @Test
        @DisplayName("should apply discount with coupon code")
        void shouldApplyDiscountWithCouponCode() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(100.00))
                .build();
            cart.addItem(item);

            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            OrderTotalsDto result = checkoutService.calculateTotals(userId, UUID.randomUUID(), "SAVE10");

            assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.valueOf(10.00)); // 10% discount
            assertThat(result.getDiscountCode()).isEqualTo("SAVE10");
        }

        @Test
        @DisplayName("should throw exception for non-existent cart")
        void shouldThrowExceptionForNonExistentCart() {
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> checkoutService.calculateTotals(userId, UUID.randomUUID(), null))
                .isInstanceOf(CartNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("checkout")
    class CheckoutTests {

        @BeforeEach
        void setUpCartWithItem() {
            CartItem item = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID().toString())
                .productName("Test Product")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(100.00))
                .build();
            cart.addItem(item);
        }

        @Test
        @DisplayName("should create order successfully")
        void shouldCreateOrderSuccessfully() {
            Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-20260214-ABC12345")
                .userId(userId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .statusHistory(new LinkedHashSet<>())
                .subtotal(BigDecimal.valueOf(100.00))
                .taxAmount(BigDecimal.valueOf(8.00))
                .shippingAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(108.00))
                .build();

            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(inventoryService.reserveInventory(any(), any())).thenReturn(true);
            when(orderRepository.existsByOrderNumber(any())).thenReturn(false);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);

            OrderDto result = checkoutService.checkout(userId, checkoutRequest);

            assertThat(result).isNotNull();
            verify(orderRepository).save(any(Order.class));
            verify(cartRepository).save(any(Cart.class));
            verify(eventPublisher).publishOrderCreated(any(Order.class));
            verify(idempotencyService).markCompleted(any(), eq(userId), any());
        }

        @Test
        @DisplayName("should throw exception for empty cart")
        void shouldThrowExceptionForEmptyCart() {
            cart.clearItems();

            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

            assertThatThrownBy(() -> checkoutService.checkout(userId, checkoutRequest))
                .isInstanceOf(EmptyCartException.class);

            verify(idempotencyService).release(any(), eq(userId));
        }

        @Test
        @DisplayName("should throw exception when inventory reservation fails")
        void shouldThrowExceptionWhenInventoryReservationFails() {
            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire(any(), any())).thenReturn(true);
            when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
            when(inventoryService.reserveInventory(any(), any())).thenReturn(false);

            assertThatThrownBy(() -> checkoutService.checkout(userId, checkoutRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("inventory");

            verify(idempotencyService).release(any(), eq(userId));
        }

        @Test
        @DisplayName("should return existing order for duplicate idempotency key")
        void shouldReturnExistingOrderForDuplicateIdempotencyKey() {
            UUID existingOrderId = UUID.randomUUID();
            Order existingOrder = Order.builder()
                .id(existingOrderId)
                .orderNumber("ORD-EXISTING")
                .userId(userId)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .statusHistory(new LinkedHashSet<>())
                .subtotal(BigDecimal.valueOf(100.00))
                .totalAmount(BigDecimal.valueOf(108.00))
                .build();

            checkoutRequest.setIdempotencyKey("unique-key-123");
            when(idempotencyService.getExistingOrderId("unique-key-123", userId)).thenReturn(existingOrderId);
            when(orderRepository.findByIdWithDetails(existingOrderId)).thenReturn(Optional.of(existingOrder));

            OrderDto result = checkoutService.checkout(userId, checkoutRequest);

            assertThat(result).isNotNull();
            assertThat(result.getOrderNumber()).isEqualTo("ORD-EXISTING");
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("should reject duplicate checkout attempt")
        void shouldRejectDuplicateCheckoutAttempt() {
            checkoutRequest.setIdempotencyKey("unique-key-123");
            when(idempotencyService.getExistingOrderId(any(), any())).thenReturn(null);
            when(idempotencyService.tryAcquire("unique-key-123", userId)).thenReturn(false);

            assertThatThrownBy(() -> checkoutService.checkout(userId, checkoutRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Duplicate checkout");
        }
    }
}
