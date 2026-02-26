package com.shopsphere.notification.controller;

import com.shopsphere.notification.model.Notification;
import com.shopsphere.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService service;

    // ── Epic 1.3.2 — Paginated + filtered inbox ──
    @GetMapping("/api/notifications")
    public Page<Notification> getInbox(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean read) {
        return service.getUserNotifications(userId, page, size, read);
    }

    // ── Epic 1.3.5 — Unread count ──
    @GetMapping("/api/notifications/unread-count")
    public Map<String, Long> getCount(@RequestParam String userId) {
        return Collections.singletonMap("count", service.getUnreadCount(userId));
    }

    // ── Epic 1.3.3 — Mark single as read ──
    @PutMapping("/api/notifications/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRead(@PathVariable String id) {
        service.markAsRead(id);
    }

    // ── Epic 1.3.3 — Mark all as read ──
    @PutMapping("/api/notifications/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@RequestParam String userId) {
        service.markAllAsRead(userId);
    }

    // ── Epic 1.3.4 — Delete single notification (soft delete) ──
    @DeleteMapping("/api/notifications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable String id) {
        service.deleteNotification(id);
    }

    // ── Epic 1.3.4 — Clear all notifications (soft delete) ──
    @DeleteMapping("/api/notifications/clear-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll(@RequestParam String userId) {
        service.clearAll(userId);
    }
}