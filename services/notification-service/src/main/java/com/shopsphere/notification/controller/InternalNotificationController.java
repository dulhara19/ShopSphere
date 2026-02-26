package com.shopsphere.notification.controller;

import com.shopsphere.notification.dto.BulkNotificationRequest;
import com.shopsphere.notification.dto.InternalNotificationRequest;
import com.shopsphere.notification.dto.ScheduleNotificationRequest;
import com.shopsphere.notification.model.ScheduledNotification;
import com.shopsphere.notification.repository.ScheduledNotificationRepository;
import com.shopsphere.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
@Slf4j
public class InternalNotificationController {

    private final NotificationService notificationService;
    private final ScheduledNotificationRepository scheduledRepo;

    // ── Epic 1.6.1 — Send notification (internal, multi-channel) ──
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> sendInternal(@RequestBody InternalNotificationRequest request) {
        notificationService.sendMultiChannel(
                request.getUserId(),
                request.getChannels(),
                request.getTemplateId(),
                request.getTitle(),
                request.getMessage(),
                request.getData(),
                request.getLink());
        return Map.of("status", "sent");
    }

    // ── Epic 1.6.2 — Bulk notifications ──
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> sendBulk(@RequestBody BulkNotificationRequest request) {
        for (String userId : request.getUserIds()) {
            notificationService.sendMultiChannel(
                    userId,
                    request.getChannels(),
                    request.getTemplateId(),
                    request.getTitle(),
                    request.getMessage(),
                    request.getData(),
                    null);
        }
        log.info("📢 Bulk notification sent to {} users", request.getUserIds().size());
        return Map.of("status", "accepted", "recipientCount", String.valueOf(request.getUserIds().size()));
    }

    // ── Epic 1.6.3 — Schedule notification ──
    @PostMapping("/schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduledNotification schedule(@RequestBody ScheduleNotificationRequest request) {
        ScheduledNotification sn = new ScheduledNotification();
        sn.setUserId(request.getUserId());
        sn.setChannels(request.getChannels());
        sn.setTemplateId(request.getTemplateId());
        sn.setTitle(request.getTitle());
        sn.setMessage(request.getMessage());
        sn.setData(request.getData());
        sn.setScheduledAt(request.getScheduledAt());
        log.info("⏰ Notification scheduled for user: {} at {}", request.getUserId(), request.getScheduledAt());
        return scheduledRepo.save(sn);
    }

    // ── Epic 1.6.3 — Cancel scheduled notification ──
    @DeleteMapping("/scheduled/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelScheduled(@PathVariable String id) {
        scheduledRepo.findById(id).ifPresent(sn -> {
            sn.setCancelled(true);
            scheduledRepo.save(sn);
            log.info("❌ Scheduled notification {} cancelled", id);
        });
    }
}
