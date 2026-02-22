package com.shopsphere.analytics.controller;

import com.shopsphere.analytics.dto.ApiResponseDTO;
import com.shopsphere.analytics.dto.UserMetricDTO;
import com.shopsphere.analytics.service.UserAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/analytics/users")
@RequiredArgsConstructor
public class UserAnalyticsController {

    private final UserAnalyticsService userAnalyticsService;

    @GetMapping("/registrations")
    public ResponseEntity<ApiResponseDTO<Long>> getNewUserRegistrations(
        @RequestParam(name = "from") String from,
        @RequestParam(name = "to") String to) {
        
        LocalDate fromDate = LocalDate.parse(from);
        LocalDate toDate = LocalDate.parse(to);
        
        Long count = userAnalyticsService.getNewUsersCount(fromDate, toDate);
        return ResponseEntity.ok(ApiResponseDTO.success(count, "New user registrations retrieved"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<Long>> getActiveUsers(
        @RequestParam(name = "metric", defaultValue = "dau") String metric,
        @RequestParam(name = "date", required = false) String date) {
        
        LocalDate dateParam = date != null ? LocalDate.parse(date) : LocalDate.now();
        
        Long count = switch (metric.toLowerCase()) {
            case "dau" -> userAnalyticsService.getDailyActiveUsers(dateParam);
            case "wau" -> {
                LocalDate weekStart = dateParam.minusDays(dateParam.getDayOfWeek().getValue() - 1);
                LocalDate weekEnd = weekStart.plusDays(6);
                yield userAnalyticsService.getWeeklyActiveUsers(weekStart, weekEnd);
            }
            case "mau" -> {
                LocalDate monthStart = dateParam.withDayOfMonth(1);
                LocalDate monthEnd = dateParam.withDayOfMonth(dateParam.lengthOfMonth());
                yield userAnalyticsService.getMonthlyActiveUsers(monthStart, monthEnd);
            }
            default -> 0L;
        };
        
        return ResponseEntity.ok(ApiResponseDTO.success(count, "Active users retrieved"));
    }

    @GetMapping("/retention")
    public ResponseEntity<ApiResponseDTO<List<UserMetricDTO>>> getUserRetention(
        @RequestParam(name = "cohort") String cohort) {
        
        YearMonth cohortMonth = YearMonth.parse(cohort);
        List<UserMetricDTO> retention = userAnalyticsService.getRetention(cohortMonth);
        return ResponseEntity.ok(ApiResponseDTO.success(retention, "User retention retrieved"));
    }

    @GetMapping("/segments")
    public ResponseEntity<ApiResponseDTO<List<UserMetricDTO>>> getUserSegments(
        @RequestParam(name = "date", required = false) String date) {
        
        LocalDate dateParam = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<UserMetricDTO> segments = userAnalyticsService.getUserSegments(dateParam);
        return ResponseEntity.ok(ApiResponseDTO.success(segments, "User segments retrieved"));
    }

    @GetMapping("/ltv-distribution")
    public ResponseEntity<ApiResponseDTO<List<UserMetricDTO>>> getLifetimeValueDistribution(
        @RequestParam(name = "limit", defaultValue = "10") int limit,
        @RequestParam(name = "date", required = false) String date) {
        
        LocalDate dateParam = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<UserMetricDTO> users = userAnalyticsService.getTopValuedUsers(limit, dateParam);
        return ResponseEntity.ok(ApiResponseDTO.success(users, "LTV distribution retrieved"));
    }

    @GetMapping("/churn")
    public ResponseEntity<ApiResponseDTO<Long>> getChurnRate(
        @RequestParam(name = "period", defaultValue = "30") int period) {
        
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(period);
        
        Long churnRate = userAnalyticsService.getChurnRate(from, today);
        return ResponseEntity.ok(ApiResponseDTO.success(churnRate, "Churn rate retrieved"));
    }
}
