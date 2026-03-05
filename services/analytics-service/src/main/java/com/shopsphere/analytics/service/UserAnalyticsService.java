package com.shopsphere.analytics.service;

import com.shopsphere.analytics.dto.UserMetricDTO;
import com.shopsphere.analytics.model.UserCohort;
import com.shopsphere.analytics.model.UserMetric;
import com.shopsphere.analytics.repository.UserCohortRepository;
import com.shopsphere.analytics.repository.UserMetricRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAnalyticsService {

    private final UserMetricRepository userMetricRepository;
    private final UserCohortRepository userCohortRepository;
    private RedisTemplate<String, Object> redisTemplate;

    public UserAnalyticsService(UserMetricRepository userMetricRepository, UserCohortRepository userCohortRepository,
                                Optional<RedisTemplate<String, Object>> redisTemplate) {
        this.userMetricRepository = userMetricRepository;
        this.userCohortRepository = userCohortRepository;
        this.redisTemplate = redisTemplate.orElse(null);
    }

    private static final String USER_CACHE_KEY = "user:";
    private static final long CACHE_TTL_MINUTES = 30;

    public Long getDailyActiveUsers(LocalDate date) {
        String cacheKey = USER_CACHE_KEY + "dau:" + date;

        try {
            if (redisTemplate != null) {
                Long cached = (Long) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        Long dau = userMetricRepository.countActiveUsersByDate(date);
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, dau, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return dau;
    }

    public Long getWeeklyActiveUsers(LocalDate startOfWeek, LocalDate endOfWeek) {
        String cacheKey = USER_CACHE_KEY + "wau:" + startOfWeek + ":" + endOfWeek;

        try {
            if (redisTemplate != null) {
                Long cached = (Long) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        Long wau = userMetricRepository.countWeeklyActiveUsers(startOfWeek, endOfWeek);
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, wau, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return wau;
    }

    public Long getMonthlyActiveUsers(LocalDate startOfMonth, LocalDate endOfMonth) {
        String cacheKey = USER_CACHE_KEY + "mau:" + startOfMonth + ":" + endOfMonth;

        try {
            if (redisTemplate != null) {
                Long cached = (Long) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }
            }
        } catch (Exception e) {
            log.debug("Redis cache unavailable: {}", e.getMessage());
        }

        Long mau = userMetricRepository.countMonthlyActiveUsers(startOfMonth, endOfMonth);
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, mau, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.debug("Could not cache result to Redis: {}", e.getMessage());
        }
        return mau;
    }

    public List<UserMetricDTO> getNewUserRegistrations(LocalDate from, LocalDate to) {
        List<UserMetric> registrations = userMetricRepository.findByRegistrationDateBetween(from, to);
        return registrations.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Long getNewUsersCount(LocalDate from, LocalDate to) {
        return (long) userMetricRepository.findByRegistrationDateBetween(from, to).size();
    }

    public List<UserMetricDTO> getRetention(YearMonth cohortMonth) {
        List<UserCohort> cohorts = userCohortRepository.findByCohortDate(cohortMonth.atDay(1));
        
        return cohorts.stream()
            .map(cohort -> {
                UserMetric metric = UserMetric.builder()
                    .userId(cohort.getUserId())
                    .active(cohort.getRetained())
                    .build();
                return convertToDTO(metric);
            })
            .collect(Collectors.toList());
    }

    public List<UserMetricDTO> getUserSegments(LocalDate date) {
        List<UserMetric> metrics = userMetricRepository.findByMetricDateBetween(date, date);
        return metrics.stream()
            .collect(Collectors.groupingBy(UserMetric::getSegment))
            .values().stream()
            .flatMap(List::stream)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<UserMetricDTO> getTopValuedUsers(int limit, LocalDate date) {
        List<UserMetric> topUsers = userMetricRepository.findTopValuedUsers(date, limit);
        return topUsers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<UserMetricDTO> getChurnedUsers(LocalDate from, LocalDate to) {
        List<UserMetric> churned = userMetricRepository.findByLastActivityDateBetween(from, to);
        return churned.stream()
            .filter(m -> m.getActive() != null && !m.getActive())
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Long getChurnRate(LocalDate from, LocalDate to) {
        List<UserMetric> previouslyActive = userMetricRepository.findByMetricDateBetween(from, to);
        List<UserMetric> churnedMetrics = userMetricRepository.findByLastActivityDateBetween(from, to).stream()
            .filter(m -> m.getActive() != null && !m.getActive())
            .collect(Collectors.toList());
        
        if (previouslyActive.isEmpty()) {
            return 0L;
        }

        return ((long) churnedMetrics.size() * 100) / previouslyActive.size();
    }

    @Transactional
    public void aggregateDailyUserMetrics(LocalDate date) {
        log.info("Aggregating user metrics for {}", date);
    }

    @Transactional
    public void calculateCohorts(YearMonth cohortMonth) {
        log.info("Calculating cohorts for {}", cohortMonth);
        List<UserMetric> newUsers = userMetricRepository.findByRegistrationDateBetween(
            cohortMonth.atDay(1),
            cohortMonth.atEndOfMonth()
        );

        for (UserMetric user : newUsers) {
            UserCohort cohort = UserCohort.builder()
                .userId(user.getUserId())
                .cohortDate(cohortMonth.atDay(1))
                .cohortAge(0)
                .activeUsers(1L)
                .retained(true)
                .build();
            userCohortRepository.save(cohort);
        }
    }

    private UserMetricDTO convertToDTO(UserMetric metric) {
        return UserMetricDTO.builder()
            .userId(metric.getUserId())
            .metricDate(metric.getMetricDate())
            .totalPurchases(metric.getTotalPurchases())
            .lifetimeValue(metric.getLifetimeValue())
            .lastPurchaseDay(metric.getLastPurchaseDay())
            .sessionCount(metric.getSessionCount())
            .pageViewCount(metric.getPageViewCount())
            .active(metric.getActive())
            .segment(metric.getSegment())
            .activityLevel(metric.getActivityLevel())
            .registrationDate(metric.getRegistrationDate())
            .lastActivityDate(metric.getLastActivityDate())
            .build();
    }
}
