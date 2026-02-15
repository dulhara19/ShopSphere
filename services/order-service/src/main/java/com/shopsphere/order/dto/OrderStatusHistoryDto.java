package com.shopsphere.order.dto;

import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.model.OrderStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryDto {

    private UUID id;
    private OrderStatus status;
    private LocalDateTime timestamp;
    private String note;
    private String updatedBy;

    public static OrderStatusHistoryDto from(OrderStatusHistory history) {
        return OrderStatusHistoryDto.builder()
            .id(history.getId())
            .status(history.getStatus())
            .timestamp(history.getTimestamp())
            .note(history.getNote())
            .updatedBy(history.getUpdatedBy())
            .build();
    }
}
