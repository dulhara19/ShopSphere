package com.shopsphere.notification.service;

import com.shopsphere.notification.dto.PreferenceUpdateRequest;
import com.shopsphere.notification.model.NotificationPreference;
import com.shopsphere.notification.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    /**
     * Get user preferences, create defaults if not found (Epic 1.4.1 + 1.4.3)
     */
    public NotificationPreference getPreferences(String userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.createDefault(userId);
                    return preferenceRepository.save(defaultPref);
                });
    }

    /**
     * Update user preferences (Epic 1.4.2)
     */
    public NotificationPreference updatePreferences(String userId, PreferenceUpdateRequest request) {
        NotificationPreference pref = getPreferences(userId);

        if (request.getEmail() != null) {
            Map<String, Boolean> merged = new HashMap<>(pref.getEmail());
            merged.putAll(request.getEmail());
            pref.setEmail(merged);
        }
        if (request.getInApp() != null) {
            Map<String, Boolean> merged = new HashMap<>(pref.getInApp());
            merged.putAll(request.getInApp());
            pref.setInApp(merged);
        }
        if (request.getSms() != null) {
            Map<String, Boolean> merged = new HashMap<>(pref.getSms());
            merged.putAll(request.getSms());
            pref.setSms(merged);
        }
        if (request.getPush() != null) {
            Map<String, Boolean> merged = new HashMap<>(pref.getPush());
            merged.putAll(request.getPush());
            pref.setPush(merged);
        }

        pref.setUpdatedAt(LocalDateTime.now());
        return preferenceRepository.save(pref);
    }

    /**
     * Unsubscribe via token (Epic 1.4.4)
     */
    public boolean unsubscribeByToken(String token) {
        return preferenceRepository.findByUnsubscribeToken(token).map(pref -> {
            // Disable all email notifications
            Map<String, Boolean> emailPrefs = new HashMap<>(pref.getEmail());
            emailPrefs.replaceAll((k, v) -> false);
            pref.setEmail(emailPrefs);
            pref.setUpdatedAt(LocalDateTime.now());
            preferenceRepository.save(pref);
            log.info("🔕 User {} unsubscribed via token", pref.getUserId());
            return true;
        }).orElse(false);
    }

    /**
     * Check if a user wants a specific notification type on a channel (Epic 1.4.5)
     */
    public boolean shouldSend(String userId, String channel, String notificationType) {
        NotificationPreference pref = getPreferences(userId);
        Map<String, Boolean> channelPrefs;

        switch (channel.toUpperCase()) {
            case "EMAIL":
                channelPrefs = pref.getEmail();
                break;
            case "IN_APP":
                channelPrefs = pref.getInApp();
                break;
            case "SMS":
                channelPrefs = pref.getSms();
                break;
            case "PUSH":
                channelPrefs = pref.getPush();
                break;
            default:
                return true;
        }

        if (channelPrefs == null)
            return true;
        return channelPrefs.getOrDefault(notificationType, true);
    }
}
