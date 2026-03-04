package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.SearchAnalytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SearchAnalyticRepository extends JpaRepository<SearchAnalytic, Long> {

    Optional<SearchAnalytic> findBySearchTermAndDate(String searchTerm, LocalDate date);

    List<SearchAnalytic> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT sa FROM SearchAnalytic sa WHERE sa.date BETWEEN :from AND :to ORDER BY sa.searchCount DESC LIMIT :limit")
    List<SearchAnalytic> findTopSearchTerms(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT sa FROM SearchAnalytic sa WHERE sa.hasResults = false AND sa.date BETWEEN :from AND :to ORDER BY sa.searchCount DESC LIMIT :limit")
    List<SearchAnalytic> findZeroResultSearches(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    List<SearchAnalytic> findBySearchTermContainingIgnoreCase(String term);
}
