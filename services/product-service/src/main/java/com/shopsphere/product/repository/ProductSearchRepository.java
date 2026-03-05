package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<Product, String> {

    // Story 2.1.2: Full-text search logic
   
    @Query("{\"bool\": {\"should\": [" +
           "{\"match\": {\"name\": {\"query\": \"?0\", \"fuzzy\": \"AUTO\"}}}," +
           "{\"match\": {\"description\": {\"query\": \"?0\", \"fuzzy\": \"AUTO\"}}}" +
           "]}}")
    Page<Product> findByNameOrDescription(String keyword, Pageable pageable);
}