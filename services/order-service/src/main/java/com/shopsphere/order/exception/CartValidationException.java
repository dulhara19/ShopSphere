package com.shopsphere.order.exception;

import com.shopsphere.order.dto.ValidationIssueDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CartValidationException extends RuntimeException {

    private final List<ValidationIssueDto> issues;

    public CartValidationException(List<ValidationIssueDto> issues) {
        super("Cart validation failed");
        this.issues = issues;
    }

    public CartValidationException(String message) {
        super(message);
        this.issues = new ArrayList<>();
    }
}
