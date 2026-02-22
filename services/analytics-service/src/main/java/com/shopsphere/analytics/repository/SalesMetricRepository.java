package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.SalesMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesMetricRepository extends JpaRepository<SalesMetric, Long> {

    List<SalesMetric> findByMetricDateBetween(LocalDate from, LocalDate to);

    List<SalesMetric> findByMetricDateBetweenAndGranularity(LocalDate from, LocalDate to, String granularity);

    List<SalesMetric> findByMetricDateBetweenAndCategoryId(LocalDate from, LocalDate to, String categoryId);

    List<SalesMetric> findByMetricDateBetweenAndProductId(LocalDate from, LocalDate to, String productId);

    Optional<SalesMetric> findByMetricDateAndCategoryIdAndGranularity(LocalDate date, String categoryId, String granularity);

    @Query("SELECT sm FROM SalesMetric sm WHERE sm.metricDate BETWEEN :from AND :to ORDER BY sm.totalRevenue DESC LIMIT :limit")
    List<SalesMetric> findTopCategories(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT sm FROM SalesMetric sm WHERE sm.metricDate BETWEEN :from AND :to AND sm.productId IS NOT NULL ORDER BY sm.totalRevenue DESC LIMIT :limit")
    List<SalesMetric> findTopProducts(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT SUM(sm.totalRevenue) FROM SalesMetric sm WHERE sm.metricDate BETWEEN :from AND :to")
    Optional<Object> sumRevenueBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<SalesMetric> findByMetricDateBetweenAndSeller(LocalDate from, LocalDate to, String seller);
}
