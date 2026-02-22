package com.shopsphere.analytics.exception;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: ", ex);
        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
            .success(false)
            .error("INTERNAL_ERROR")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
            .success(false)
            .error("VALIDATION_ERROR")
            .message(message.toString())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleException(Exception ex) {
        log.error("Unexpected exception: ", ex);
        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
            .success(false)
            .error("SERVER_ERROR")
            .message("An unexpected error occurred")
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
