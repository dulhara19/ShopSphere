package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.SocialMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SocialMetricRepository extends JpaRepository<SocialMetric, Long> {
    List<SocialMetric> findByMetricDateBetween(LocalDate startDate, LocalDate endDate);

    List<SocialMetric> findTopByOrderByAttributedSalesDesc();
}
