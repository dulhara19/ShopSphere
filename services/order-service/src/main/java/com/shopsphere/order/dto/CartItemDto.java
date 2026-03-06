package com.shopsphere.order.dto;

import com.shopsphere.order.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private UUID id;
    private UUID productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal priceAtAdd;
    private LocalDateTime addedAt;

    public static CartItemDto from(CartItem item) {
        return CartItemDto.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .productName(item.getProductName())
            .productImage(item.getProductImage())
            .quantity(item.getQuantity())
            .unitPrice(item.getUnitPrice())
            .totalPrice(item.getTotalPrice())
            .priceAtAdd(item.getPriceAtAdd())
            .addedAt(item.getAddedAt())
            .build();
    }
}
