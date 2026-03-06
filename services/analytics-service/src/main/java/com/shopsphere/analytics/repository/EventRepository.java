package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventTypeAndEventTimestampBetween(String eventType, LocalDateTime from, LocalDateTime to);

    List<Event> findByUserId(String userId);

    List<Event> findByUserIdAndEventTimestampBetween(String userId, LocalDateTime from, LocalDateTime to);

    List<Event> findByProductId(String productId);

    List<Event> findByOrderId(String orderId);

    @Query("SELECT e FROM Event e WHERE e.eventType = :eventType ORDER BY e.eventTimestamp DESC LIMIT :limit")
    List<Event> findRecentEventsByType(@Param("eventType") String eventType, @Param("limit") int limit);

    List<Event> findByEventTimestampBetweenOrderByEventTimestampDesc(LocalDateTime from, LocalDateTime to);

    long countByEventTypeAndEventTimestampBetween(String eventType, LocalDateTime from, LocalDateTime to);
}
