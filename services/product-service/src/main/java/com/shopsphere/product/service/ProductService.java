package com.shopsphere.product.service;

import com.shopsphere.product.model.Product;
import com.shopsphere.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
      
        product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
       
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
}