package com.shopsphere.product.repository;

import com.shopsphere.product.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
  
    boolean existsByName(String name);
}