package com.shopsphere.notification.service;

import com.shopsphere.notification.model.Notification;
import com.shopsphere.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    private final EmailService emailService;

    public Notification createNotification(String userId, String type, String title, String message) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);

        if ("ORDER".equals(type)) {
            emailService.sendEmail(userId + "@test.com", title, message);
        }
        return repository.save(n);
    }

    public List<Notification> getUserNotifications(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(String userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    public void markAsRead(String id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

    public void markAllAsRead(String userId) {
        List<Notification> list = repository.findByUserIdOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        repository.saveAll(list);
    }

    public void clearAll(String userId) {
        List<Notification> list = repository.findByUserIdOrderByCreatedAtDesc(userId);
        repository.deleteAll(list);
    }
}