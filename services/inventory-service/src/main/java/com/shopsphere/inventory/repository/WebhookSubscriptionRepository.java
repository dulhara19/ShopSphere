package com.shopsphere.inventory.repository;

import com.shopsphere.inventory.model.WebhookSubscription;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
    List<WebhookSubscription> findByActiveTrueAndEventType(String eventType);
}
