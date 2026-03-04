package com.shopsphere.product.service;

import com.shopsphere.product.dto.CategoryDetailResponseDTO;
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
     */
    public List<CategoryResponseDTO> getAllCategoriesHierarchy() {
        List<Category> allCategories = categoryRepository.findAll();
        
        return allCategories.stream()
                .filter(c -> c.getParentCategoryId() == null)
                .map(c -> convertToDTO(c, allCategories))
                .collect(Collectors.toList());
    }

    /**
     * Story 1.2.3: Get category by ID with parent and subcategories details
     */
    public CategoryDetailResponseDTO getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        CategoryDetailResponseDTO dto = new CategoryDetailResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIconUrl(category.getIconUrl());
        dto.setProductCount(productRepository.countByCategoryId(category.getId()));

        if (category.getParentCategoryId() != null) {
            categoryRepository.findById(category.getParentCategoryId()).ifPresent(parent -> {
                CategoryResponseDTO parentDto = new CategoryResponseDTO();
                parentDto.setId(parent.getId());
                parentDto.setName(parent.getName());
                dto.setParentCategory(parentDto);
            });
        }

        List<Category> allCategories = categoryRepository.findAll();
        List<CategoryResponseDTO> subcategories = allCategories.stream()
                .filter(c -> category.getId().equals(c.getParentCategoryId()))
                .map(c -> {
                    CategoryResponseDTO sDto = new CategoryResponseDTO();
                    sDto.setId(c.getId());
                    sDto.setName(c.getName());
                    sDto.setProductCount(productRepository.countByCategoryId(c.getId()));
                    return sDto;
                })
                .collect(Collectors.toList());

        dto.setSubcategories(subcategories);
        return dto;
    }

    /**
     * Story 1.2.4: Update category (Admin)
     */
    public Category updateCategory(String id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (categoryDetails.getName() != null && 
            !existingCategory.getName().equals(categoryDetails.getName()) && 
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new RuntimeException("Category name already exists: " + categoryDetails.getName());
        }

        if (categoryDetails.getName() != null) existingCategory.setName(categoryDetails.getName());
        if (categoryDetails.getDescription() != null) existingCategory.setDescription(categoryDetails.getDescription());
        if (categoryDetails.getIconUrl() != null) existingCategory.setIconUrl(categoryDetails.getIconUrl());
        
        existingCategory.setParentCategoryId(categoryDetails.getParentCategoryId());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Story 1.2.5: Delete category (Admin)
     */
    public void deleteCategory(String id) {
       
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

  
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete category. It contains " + productCount + " products. Please reassign or delete products first.");
        }

        categoryRepository.deleteById(id);
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