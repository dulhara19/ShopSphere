package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.ProductMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMetricRepository extends JpaRepository<ProductMetric, Long> {

    List<ProductMetric> findByProductId(String productId);

    List<ProductMetric> findByProductIdAndMetricDateBetween(String productId, LocalDate from, LocalDate to);

    List<ProductMetric> findByCategoryId(String categoryId);

    List<ProductMetric> findByCategoryIdAndMetricDateBetween(String categoryId, LocalDate from, LocalDate to);

    @Query("SELECT pm FROM ProductMetric pm WHERE pm.metricDate BETWEEN :from AND :to ORDER BY pm.viewCount DESC LIMIT :limit")
    List<ProductMetric> findTopViewedProducts(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT pm FROM ProductMetric pm WHERE pm.metricDate BETWEEN :from AND :to ORDER BY pm.purchaseCount DESC LIMIT :limit")
    List<ProductMetric> findTopSellingProducts(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT pm FROM ProductMetric pm WHERE pm.metricDate BETWEEN :from AND :to ORDER BY pm.revenue DESC LIMIT :limit")
    List<ProductMetric> findTopRevenueProducts(@Param("from") LocalDate from, @Param("to") LocalDate to, @Param("limit") int limit);

    @Query("SELECT pm FROM ProductMetric pm WHERE pm.categoryId = :categoryId AND pm.metricDate BETWEEN :from AND :to ORDER BY pm.revenue DESC")
    List<ProductMetric> findTopProductsByCategory(@Param("categoryId") String categoryId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    List<ProductMetric> findByMetricDateBetween(LocalDate from, LocalDate to);
}
