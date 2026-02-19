package com.shopsphere.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationDto {

    private boolean valid;
    @Builder.Default
    private List<ValidationIssueDto> issues = new ArrayList<>();

    public static CartValidationDto valid() {
        return CartValidationDto.builder()
            .valid(true)
            .issues(new ArrayList<>())
            .build();
    }

    public static CartValidationDto invalid(List<ValidationIssueDto> issues) {
        return CartValidationDto.builder()
            .valid(false)
            .issues(issues)
            .build();
    }
}
