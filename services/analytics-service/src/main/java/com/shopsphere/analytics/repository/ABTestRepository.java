package com.shopsphere.analytics.repository;

import com.shopsphere.analytics.model.ABTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ABTestRepository extends JpaRepository<ABTest, Long> {
    List<ABTest> findByStatus(String status);
}
