package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.BatchEventDTO;
import com.shopsphere.analytics.dto.EventDTO;
import com.shopsphere.analytics.model.Event;
import com.shopsphere.analytics.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final RealTimeAnalyticsService realTimeAnalyticsService;

    @Transactional
    public Event trackEvent(EventDTO eventDTO) {
        Event event = Event.builder()
                .eventType(eventDTO.getEventType())
                .eventTimestamp(eventDTO.getTimestamp() != null ? eventDTO.getTimestamp() : LocalDateTime.now())
                .userId(eventDTO.getUserId())
                .productId(eventDTO.getProductId())
                .orderId(eventDTO.getOrderId())
                .categoryId(eventDTO.getCategoryId())
                .sessionId(eventDTO.getSessionId())
                .properties(eventDTO.getProperties() != null ? eventDTO.getProperties().toString() : null)
                .source(eventDTO.getSource())
                .version(eventDTO.getVersion())
                .build();

        Event savedEvent = eventRepository.save(event);
        log.info("Event tracked: {} for user: {}", eventDTO.getEventType(), eventDTO.getUserId());

        // Broadcast the event to real-time clients
        realTimeAnalyticsService.broadcastEvent(savedEvent);

        return savedEvent;
    }

    @Transactional
    public List<Event> trackBatchEvents(BatchEventDTO batchEventDTO) {
        List<Event> events = batchEventDTO.getEvents().stream()
                .map(eventDTO -> {
                    eventDTO.setSource(batchEventDTO.getSource());
                    return EventDTO.builder()
                            .eventType(eventDTO.getEventType())
                            .timestamp(eventDTO.getTimestamp())
                            .userId(eventDTO.getUserId())
                            .productId(eventDTO.getProductId())
                            .orderId(eventDTO.getOrderId())
                            .categoryId(eventDTO.getCategoryId())
                            .sessionId(eventDTO.getSessionId())
                            .properties(eventDTO.getProperties())
                            .source(batchEventDTO.getSource())
                            .version(eventDTO.getVersion())
                            .build();
                })
                .map(this::convertToEntity)
                .toList();

        List<Event> savedEvents = eventRepository.saveAll(events);
        log.info("Batch events tracked: {} events", savedEvents.size());
        return savedEvents;
    }

    public List<Event> getEventsByType(String eventType, LocalDateTime from, LocalDateTime to) {
        return eventRepository.findByEventTypeAndEventTimestampBetween(eventType, from, to);
    }

    public List<Event> getEventsByUser(String userId) {
        return eventRepository.findByUserId(userId);
    }

    public List<Event> getEventsByUserAndDateRange(String userId, LocalDateTime from, LocalDateTime to) {
        return eventRepository.findByUserIdAndEventTimestampBetween(userId, from, to);
    }

    public List<Event> getEventsByProduct(String productId) {
        return eventRepository.findByProductId(productId);
    }

    public List<Event> getEventsByOrder(String orderId) {
        return eventRepository.findByOrderId(orderId);
    }

    public long getEventCount(String eventType, LocalDateTime from, LocalDateTime to) {
        return eventRepository.countByEventTypeAndEventTimestampBetween(eventType, from, to);
    }

    @RabbitListener(queues = "analytics.events")
    public void consumeEvent(EventDTO eventDTO) {
        try {
            trackEvent(eventDTO);
        } catch (Exception e) {
            log.error("Error consuming event from RabbitMQ", e);
        }
    }

    @RabbitListener(queues = "analytics.batch-events")
    public void consumeBatchEvents(BatchEventDTO batchEventDTO) {
        try {
            trackBatchEvents(batchEventDTO);
        } catch (Exception e) {
            log.error("Error consuming batch events from RabbitMQ", e);
        }
    }

    private Event convertToEntity(EventDTO eventDTO) {
        return Event.builder()
                .eventType(eventDTO.getEventType())
                .eventTimestamp(eventDTO.getTimestamp() != null ? eventDTO.getTimestamp() : LocalDateTime.now())
                .userId(eventDTO.getUserId())
                .productId(eventDTO.getProductId())
                .orderId(eventDTO.getOrderId())
                .categoryId(eventDTO.getCategoryId())
                .sessionId(eventDTO.getSessionId())
                .properties(eventDTO.getProperties() != null ? eventDTO.getProperties().toString() : null)
                .source(eventDTO.getSource())
                .version(eventDTO.getVersion())
                .build();
    }
}
