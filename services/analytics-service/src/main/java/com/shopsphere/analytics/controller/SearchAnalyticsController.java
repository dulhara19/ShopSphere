package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.model.SearchAnalytic;
import com.shopsphere.analytics.service.SearchAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics/search")
@RequiredArgsConstructor
public class SearchAnalyticsController {

    private final SearchAnalyticsService searchAnalyticsService;

    @GetMapping("/top-terms")
    public ResponseEntity<ApiResponseDTO<List<SearchAnalytic>>> getTopSearchTerms(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "20") int limit) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<SearchAnalytic> terms = searchAnalyticsService.getTopSearchTerms(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(terms, "Top search terms retrieved"));
    }

    @GetMapping("/no-results")
    public ResponseEntity<ApiResponseDTO<List<SearchAnalytic>>> getZeroResultSearches(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to,
        @RequestParam(name = "limit", defaultValue = "20") int limit) {

        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);

        List<SearchAnalytic> searches = searchAnalyticsService.getZeroResultSearches(limit, fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(searches, "Zero result searches retrieved"));
    }
}
