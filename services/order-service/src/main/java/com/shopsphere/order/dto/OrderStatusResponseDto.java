package com.shopsphere.order.dto;

import com.shopsphere.order.model.Order;
import com.shopsphere.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponseDto {

    private OrderStatus currentStatus;
    private List<StatusHistoryItemDto> history;

    public static OrderStatusResponseDto from(Order order) {
        return OrderStatusResponseDto.builder()
            .currentStatus(order.getStatus())
            .history(order.getStatusHistory().stream()
                .map(StatusHistoryItemDto::from)
                .collect(Collectors.toList()))
            .build();
    }
}
