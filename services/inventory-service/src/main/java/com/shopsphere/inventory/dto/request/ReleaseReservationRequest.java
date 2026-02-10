package com.shopsphere.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseReservationRequest {

    @NotNull(message = "Reservation ID is required")
    @JsonProperty("reservation_id")
    private UUID reservationId;
}
