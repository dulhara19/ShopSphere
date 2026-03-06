package com.shopsphere.product.service;

import com.shopsphere.product.dto.CategoryResponse;
import com.shopsphere.product.dto.CreateCategoryRequest;
import com.shopsphere.product.exception.CategoryNotFoundException;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIdIsNull().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return toResponse(category);
    }

    public List<CategoryResponse> getSubcategories(String parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .image(request.getImage())
                .description(request.getDescription())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentId())
                .image(category.getImage())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
