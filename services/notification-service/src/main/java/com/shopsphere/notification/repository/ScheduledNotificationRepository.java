package com.shopsphere.notification.repository;

import com.shopsphere.notification.model.ScheduledNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledNotificationRepository extends MongoRepository<ScheduledNotification, String> {
    List<ScheduledNotification> findBySentFalseAndCancelledFalseAndScheduledAtBefore(LocalDateTime dateTime);
}
