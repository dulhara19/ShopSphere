package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.UserCohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserCohortRepository extends JpaRepository<UserCohort, Long> {

    List<UserCohort> findByCohortDate(LocalDate cohortDate);

    List<UserCohort> findByUserId(String userId);

    @Query("SELECT uc FROM UserCohort uc WHERE uc.cohortDate = :cohortDate AND uc.cohortAge = :cohortAge")
    List<UserCohort> findCohortByDateAndAge(@Param("cohortDate") LocalDate cohortDate, @Param("cohortAge") Integer cohortAge);

    @Query("SELECT SUM(uc.activeUsers) FROM UserCohort uc WHERE uc.cohortDate = :cohortDate AND uc.cohortAge = :cohortAge")
    Long sumActiveUsersByCohort(@Param("cohortDate") LocalDate cohortDate, @Param("cohortAge") Integer cohortAge);

    @Query("SELECT COUNT(uc) FROM UserCohort uc WHERE uc.cohortDate = :cohortDate AND uc.retained = true")
    Long countRetainedByDate(@Param("cohortDate") LocalDate cohortDate);
}
