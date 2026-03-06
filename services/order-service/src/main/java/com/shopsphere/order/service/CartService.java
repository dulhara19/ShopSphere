package com.shopsphere.order.service;

import com.shopsphere.order.dto.CartDto;
import com.shopsphere.order.dto.CartValidationDto;
import com.shopsphere.order.dto.ValidationIssueDto;
import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.MergeCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.exception.CartItemNotFoundException;
import com.shopsphere.order.exception.CartNotFoundException;
import com.shopsphere.order.exception.CartValidationException;
import com.shopsphere.order.model.Cart;
import com.shopsphere.order.model.CartItem;
import com.shopsphere.order.repository.CartItemRepository;
import com.shopsphere.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private static final int MAX_CART_ITEMS = 50;
    private static final int MAX_QUANTITY_PER_ITEM = 99;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional(readOnly = true)
    public CartDto getCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> createEmptyCart(userId, null));
        return CartDto.from(cart);
    }

    @Transactional(readOnly = true)
    public CartDto getGuestCart(String sessionId) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseGet(() -> createEmptyCart(null, sessionId));
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto addToCart(UUID userId, AddToCartRequest request) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .userId(userId)
                    .build();
                return cartRepository.save(newCart);
            });

        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                throw new CartValidationException("Maximum quantity per item is " + MAX_QUANTITY_PER_ITEM);
            }
            item.setQuantity(newQuantity);
        } else {
            // Check max items limit
            if (cart.getItems().size() >= MAX_CART_ITEMS) {
                throw new CartValidationException("Cart cannot have more than " + MAX_CART_ITEMS + " different items");
            }

            if (request.getQuantity() > MAX_QUANTITY_PER_ITEM) {
                throw new CartValidationException("Maximum quantity per item is " + MAX_QUANTITY_PER_ITEM);
            }

            // In production, fetch product details from Product Service
            CartItem newItem = CartItem.builder()
                .productId(request.getProductId())
                .productName("Product " + request.getProductId()) // Mock
                .productImage(null)
                .quantity(request.getQuantity())
                .unitPrice(BigDecimal.valueOf(99.99)) // Mock price
                .priceAtAdd(BigDecimal.valueOf(99.99)) // Mock price
                .build();
            cart.addItem(newItem);
        }

        cart = cartRepository.save(cart);
        log.info("Added product {} to cart for user {}", request.getProductId(), userId);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto updateCartItem(UUID userId, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> CartItemNotFoundException.forId(itemId));

        if (request.getQuantity() == 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(request.getQuantity());
        }

        cart = cartRepository.save(cart);
        log.info("Updated cart item {} for user {}", itemId, userId);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto removeCartItem(UUID userId, UUID itemId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> CartItemNotFoundException.forId(itemId));

        cart.removeItem(item);
        cartItemRepository.delete(item);
        cart = cartRepository.save(cart);

        log.info("Removed cart item {} for user {}", itemId, userId);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto clearCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        cart.clearItems();
        cart = cartRepository.save(cart);

        log.info("Cleared cart for user {}", userId);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto mergeCart(UUID userId, MergeCartRequest request) {
        Cart userCart = cartRepository.findByUserIdWithItems(userId)
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .userId(userId)
                    .build();
                return cartRepository.save(newCart);
            });

        Optional<Cart> guestCart = cartRepository.findBySessionIdWithItems(request.getSessionId());

        if (guestCart.isPresent()) {
            Cart guest = guestCart.get();

            // Calculate how many new unique items would be added
            long newUniqueItems = guest.getItems().stream()
                .filter(guestItem -> userCart.getItems().stream()
                    .noneMatch(userItem -> userItem.getProductId().equals(guestItem.getProductId())))
                .count();

            // Check if merged cart would exceed max items limit
            if (userCart.getItems().size() + newUniqueItems > MAX_CART_ITEMS) {
                throw new CartValidationException(String.format(
                    "Cannot merge carts: combined cart would have %d items, maximum allowed is %d",
                    userCart.getItems().size() + newUniqueItems, MAX_CART_ITEMS));
            }

            for (CartItem guestItem : guest.getItems()) {
                Optional<CartItem> existingItem = userCart.getItems().stream()
                    .filter(item -> item.getProductId().equals(guestItem.getProductId()))
                    .findFirst();

                if (existingItem.isPresent()) {
                    int combinedQuantity = existingItem.get().getQuantity() + guestItem.getQuantity();
                    // Cap quantity at max allowed
                    existingItem.get().setQuantity(Math.min(combinedQuantity, MAX_QUANTITY_PER_ITEM));
                } else {
                    CartItem newItem = CartItem.builder()
                        .productId(guestItem.getProductId())
                        .productName(guestItem.getProductName())
                        .productImage(guestItem.getProductImage())
                        .quantity(Math.min(guestItem.getQuantity(), MAX_QUANTITY_PER_ITEM))
                        .unitPrice(guestItem.getUnitPrice())
                        .priceAtAdd(guestItem.getPriceAtAdd())
                        .build();
                    userCart.addItem(newItem);
                }
            }
            cartRepository.delete(guest);
            log.info("Merged guest cart {} into user cart {}", request.getSessionId(), userId);
        }

        userCart = cartRepository.save(userCart);
        return CartDto.from(userCart);
    }

    @Transactional(readOnly = true)
    public CartValidationDto validateCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        if (cart.getItems().isEmpty()) {
            return CartValidationDto.invalid(List.of(
                ValidationIssueDto.builder()
                    .issue(ValidationIssueDto.IssueType.PRODUCT_UNAVAILABLE)
                    .productName("Cart is empty")
                    .build()
            ));
        }

        List<ValidationIssueDto> issues = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            // Validate quantity limits
            if (item.getQuantity() > MAX_QUANTITY_PER_ITEM) {
                issues.add(ValidationIssueDto.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .issue(ValidationIssueDto.IssueType.INSUFFICIENT_STOCK)
                    .requestedQuantity(item.getQuantity())
                    .currentStock(MAX_QUANTITY_PER_ITEM)
                    .build());
            }

            // In production: Call Inventory Service to check stock
            // For MVP, mock inventory check - assume items with quantity > 10 are low stock
            int mockAvailableStock = 100; // Mock: assume 100 in stock
            if (item.getQuantity() > mockAvailableStock) {
                issues.add(ValidationIssueDto.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .issue(ValidationIssueDto.IssueType.INSUFFICIENT_STOCK)
                    .requestedQuantity(item.getQuantity())
                    .currentStock(mockAvailableStock)
                    .build());
            }

            // In production: Call Product Service to check if price changed
            // For MVP, compare priceAtAdd with current unitPrice
            if (item.getPriceAtAdd() != null &&
                item.getUnitPrice().compareTo(item.getPriceAtAdd()) != 0) {
                issues.add(ValidationIssueDto.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .issue(ValidationIssueDto.IssueType.PRICE_CHANGED)
                    .oldPrice(item.getPriceAtAdd())
                    .newPrice(item.getUnitPrice())
                    .build());
            }
        }

        if (issues.isEmpty()) {
            log.debug("Cart validation passed for user {}", userId);
            return CartValidationDto.valid();
        }

        log.warn("Cart validation found {} issues for user {}", issues.size(), userId);
        return CartValidationDto.invalid(issues);
    }

    @Transactional(readOnly = true)
    public Cart getCartEntity(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));
    }

    @Transactional
    public CartDto applyCoupon(UUID userId, String couponCode) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        // In production: Validate coupon with Coupon Service
        // For MVP: Basic validation - accept all codes that start with uppercase letter
        if (couponCode == null || couponCode.isBlank()) {
            throw new CartValidationException("Coupon code cannot be empty");
        }

        if (!Character.isUpperCase(couponCode.charAt(0))) {
            throw new CartValidationException("Invalid coupon code");
        }

        cart.setCouponCode(couponCode.toUpperCase());
        cart = cartRepository.save(cart);

        log.info("Applied coupon {} to cart for user {}", couponCode, userId);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto removeCoupon(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
            .orElseThrow(() -> CartNotFoundException.forUser(userId.toString()));

        String previousCoupon = cart.getCouponCode();
        cart.setCouponCode(null);
        cart = cartRepository.save(cart);

        log.info("Removed coupon {} from cart for user {}", previousCoupon, userId);
        return CartDto.from(cart);
    }

    private Cart createEmptyCart(UUID userId, String sessionId) {
        Cart cart = Cart.builder()
            .userId(userId)
            .sessionId(sessionId)
            .build();
        return cart;
    }
}
