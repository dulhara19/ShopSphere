package com.shopsphere.notification.controller;

import com.shopsphere.notification.dto.NotificationRequest;
import com.shopsphere.notification.model.Notification;
import com.shopsphere.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;

    // Internal API (Epic 1.6)
    @PostMapping("/internal/notifications/send")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendInternal(@RequestBody NotificationRequest request) {
        service.createNotification(request.getUserId(), request.getType(), request.getTitle(), request.getMessage());
    }

    // Public API (Epic 1.3)
    @GetMapping("/api/notifications")
    public List<Notification> getInbox(@RequestParam String userId) {
        return service.getUserNotifications(userId);
    }

    @GetMapping("/api/notifications/unread-count")
    public Map<String, Long> getCount(@RequestParam String userId) {
        return Collections.singletonMap("count", service.getUnreadCount(userId));
    }

    @PutMapping("/api/notifications/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable String id) {
        service.markAsRead(id);
    }

    @PutMapping("/api/notifications/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@RequestParam String userId) {
        service.markAllAsRead(userId);
    }

    @DeleteMapping("/api/notifications/clear-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll(@RequestParam String userId) {
        service.clearAll(userId);
    }
}