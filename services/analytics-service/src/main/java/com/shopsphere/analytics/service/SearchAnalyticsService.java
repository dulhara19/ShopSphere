package com.shopsphere.analytics.service;

import com.shopsphere.analytics.model.SearchAnalytic;
import com.shopsphere.analytics.repository.SearchAnalyticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchAnalyticsService {

    private final SearchAnalyticRepository searchAnalyticRepository;

    @Transactional
    public SearchAnalytic trackSearch(String searchTerm, long resultCount, LocalDate date) {
        SearchAnalytic existing = searchAnalyticRepository.findBySearchTermAndDate(searchTerm, date)
            .orElse(null);

        if (existing != null) {
            existing.setSearchCount(existing.getSearchCount() + 1);
            if (resultCount > 0) {
                existing.setResultCount(resultCount);
            }
            return searchAnalyticRepository.save(existing);
        }

        SearchAnalytic searchAnalytic = SearchAnalytic.builder()
            .searchTerm(searchTerm)
            .date(date)
            .searchCount(1L)
            .resultCount(resultCount)
            .hasResults(resultCount > 0)
            .build();

        return searchAnalyticRepository.save(searchAnalytic);
    }

    public List<SearchAnalytic> getTopSearchTerms(int limit, LocalDate from, LocalDate to) {
        return searchAnalyticRepository.findTopSearchTerms(from, to, limit);
    }

    public List<SearchAnalytic> getZeroResultSearches(int limit, LocalDate from, LocalDate to) {
        return searchAnalyticRepository.findZeroResultSearches(from, to, limit);
    }

    public List<SearchAnalytic> getSearchAnalytics(LocalDate from, LocalDate to) {
        return searchAnalyticRepository.findByDateBetween(from, to);
    }
}
