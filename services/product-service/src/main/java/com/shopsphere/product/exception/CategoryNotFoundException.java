package com.shopsphere.product.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String id) {
        super("Category not found with id: " + id);
    }
}
