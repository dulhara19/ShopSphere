package com.shopsphere.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchEventDTO {

    @NotEmpty(message = "Events list cannot be empty")
    @Valid
    private List<EventDTO> events;

    private String source;
}
