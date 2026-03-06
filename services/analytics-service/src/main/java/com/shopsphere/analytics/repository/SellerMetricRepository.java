package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.SellerMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SellerMetricRepository extends JpaRepository<SellerMetric, Long> {
    List<SellerMetric> findBySellerIdAndMetricDateBetween(String sellerId, LocalDate start, LocalDate end);

    List<SellerMetric> findTopByMetricDateOrderByGrossRevenueDesc(LocalDate date);
}
