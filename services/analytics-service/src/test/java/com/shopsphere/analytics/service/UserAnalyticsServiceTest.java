package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.UserMetricDTO;
import com.shopsphere.analytics.model.UserCohort;
import com.shopsphere.analytics.model.UserMetric;
import com.shopsphere.analytics.repository.UserCohortRepository;
import com.shopsphere.analytics.repository.UserMetricRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserAnalyticsServiceTest {

    @Mock
    private UserMetricRepository userMetricRepository;

    @Mock
    private UserCohortRepository userCohortRepository;

    private UserAnalyticsService userAnalyticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userAnalyticsService = new UserAnalyticsService(userMetricRepository, userCohortRepository, Optional.empty());
    }

    @Test
    public void testGetDailyActiveUsers() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        when(userMetricRepository.countActiveUsersByDate(date)).thenReturn(150L);

        Long result = userAnalyticsService.getDailyActiveUsers(date);

        assertEquals(150L, result);
    }

    @Test
    public void testGetWeeklyActiveUsers() {
        LocalDate start = LocalDate.of(2024, 1, 8);
        LocalDate end = LocalDate.of(2024, 1, 14);
        when(userMetricRepository.countWeeklyActiveUsers(start, end)).thenReturn(500L);

        Long result = userAnalyticsService.getWeeklyActiveUsers(start, end);

        assertEquals(500L, result);
    }

    @Test
    public void testGetMonthlyActiveUsers() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        when(userMetricRepository.countMonthlyActiveUsers(start, end)).thenReturn(2000L);

        Long result = userAnalyticsService.getMonthlyActiveUsers(start, end);

        assertEquals(2000L, result);
    }

    @Test
    public void testGetNewUserRegistrations() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        UserMetric user = UserMetric.builder()
            .userId("user1")
            .metricDate(from)
            .registrationDate(from)
            .totalPurchases(0L)
            .lifetimeValue(BigDecimal.ZERO)
            .lastPurchaseDay(0L)
            .sessionCount(1L)
            .pageViewCount(5L)
            .active(true)
            .build();

        when(userMetricRepository.findByRegistrationDateBetween(from, to)).thenReturn(List.of(user));

        List<UserMetricDTO> result = userAnalyticsService.getNewUserRegistrations(from, to);

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserId());
    }

    @Test
    public void testGetNewUsersCount() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        UserMetric u1 = UserMetric.builder().userId("u1").metricDate(from).build();
        UserMetric u2 = UserMetric.builder().userId("u2").metricDate(from).build();

        when(userMetricRepository.findByRegistrationDateBetween(from, to))
            .thenReturn(Arrays.asList(u1, u2));

        Long result = userAnalyticsService.getNewUsersCount(from, to);

        assertEquals(2L, result);
    }

    @Test
    public void testGetChurnedUsers() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        UserMetric active = UserMetric.builder()
            .userId("u1").metricDate(from).active(true)
            .totalPurchases(0L).lifetimeValue(BigDecimal.ZERO).lastPurchaseDay(0L)
            .sessionCount(0L).pageViewCount(0L).lastActivityDate(from)
            .build();
        UserMetric churned = UserMetric.builder()
            .userId("u2").metricDate(from).active(false)
            .totalPurchases(0L).lifetimeValue(BigDecimal.ZERO).lastPurchaseDay(0L)
            .sessionCount(0L).pageViewCount(0L).lastActivityDate(from)
            .build();

        when(userMetricRepository.findByLastActivityDateBetween(from, to))
            .thenReturn(Arrays.asList(active, churned));

        List<UserMetricDTO> result = userAnalyticsService.getChurnedUsers(from, to);

        assertEquals(1, result.size());
        assertEquals("u2", result.get(0).getUserId());
    }

    @Test
    public void testGetChurnRate() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        UserMetric u1 = UserMetric.builder().userId("u1").metricDate(from).active(true).build();
        UserMetric u2 = UserMetric.builder().userId("u2").metricDate(from).active(true).build();
        UserMetric u3 = UserMetric.builder().userId("u3").metricDate(from).active(false).build();

        when(userMetricRepository.findByMetricDateBetween(from, to)).thenReturn(Arrays.asList(u1, u2, u3));
        when(userMetricRepository.findByLastActivityDateBetween(from, to)).thenReturn(List.of(u3));

        Long result = userAnalyticsService.getChurnRate(from, to);

        assertEquals(33L, result); // 1/3 * 100 = 33
    }

    @Test
    public void testGetChurnRateEmpty() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        when(userMetricRepository.findByMetricDateBetween(from, to)).thenReturn(Collections.emptyList());
        when(userMetricRepository.findByLastActivityDateBetween(from, to)).thenReturn(Collections.emptyList());

        Long result = userAnalyticsService.getChurnRate(from, to);

        assertEquals(0L, result);
    }

    @Test
    public void testGetRetention() {
        YearMonth month = YearMonth.of(2024, 1);

        UserCohort cohort = UserCohort.builder()
            .userId("u1")
            .cohortDate(month.atDay(1))
            .cohortAge(0)
            .activeUsers(1L)
            .retained(true)
            .build();

        when(userCohortRepository.findByCohortDate(month.atDay(1))).thenReturn(List.of(cohort));

        List<UserMetricDTO> result = userAnalyticsService.getRetention(month);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
    }
}
