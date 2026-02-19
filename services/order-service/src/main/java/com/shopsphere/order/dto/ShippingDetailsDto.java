package com.shopsphere.order.dto;

import com.shopsphere.order.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDetailsDto {

    private UUID orderId;
    private String orderNumber;
    private List<OrderItemDto> items;
    private AddressDto shippingAddress;
    private BigDecimal totalWeight;

    public static ShippingDetailsDto from(Order order) {
        return ShippingDetailsDto.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .items(order.getItems().stream()
                .map(OrderItemDto::from)
                .collect(Collectors.toList()))
            .shippingAddress(AddressDto.from(order.getShippingAddress()))
            .totalWeight(null) // Would be calculated from product weights
            .build();
    }
}
