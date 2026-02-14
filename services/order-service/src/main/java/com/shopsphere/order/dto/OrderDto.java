package com.shopsphere.order.dto;

import com.shopsphere.order.model.Order;
import com.shopsphere.order.model.OrderStatus;
import com.shopsphere.order.model.PaymentMethod;
import com.shopsphere.order.model.PaymentStatus;
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
public class OrderDto {

    private UUID id;
    private String orderNumber;
    private UUID userId;
    private OrderStatus status;
    private List<OrderItemDto> items;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private UUID paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String trackingNumber;
    private String notes;
    private String internalNotes;
    private List<StatusHistoryItemDto> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderDto from(Order order) {
        return OrderDto.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .userId(order.getUserId())
            .status(order.getStatus())
            .items(order.getItems().stream()
                .map(OrderItemDto::from)
                .collect(Collectors.toList()))
            .shippingAddress(AddressDto.from(order.getShippingAddress()))
            .billingAddress(AddressDto.from(order.getBillingAddress()))
            .subtotal(order.getSubtotal())
            .taxAmount(order.getTaxAmount())
            .shippingAmount(order.getShippingAmount())
            .discountAmount(order.getDiscountAmount())
            .totalAmount(order.getTotalAmount())
            .paymentId(order.getPaymentId())
            .paymentStatus(order.getPaymentStatus())
            .paymentMethod(order.getPaymentMethod())
            .trackingNumber(order.getTrackingNumber())
            .notes(order.getNotes())
            .internalNotes(order.getInternalNotes())
            .statusHistory(order.getStatusHistory().stream()
                .map(StatusHistoryItemDto::from)
                .collect(Collectors.toList()))
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }
}
