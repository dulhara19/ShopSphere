package com.shopsphere.notification.service;

import com.shopsphere.notification.model.Notification;
import com.shopsphere.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;
    private final TemplateService templateService;
    private final PreferenceService preferenceService;

    /**
     * Create in-app notification (Epic 1.3.1)
     */
    public Notification createNotification(String userId, String type, String title, String message,
            Map<String, Object> data, String link) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setData(data);
        n.setLink(link);
        log.info("🔔 Notification created for user: {} | Type: {}", userId, type);
        return repository.save(n);
    }

    /** Overload for backward compatibility */
    public Notification createNotification(String userId, String type, String title, String message) {
        return createNotification(userId, type, title, message, null, null);
    }

    /**
     * Get user notifications — paginated + filtered (Epic 1.3.2)
     */
    public Page<Notification> getUserNotifications(String userId, int page, int size, Boolean read) {
        Pageable pageable = PageRequest.of(page, size);
        if (read != null) {
            return repository.findByUserIdAndReadAndDeletedFalseOrderByCreatedAtDesc(userId, read, pageable);
        }
        return repository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get unread count (Epic 1.3.5)
     */
    public long getUnreadCount(String userId) {
        return repository.countByUserIdAndReadFalseAndDeletedFalse(userId);
    }

    /**
     * Mark single notification as read (Epic 1.3.3)
     */
    public void markAsRead(String id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            repository.save(n);
        });
    }

    /**
     * Mark all as read (Epic 1.3.3)
     */
    public void markAllAsRead(String userId) {
        List<Notification> list = repository.findByUserIdAndDeletedFalse(userId);
        list.forEach(n -> {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
        });
        repository.saveAll(list);
    }

    /**
     * Soft-delete single notification (Epic 1.3.4)
     */
    public void deleteNotification(String id) {
        repository.findById(id).ifPresent(n -> {
            n.setDeleted(true);
            repository.save(n);
        });
    }

    /**
     * Soft-delete all / clear all (Epic 1.3.4)
     */
    public void clearAll(String userId) {
        List<Notification> list = repository.findByUserIdAndDeletedFalse(userId);
        list.forEach(n -> n.setDeleted(true));
        repository.saveAll(list);
    }

    /**
     * Send notification using channels (Epic 1.6.1)
     * Called by internal API and event listeners.
     */
    public void sendMultiChannel(String userId, List<String> channels, String templateId,
            String title, String message, Map<String, Object> data, String link) {
        for (String channel : channels) {
            // Check user preferences before sending (Epic 1.4.5)
            if (!preferenceService.shouldSend(userId, channel, mapTypeToPreference(title))) {
                log.info("🔕 Skipped {} for user {} — preference disabled", channel, userId);
                continue;
            }

            switch (channel.toUpperCase()) {
                case "IN_APP":
                    createNotification(userId, deriveType(templateId), title, message, data, link);
                    break;
                case "EMAIL":
                    String htmlBody;
                    String subject;
                    if (templateId != null) {
                        try {
                            htmlBody = templateService.renderTemplate(templateId, data);
                            subject = templateService.renderSubject(templateId, data);
                        } catch (Exception e) {
                            log.warn("Template '{}' not found, using plain message", templateId);
                            htmlBody = message;
                            subject = title;
                        }
                    } else {
                        htmlBody = message;
                        subject = title;
                    }
                    // Resolve user email — in real app this would call User Service
                    String email = data != null && data.containsKey("email") ? data.get("email").toString()
                            : userId + "@shopsphere.com";
                    emailService.queueEmail(email, subject, htmlBody, templateId, data);
                    break;
                default:
                    log.info("📱 Channel {} not yet implemented for Phase 1", channel);
            }
        }
    }

    private String deriveType(String templateId) {
        if (templateId == null)
            return "SYSTEM";
        if (templateId.contains("order"))
            return "ORDER";
        if (templateId.contains("payment") || templateId.contains("receipt"))
            return "PAYMENT";
        if (templateId.contains("ship") || templateId.contains("deliver"))
            return "SHIPPING";
        if (templateId.contains("promo"))
            return "PROMO";
        return "SYSTEM";
    }

    private String mapTypeToPreference(String title) {
        if (title == null)
            return "systemAlerts";
        String lower = title.toLowerCase();
        if (lower.contains("order") || lower.contains("ship") || lower.contains("deliver"))
            return "orderUpdates";
        if (lower.contains("promo") || lower.contains("sale"))
            return "promotions";
        return "systemAlerts";
    }
}