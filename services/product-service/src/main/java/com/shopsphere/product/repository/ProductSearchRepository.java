package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, String> {

    // Story 2.1.2: Full-text search logic
    @Query("{\"bool\": {\"should\": [" +
           "{\"match\": {\"name\": {\"query\": \"?0\", \"fuzzy\": \"AUTO\"}}}," +
           "{\"match\": {\"description\": {\"query\": \"?0\", \"fuzzy\": \"AUTO\"}}}" +
           "]}}")
    Page<Product> findByNameOrDescription(String keyword, Pageable pageable);

    /**
     * Story 2.1.3: Autocomplete suggestions based on product name
     * This finds products where the name starts with or contains the partial keyword using a wildcard search.
     */
    @Query("{\"bool\": {\"must\": [{\"wildcard\": {\"name\": \"*?0*\"}}]}}")
    List<Product> findByNameSuggestions(String partialName, Pageable pageable);
}