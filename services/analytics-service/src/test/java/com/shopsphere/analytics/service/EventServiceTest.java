package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.EventDTO;
import com.shopsphere.analytics.model.Event;
import com.shopsphere.analytics.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTrackEvent() {
        // Arrange
        EventDTO eventDTO = EventDTO.builder()
            .eventType("PAGE_VIEW")
            .timestamp(LocalDateTime.now())
            .userId("user123")
            .productId("prod456")
            .build();

        Event event = Event.builder()
            .id(1L)
            .eventType("PAGE_VIEW")
            .eventTimestamp(LocalDateTime.now())
            .userId("user123")
            .productId("prod456")
            .build();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        Event result = eventService.trackEvent(eventDTO);

        // Assert
        assertNotNull(result);
        assertEquals("PAGE_VIEW", result.getEventType());
        assertEquals("user123", result.getUserId());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testGetEventsByUser() {
        // Arrange
        String userId = "user123";
        Event event1 = Event.builder()
            .id(1L)
            .eventType("PAGE_VIEW")
            .userId(userId)
            .build();

        Event event2 = Event.builder()
            .id(2L)
            .eventType("PURCHASE")
            .userId(userId)
            .build();

        List<Event> events = Arrays.asList(event1, event2);
        when(eventRepository.findByUserId(userId)).thenReturn(events);

        // Act
        List<Event> result = eventService.getEventsByUser(userId);

        // Assert
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findByUserId(userId);
    }
}
