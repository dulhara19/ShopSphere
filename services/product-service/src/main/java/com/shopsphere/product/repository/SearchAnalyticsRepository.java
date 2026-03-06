package com.shopsphere.product.repository;

import com.shopsphere.product.model.SearchAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchAnalyticsRepository extends MongoRepository<SearchAnalytics, String> {
}