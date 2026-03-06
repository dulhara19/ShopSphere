package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import com.shopsphere.product.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{ 'deleted': false, 'status': 'ACTIVE', '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Product> searchByKeyword(String keyword, Pageable pageable);

    Page<Product> findByDeletedFalse(Pageable pageable);

    Page<Product> findByDeletedFalseAndStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByDeletedFalseAndCategoryId(String categoryId, Pageable pageable);

    Page<Product> findByDeletedFalseAndSellerId(String sellerId, Pageable pageable);

    Optional<Product> findByIdAndDeletedFalse(String id);

    List<Product> findByIdInAndDeletedFalse(List<String> ids);
}
