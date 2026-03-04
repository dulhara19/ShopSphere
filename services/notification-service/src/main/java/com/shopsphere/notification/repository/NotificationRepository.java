package com.shopsphere.notification.repository;

import com.shopsphere.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    // Pagination + soft-delete aware
    Page<Notification> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Filter by read status + pagination
    Page<Notification> findByUserIdAndReadAndDeletedFalseOrderByCreatedAtDesc(String userId, boolean read,
            Pageable pageable);

    long countByUserIdAndReadFalseAndDeletedFalse(String userId);

    List<Notification> findByUserIdAndDeletedFalse(String userId);
}