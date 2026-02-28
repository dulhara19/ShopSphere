package com.shopsphere.recommendation.service;

import com.shopsphere.recommendation.model.SearchSuggestion;
import com.shopsphere.recommendation.repository.SearchSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Epic 2.5: Search Recommendations Service
 */
@Service
@RequiredArgsConstructor
public class SearchRecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(SearchRecommendationService.class);

    private final SearchSuggestionRepository suggestionRepository;

    /**
     * Autocomplete search terms
     */
    public List<String> autocomplete(String prefix, int limit) {
        logger.debug("Autocomplete for prefix={} limit={}", prefix, limit);
        return suggestionRepository.findByTermStartingWithIgnoreCaseOrderByUsageCountDesc(prefix)
                .stream()
                .map(SearchSuggestion::getTerm)
                .limit(limit)
                .toList();
    }

    /**
     * Record a search term usage
     */
    public void recordSearchTerm(String term) {
        logger.debug("Recording search term={}", term);
        SearchSuggestion suggestion = suggestionRepository.findByTermStartingWithIgnoreCaseOrderByUsageCountDesc(term)
                .stream()
                .filter(s -> s.getTerm().equalsIgnoreCase(term))
                .findFirst()
                .orElse(GetNewSuggestion(term));
        suggestion.setUsageCount(suggestion.getUsageCount() + 1);
        suggestion.setLastUsed(Instant.now());
        suggestionRepository.save(suggestion);
    }

    private SearchSuggestion GetNewSuggestion(String term) {
        SearchSuggestion s = new SearchSuggestion();
        s.setTerm(term);
        s.setUsageCount(0);
        s.setLastUsed(Instant.now());
        return s;
    }
}
