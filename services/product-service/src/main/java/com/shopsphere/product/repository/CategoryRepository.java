package com.shopsphere.product.repository;

import com.shopsphere.product.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByParentId(String parentId);

    List<Category> findByParentIdIsNull();

    boolean existsByName(String name);
}
