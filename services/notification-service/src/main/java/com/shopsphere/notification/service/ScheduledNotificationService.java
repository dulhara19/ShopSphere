package com.shopsphere.notification.service;

import com.shopsphere.notification.model.ScheduledNotification;
import com.shopsphere.notification.repository.ScheduledNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Processes scheduled notifications that are due (Epic 1.6.3)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final ScheduledNotificationRepository scheduledRepo;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 30000) // Check every 30 seconds
    public void processScheduledNotifications() {
        List<ScheduledNotification> due = scheduledRepo
                .findBySentFalseAndCancelledFalseAndScheduledAtBefore(LocalDateTime.now());

        for (ScheduledNotification sn : due) {
            try {
                notificationService.sendMultiChannel(
                        sn.getUserId(),
                        sn.getChannels(),
                        sn.getTemplateId(),
                        sn.getTitle(),
                        sn.getMessage(),
                        sn.getData(),
                        null);
                sn.setSent(true);
                scheduledRepo.save(sn);
                log.info("⏰ Scheduled notification {} sent to user {}", sn.getId(), sn.getUserId());
            } catch (Exception e) {
                log.error("❌ Failed to send scheduled notification: {}", sn.getId(), e);
            }
        }
    }
}
