package com.shopsphere.analytics.service;

import com.shopsphere.analytics.model.ABTest;
import com.shopsphere.analytics.repository.ABTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ABTestService {

    private final ABTestRepository abTestRepository;

    public ABTest createTest(ABTest test) {
        test.setStatus("DRAFT");
        test.setStartDate(LocalDateTime.now());
        return abTestRepository.save(test);
    }

    public List<ABTest> getAllTests() {
        return abTestRepository.findAll();
    }

    public ABTest getTestById(Long id) {
        return abTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }

    public ABTest startTest(Long id) {
        ABTest test = getTestById(id);
        test.setStatus("RUNNING");
        test.setStartDate(LocalDateTime.now());
        return abTestRepository.save(test);
    }

    public ABTest concludeTest(Long id) {
        ABTest test = getTestById(id);
        test.setStatus("CONCLUDED");
        test.setEndDate(LocalDateTime.now());

        // In a real implementation we would fetch event tracking data to determine
        // significance.
        // For demonstration, simulating a statistical outcome:
        test.setWinnerVariant("Variant B");
        test.setConfidenceLevel(96.5);

        log.info("Concluded A/B Test {}, Winner: {} at {}% confidence",
                test.getTestName(), test.getWinnerVariant(), test.getConfidenceLevel());

        return abTestRepository.save(test);
    }
}
