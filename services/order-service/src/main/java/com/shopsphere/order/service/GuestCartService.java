package com.shopsphere.order.service;

import com.shopsphere.order.dto.CartDto;
import com.shopsphere.order.dto.request.AddToCartRequest;
import com.shopsphere.order.dto.request.UpdateCartItemRequest;
import com.shopsphere.order.exception.CartItemNotFoundException;
import com.shopsphere.order.exception.CartNotFoundException;
import com.shopsphere.order.exception.MaxCartItemsExceededException;
import com.shopsphere.order.model.Cart;
import com.shopsphere.order.model.CartItem;
import com.shopsphere.order.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestCartService {

    @Value("${order.cart.max-items:50}")
    private int maxCartItems;

    @Value("${order.cart.max-quantity-per-item:10}")
    private int maxQuantityPerItem;

    private final CartRepository cartRepository;

    @Transactional
    public CartDto getOrCreateCart(String sessionId) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseGet(() -> createGuestCart(sessionId));
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto addToCart(String sessionId, AddToCartRequest request) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseGet(() -> createGuestCart(sessionId));

        // Check max items limit
        if (cart.getItems().size() >= maxCartItems) {
            throw new MaxCartItemsExceededException(maxCartItems);
        }

        // Check if product already in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(request.getProductId()))
            .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = Math.min(item.getQuantity() + request.getQuantity(), maxQuantityPerItem);
            item.setQuantity(newQuantity);
        } else {
            // Fetch product details (mock for now)
            BigDecimal mockPrice = BigDecimal.valueOf(29.99);
            CartItem newItem = CartItem.builder()
                .productId(request.getProductId())
                .productName("Product " + request.getProductId().toString().substring(0, 8))
                .productImage("https://placeholder.com/product.jpg")
                .quantity(Math.min(request.getQuantity(), maxQuantityPerItem))
                .unitPrice(mockPrice)
                .priceAtAdd(mockPrice)
                .build();
            cart.addItem(newItem);
        }

        cart = cartRepository.save(cart);
        log.info("Added item to guest cart: sessionId={}, productId={}", sessionId, request.getProductId());
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto updateCartItem(String sessionId, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseThrow(() -> CartNotFoundException.forSession(sessionId));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new CartItemNotFoundException(itemId));

        if (request.getQuantity() <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(Math.min(request.getQuantity(), maxQuantityPerItem));
        }

        cart = cartRepository.save(cart);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto removeCartItem(String sessionId, UUID itemId) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseThrow(() -> CartNotFoundException.forSession(sessionId));

        CartItem item = cart.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new CartItemNotFoundException(itemId));

        cart.removeItem(item);
        cart = cartRepository.save(cart);
        return CartDto.from(cart);
    }

    @Transactional
    public CartDto clearCart(String sessionId) {
        Cart cart = cartRepository.findBySessionIdWithItems(sessionId)
            .orElseThrow(() -> CartNotFoundException.forSession(sessionId));

        cart.clearItems();
        cart = cartRepository.save(cart);
        return CartDto.from(cart);
    }

    private Cart createGuestCart(String sessionId) {
        Cart cart = Cart.builder()
            .sessionId(sessionId)
            .build();
        return cartRepository.save(cart);
    }
}
