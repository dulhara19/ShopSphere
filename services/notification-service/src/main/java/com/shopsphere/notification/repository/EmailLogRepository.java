package com.shopsphere.notification.repository;

import com.shopsphere.notification.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
    List<EmailLog> findByStatusAndAttemptsLessThan(String status, int maxAttempts);

    List<EmailLog> findByTo(String to);
}
