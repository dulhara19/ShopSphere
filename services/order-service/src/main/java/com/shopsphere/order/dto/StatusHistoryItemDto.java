package com.shopsphere.order.dto;

import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.model.OrderStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryItemDto {

    private OrderStatus status;
    private LocalDateTime timestamp;
    private String note;
    private String updatedBy;

    public static StatusHistoryItemDto from(OrderStatusHistory history) {
        return StatusHistoryItemDto.builder()
            .status(history.getStatus())
            .timestamp(history.getTimestamp())
            .note(history.getNote())
            .updatedBy(history.getUpdatedBy())
            .build();
    }
}
