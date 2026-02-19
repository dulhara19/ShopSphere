package com.shopsphere.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopsphere.inventory.model.StockReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    @JsonProperty("reservation_id")
    private UUID reservationId;

    @JsonProperty("product_id")
    private UUID productId;

    private Long quantity;

    private String status;

    @JsonProperty("order_id")
    private UUID orderId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("expires_at")
    private LocalDateTime expiresAt;

    @JsonProperty("confirmed_at")
    private LocalDateTime confirmedAt;

    @JsonProperty("released_at")
    private LocalDateTime releasedAt;

    public static ReservationDTO fromEntity(StockReservation reservation) {
        return ReservationDTO.builder()
                .reservationId(reservation.getId())
                .productId(reservation.getProductId())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus().toString())
                .orderId(reservation.getOrderId())
                .createdAt(reservation.getCreatedAt())
                .expiresAt(reservation.getExpiresAt())
                .confirmedAt(reservation.getConfirmedAt())
                .releasedAt(reservation.getReleasedAt())
                .build();
    }
}
