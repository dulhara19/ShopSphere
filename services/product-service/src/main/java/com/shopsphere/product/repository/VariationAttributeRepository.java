package com.shopsphere.product.repository;

import com.shopsphere.product.model.VariationAttribute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VariationAttributeRepository extends MongoRepository<VariationAttribute, String> {
    
    Optional<VariationAttribute> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}