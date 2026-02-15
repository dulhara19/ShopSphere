package com.shopsphere.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddInternalNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note cannot exceed 2000 characters")
    private String note;
}
