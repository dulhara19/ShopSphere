package com.shopsphere.product.service;

import com.shopsphere.product.dto.CategoryResponseDTO;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.repository.CategoryRepository;
import com.shopsphere.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Story 1.2.1: Create category with unique name validation
     */
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category already exists with name: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    /**
     * Story 1.2.2: List all categories with hierarchy and product counts
     * @return List of CategoryResponseDTO with parent-child structure
     */
    public List<CategoryResponseDTO> getAllCategoriesHierarchy() {
      
        List<Category> allCategories = categoryRepository.findAll();
        
        
        return allCategories.stream()
                .filter(c -> c.getParentCategoryId() == null)
                .map(c -> convertToDTO(c, allCategories))
                .collect(Collectors.toList());
    }

    
    private CategoryResponseDTO convertToDTO(Category category, List<Category> allCategories) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconUrl(category.getIconUrl());
        
      
        dto.setProductCount(productRepository.countByCategoryId(category.getId()));

       
        List<CategoryResponseDTO> subcategories = allCategories.stream()
                .filter(c -> category.getId().equals(c.getParentCategoryId()))
                .map(c -> convertToDTO(c, allCategories))
                .collect(Collectors.toList());
        
        dto.setSubcategories(subcategories);
        return dto;
    }
}