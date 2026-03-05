package com.shopsphere.analytics.service;

import com.shopsphere.analytics.model.SearchAnalytic;
import com.shopsphere.analytics.repository.SearchAnalyticRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SearchAnalyticsServiceTest {

    @Mock
    private SearchAnalyticRepository searchAnalyticRepository;

    @InjectMocks
    private SearchAnalyticsService searchAnalyticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTrackSearchNewTerm() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(searchAnalyticRepository.findBySearchTermAndDate("shoes", date))
            .thenReturn(Optional.empty());

        SearchAnalytic saved = SearchAnalytic.builder()
            .id(1L)
            .searchTerm("shoes")
            .date(date)
            .searchCount(1L)
            .resultCount(50L)
            .hasResults(true)
            .build();

        when(searchAnalyticRepository.save(any(SearchAnalytic.class))).thenReturn(saved);

        SearchAnalytic result = searchAnalyticsService.trackSearch("shoes", 50, date);

        assertNotNull(result);
        assertEquals("shoes", result.getSearchTerm());
        assertEquals(1L, result.getSearchCount());
        assertTrue(result.getHasResults());
    }

    @Test
    public void testTrackSearchExistingTerm() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        SearchAnalytic existing = SearchAnalytic.builder()
            .id(1L)
            .searchTerm("shoes")
            .date(date)
            .searchCount(5L)
            .resultCount(50L)
            .hasResults(true)
            .build();

        when(searchAnalyticRepository.findBySearchTermAndDate("shoes", date))
            .thenReturn(Optional.of(existing));
        when(searchAnalyticRepository.save(any(SearchAnalytic.class))).thenReturn(existing);

        SearchAnalytic result = searchAnalyticsService.trackSearch("shoes", 50, date);

        assertNotNull(result);
        assertEquals(6L, result.getSearchCount());
        verify(searchAnalyticRepository, times(1)).save(any(SearchAnalytic.class));
    }

    @Test
    public void testTrackSearchZeroResults() {
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(searchAnalyticRepository.findBySearchTermAndDate("nonexistent", date))
            .thenReturn(Optional.empty());

        SearchAnalytic saved = SearchAnalytic.builder()
            .id(1L)
            .searchTerm("nonexistent")
            .date(date)
            .searchCount(1L)
            .resultCount(0L)
            .hasResults(false)
            .build();

        when(searchAnalyticRepository.save(any(SearchAnalytic.class))).thenReturn(saved);

        SearchAnalytic result = searchAnalyticsService.trackSearch("nonexistent", 0, date);

        assertNotNull(result);
        assertFalse(result.getHasResults());
    }

    @Test
    public void testGetTopSearchTerms() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SearchAnalytic term1 = SearchAnalytic.builder()
            .searchTerm("shoes")
            .date(from)
            .searchCount(100L)
            .resultCount(50L)
            .hasResults(true)
            .build();

        SearchAnalytic term2 = SearchAnalytic.builder()
            .searchTerm("shirts")
            .date(from)
            .searchCount(80L)
            .resultCount(30L)
            .hasResults(true)
            .build();

        when(searchAnalyticRepository.findTopSearchTerms(from, to, 10))
            .thenReturn(List.of(term1, term2));

        List<SearchAnalytic> result = searchAnalyticsService.getTopSearchTerms(10, from, to);

        assertEquals(2, result.size());
        assertEquals("shoes", result.get(0).getSearchTerm());
    }

    @Test
    public void testGetZeroResultSearches() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        SearchAnalytic noResult = SearchAnalytic.builder()
            .searchTerm("xyz123")
            .date(from)
            .searchCount(10L)
            .resultCount(0L)
            .hasResults(false)
            .build();

        when(searchAnalyticRepository.findZeroResultSearches(from, to, 10))
            .thenReturn(List.of(noResult));

        List<SearchAnalytic> result = searchAnalyticsService.getZeroResultSearches(10, from, to);

        assertEquals(1, result.size());
        assertFalse(result.get(0).getHasResults());
    }

    @Test
    public void testGetSearchAnalytics() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(searchAnalyticRepository.findByDateBetween(from, to)).thenReturn(Collections.emptyList());

        List<SearchAnalytic> result = searchAnalyticsService.getSearchAnalytics(from, to);

        assertTrue(result.isEmpty());
    }
}
