package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.BatchEventDTO;
import com.shopsphere.analytics.dto.EventDTO;
import com.shopsphere.analytics.model.Event;
import com.shopsphere.analytics.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/analytics/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<Event>> trackEvent(@Valid @RequestBody EventDTO eventDTO) {
        Event event = eventService.trackEvent(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseDTO.success(event, "Event tracked successfully"));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponseDTO<List<Event>>> trackBatchEvents(@Valid @RequestBody BatchEventDTO batchEventDTO) {
        List<Event> events = eventService.trackBatchEvents(batchEventDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseDTO.success(events, "Batch events tracked successfully"));
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<ApiResponseDTO<Long>> getEventCount(
        @PathVariable String eventType,
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        // Parse dates and get count
        long count = eventService.getEventCount(
            eventType,
            java.time.LocalDateTime.parse(from),
            java.time.LocalDateTime.parse(to)
        );
        
        return ResponseEntity.ok(ApiResponseDTO.success(count, "Event count retrieved"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<Event>>> getUserEvents(@PathVariable String userId) {
        List<Event> events = eventService.getEventsByUser(userId);
        return ResponseEntity.ok(ApiResponseDTO.success(events, "User events retrieved"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponseDTO<List<Event>>> getProductEvents(@PathVariable String productId) {
        List<Event> events = eventService.getEventsByProduct(productId);
        return ResponseEntity.ok(ApiResponseDTO.success(events, "Product events retrieved"));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponseDTO<List<Event>>> getOrderEvents(@PathVariable String orderId) {
        List<Event> events = eventService.getEventsByOrder(orderId);
        return ResponseEntity.ok(ApiResponseDTO.success(events, "Order events retrieved"));
    }
}
