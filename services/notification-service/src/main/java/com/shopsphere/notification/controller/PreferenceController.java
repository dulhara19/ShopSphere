package com.shopsphere.notification.controller;

import com.shopsphere.notification.dto.PreferenceUpdateRequest;
import com.shopsphere.notification.model.NotificationPreference;
import com.shopsphere.notification.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PreferenceController {

    private final PreferenceService preferenceService;

    // ── Epic 1.4.1 — Get user preferences ──
    @GetMapping("/api/notifications/preferences")
    public NotificationPreference getPreferences(@RequestParam String userId) {
        return preferenceService.getPreferences(userId);
    }

    // ── Epic 1.4.2 — Update user preferences ──
    @PutMapping("/api/notifications/preferences")
    public NotificationPreference updatePreferences(@RequestParam String userId,
            @RequestBody PreferenceUpdateRequest request) {
        return preferenceService.updatePreferences(userId, request);
    }

    // ── Epic 1.4.4 — One-click unsubscribe from email ──
    @GetMapping("/api/notifications/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestParam String token) {
        boolean success = preferenceService.unsubscribeByToken(token);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Successfully unsubscribed from email notifications."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired unsubscribe token."));
    }
}
