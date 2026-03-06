package com.shopsphere.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private Boolean success;

    private String message;

    private T data;

    private String error;

    private LocalDateTime timestamp;

    private String path;

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return ApiResponseDTO.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponseDTO<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponseDTO<T> error(String error, String message) {
        return ApiResponseDTO.<T>builder()
            .success(false)
            .error(error)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
