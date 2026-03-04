package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.CustomReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomReportRepository extends JpaRepository<CustomReport, Long> {
    List<CustomReport> findByAuthorUserId(String authorUserId);

    List<CustomReport> findByIsScheduledTrue();
}
