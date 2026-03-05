package com.shopsphere.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products") // For MongoDB
@org.springframework.data.elasticsearch.annotations.Document(indexName = "products") 
public class Product {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Keyword)
    private String sellerId; 

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    private Category category; 
    
    @Field(type = FieldType.Keyword)
    private String sku; 
    
    private List<String> images;
    
    private String primaryImage; 
    
    @Field(type = FieldType.Keyword)
    private String status; 
    
    @Field(type = FieldType.Keyword)
    private String brand;
    
    // --- Story 2.2.2: Product Variations Additions ---
    
    // Indicates if this product has multiple variations
    private boolean hasVariations = false;
    
    // The list of variations (e.g., Red-S, Red-M, Blue-L)
    @Field(type = FieldType.Nested)
    private List<ProductVariant> variants = new ArrayList<>();
    
    // -------------------------------------------------
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}