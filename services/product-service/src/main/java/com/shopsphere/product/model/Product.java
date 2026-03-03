// Product.java
package com.shopsphere.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String id;
    private String sellerId; 
    private String name;
    private String description;
    private Double price;
    private String categoryId;
    
    // Category details include karanna me field eka dammu
    private Category category; 
    
    private String sku; 
    private List<String> images;
    private String status; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Category.java (Simple DTO ekak widihata)
@Data
class Category {
    private String id;
    private String name;
}