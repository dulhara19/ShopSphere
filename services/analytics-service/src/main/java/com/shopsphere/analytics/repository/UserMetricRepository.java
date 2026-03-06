package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.UserMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMetricRepository extends JpaRepository<UserMetric, Long> {

    Optional<UserMetric> findByUserIdAndMetricDate(String userId, LocalDate metricDate);

    List<UserMetric> findByUserId(String userId);

    List<UserMetric> findByMetricDateBetween(LocalDate from, LocalDate to);

    List<UserMetric> findBySegment(String segment);

    List<UserMetric> findByActivityLevel(String activityLevel);

    @Query("SELECT COUNT(DISTINCT um.userId) FROM UserMetric um WHERE um.metricDate = :date AND um.active = true")
    Long countActiveUsersByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT um.userId) FROM UserMetric um WHERE um.metricDate BETWEEN :startOfMonth AND :endOfMonth AND um.active = true")
    Long countMonthlyActiveUsers(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT COUNT(DISTINCT um.userId) FROM UserMetric um WHERE um.metricDate BETWEEN :startOfWeek AND :endOfWeek AND um.active = true")
    Long countWeeklyActiveUsers(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    @Query("SELECT um FROM UserMetric um WHERE um.metricDate = :date ORDER BY um.lifetimeValue DESC LIMIT :limit")
    List<UserMetric> findTopValuedUsers(@Param("date") LocalDate date, @Param("limit") int limit);

    List<UserMetric> findByRegistrationDateBetween(LocalDate from, LocalDate to);

    List<UserMetric> findByLastActivityDateBetween(LocalDate from, LocalDate to);
}
