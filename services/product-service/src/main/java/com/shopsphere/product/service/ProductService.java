package com.shopsphere.product.service;

import com.shopsphere.product.exception.ProductNotFoundException;
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

    // 1.1.1: Create Product
    public Product createProduct(Product product) {
        product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    // 1.1.2: Get Product by ID
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    // 1.1.3: Update Product (Seller/Admin)
    public Product updateProduct(String id, Product productDetails) {
     
        Product existingProduct = getProductById(id);

      
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setCategoryId(productDetails.getCategoryId());
        
        
        if (productDetails.getStatus() != null) {
            existingProduct.setStatus(productDetails.getStatus());
        }

       
        existingProduct.setUpdatedAt(LocalDateTime.now());

      
        return productRepository.save(existingProduct);
    }
}