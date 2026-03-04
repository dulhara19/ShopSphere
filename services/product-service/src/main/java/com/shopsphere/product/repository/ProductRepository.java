package com.shopsphere.product.repository;

import com.shopsphere.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    
    Page<Product> findBySellerIdAndStatus(String sellerId, String status, Pageable pageable);

    
    Page<Product> findBySellerId(String sellerId, Pageable pageable);
}