package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.RecommendationEvent;
import com.shopsphere.recommendation.repository.EventTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventTrackingService {

    private final EventTrackingRepository repository;

    // Save the event to MongoDB and return the saved event
    public RecommendationEvent trackEvent(RecommendationEvent event) {
        // Save the event to MongoDB
        return repository.save(event);
    }
}
