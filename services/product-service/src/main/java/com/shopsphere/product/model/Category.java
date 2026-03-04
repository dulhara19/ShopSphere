package com.shopsphere.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;
    private String iconUrl;
    private String parentCategoryId; 
}