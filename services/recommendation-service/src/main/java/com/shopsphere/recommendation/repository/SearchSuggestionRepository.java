package com.shopsphere.recommendation.repository;

import com.shopsphere.recommendation.model.SearchSuggestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchSuggestionRepository extends MongoRepository<SearchSuggestion, String> {
    List<SearchSuggestion> findByTermStartingWithIgnoreCaseOrderByUsageCountDesc(String prefix);
}
