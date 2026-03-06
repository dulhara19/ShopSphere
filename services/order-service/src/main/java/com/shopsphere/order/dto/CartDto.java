package com.shopsphere.order.dto;

import com.shopsphere.order.model.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private UUID id;
    private UUID userId;
    private String sessionId;
    private String couponCode;
    private List<CartItemDto> items;
    private int itemCount;
    private BigDecimal subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartDto from(Cart cart) {
        return CartDto.builder()
            .id(cart.getId())
            .userId(cart.getUserId())
            .sessionId(cart.getSessionId())
            .couponCode(cart.getCouponCode())
            .items(cart.getItems().stream()
                .map(CartItemDto::from)
                .collect(Collectors.toList()))
            .itemCount(cart.getItemCount())
            .subtotal(cart.getSubtotal())
            .createdAt(cart.getCreatedAt())
            .updatedAt(cart.getUpdatedAt())
            .build();
    }
}
