package com.shopsphere.order.service;

import com.shopsphere.order.dto.OrderDto;
import com.shopsphere.order.dto.OrderTotalsDto;
import com.shopsphere.order.dto.request.CheckoutRequest;
import com.shopsphere.order.exception.CartNotFoundException;
import com.shopsphere.order.exception.EmptyCartException;
import com.shopsphere.order.model.*;
import com.shopsphere.order.repository.CartRepository;
import com.shopsphere.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8% tax
    private static final BigDecimal SHIPPING_BASE = new BigDecimal("5.99");
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("50.00");
    private static final int MAX_ORDER_NUMBER_RETRIES = 5;

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final InventoryService inventoryService;
    private final IdempotencyService idempotencyService;

    @Transactional(readOnly = true)
    public OrderTotalsDto calculateTotals(UUID userId, UUID shippingAddressId, String couponCode) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shippingAmount = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
            ? BigDecimal.ZERO
            : SHIPPING_BASE;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // In production, validate and apply coupon from Coupon Service
        if (couponCode != null && !couponCode.isBlank()) {
            // Mock: Apply 10% discount
            discountAmount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalAmount = subtotal
            .add(taxAmount)
            .add(shippingAmount)
            .subtract(discountAmount);

        return OrderTotalsDto.builder()
            .subtotal(subtotal)
            .taxAmount(taxAmount)
            .taxRate(TAX_RATE)
            .shippingAmount(shippingAmount)
            .discountAmount(discountAmount)
            .discountCode(couponCode)
            .totalAmount(totalAmount)
            .build();
    }

    @Transactional
    public OrderDto checkout(UUID userId, CheckoutRequest request) {
        String idempotencyKey = request.getIdempotencyKey();

        // Check for duplicate request using idempotency key
        UUID existingOrderId = idempotencyService.getExistingOrderId(idempotencyKey, userId);
        if (existingOrderId != null) {
            log.info("Returning existing order {} for idempotency key {}", existingOrderId, idempotencyKey);
            return orderRepository.findByIdWithDetails(existingOrderId)
                .map(OrderDto::from)
                .orElseThrow(() -> new RuntimeException("Existing order not found"));
        }

        // Try to acquire idempotency lock
        if (!idempotencyService.tryAcquire(idempotencyKey, userId)) {
            throw new RuntimeException("Duplicate checkout request. Please wait for the previous request to complete.");
        }

        try {
            Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

            if (cart.getItems().isEmpty()) {
                throw new EmptyCartException();
            }

            // Calculate totals
            OrderTotalsDto totals = calculateTotals(userId, request.getShippingAddressId(), request.getCouponCode());

            // Reserve inventory before creating order
            Map<String, Integer> productQuantities = cart.getItems().stream()
                .collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

            // Generate order ID early for inventory reservation tracking
            UUID orderId = UUID.randomUUID();

            boolean inventoryReserved = inventoryService.reserveInventory(orderId, productQuantities);
            if (!inventoryReserved) {
                throw new RuntimeException("Failed to reserve inventory. Please try again.");
            }

            // Build address from inline data if provided, otherwise use mock
            Address shippingAddress;
            if (request.getShippingAddress() != null) {
                CheckoutRequest.ShippingAddress sa = request.getShippingAddress();
                shippingAddress = Address.builder()
                    .id(UUID.randomUUID())
                    .fullName((sa.getFirstName() != null ? sa.getFirstName() : "") + " " + (sa.getLastName() != null ? sa.getLastName() : ""))
                    .addressLine1(sa.getAddress())
                    .city(sa.getCity())
                    .state(sa.getState())
                    .postalCode(sa.getPostalCode())
                    .country(sa.getCountry())
                    .phone(sa.getPhone())
                    .build();
            } else {
                shippingAddress = createMockAddress(request.getShippingAddressId());
            }
            Address billingAddress = request.getBillingAddressId() != null
                ? createMockAddress(request.getBillingAddressId())
                : shippingAddress;

            // Create order
            Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .subtotal(totals.getSubtotal())
                .taxAmount(totals.getTaxAmount())
                .shippingAmount(totals.getShippingAmount())
                .discountAmount(totals.getDiscountAmount())
                .totalAmount(totals.getTotalAmount())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .notes(request.getNotes())
                .couponCode(request.getCouponCode())
                .build();

            // Add order items from cart
            for (CartItem cartItem : cart.getItems()) {
                OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .productImage(cartItem.getProductImage())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
                order.addItem(orderItem);
            }

            // Add initial status history
            OrderStatusHistory history = OrderStatusHistory.builder()
                .status(OrderStatus.PENDING)
                .note("Order created")
                .updatedBy("SYSTEM")
                .build();
            order.addStatusHistory(history);

            order = orderRepository.save(order);

            // Clear cart
            cart.clearItems();
            cartRepository.save(cart);

            // Publish order created event
            eventPublisher.publishOrderCreated(order);

            // Mark idempotency key as completed with order ID
            idempotencyService.markCompleted(idempotencyKey, userId, order.getId());

            log.info("Order {} created for user {}", order.getOrderNumber(), userId);
            return OrderDto.from(order);
        } catch (Exception e) {
            // Release idempotency lock on failure
            idempotencyService.release(idempotencyKey, userId);
            throw e;
        }
    }

    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (int attempt = 0; attempt < MAX_ORDER_NUMBER_RETRIES; attempt++) {
            String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String orderNumber = String.format("ORD-%s-%s", datePart, randomPart);

            if (!orderRepository.existsByOrderNumber(orderNumber)) {
                return orderNumber;
            }
            log.warn("Order number collision detected, retrying... attempt {}", attempt + 1);
        }

        // Fallback: Use full UUID to guarantee uniqueness
        String fallbackNumber = String.format("ORD-%s-%s", datePart, UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        log.info("Using fallback order number generation: {}", fallbackNumber);
        return fallbackNumber;
    }

    private Address createMockAddress(UUID addressId) {
        // In production, fetch from User Service
        return Address.builder()
            .id(addressId)
            .fullName("John Doe")
            .addressLine1("123 Main Street")
            .addressLine2("Apt 4B")
            .city("New York")
            .state("NY")
            .postalCode("10001")
            .country("USA")
            .phone("+1-555-123-4567")
            .build();
    }
}
