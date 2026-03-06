package com.shopsphere.notification.repository;

import com.shopsphere.notification.model.NotificationPreference;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface NotificationPreferenceRepository extends MongoRepository<NotificationPreference, String> {
    Optional<NotificationPreference> findByUserId(String userId);

    Optional<NotificationPreference> findByUnsubscribeToken(String token);
}
