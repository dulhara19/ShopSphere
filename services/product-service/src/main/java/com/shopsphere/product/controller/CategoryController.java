package com.shopsphere.product.controller;

import com.shopsphere.product.dto.CategoryDetailResponseDTO;
import com.shopsphere.product.dto.CategoryResponseDTO;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 1.2.1: Create category (Admin)
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * 1.2.2: List all categories (Hierarchy + Product count)
     * URL: GET /api/categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategoriesHierarchy();
        return ResponseEntity.ok(categories);
    }

    /**
     * 1.2.3: Get category by ID
     * URL: GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailResponseDTO> getCategoryById(@PathVariable String id) {
        CategoryDetailResponseDTO categoryDetail = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDetail);
    }

    /**
     * 1.2.4: Update category (Admin)
     * URL: PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }
}