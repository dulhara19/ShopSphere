package com.shopsphere.product.service;

import com.shopsphere.product.exception.ProductNotFoundException;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.repository.CategoryRepository;
import com.shopsphere.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Story 1.1.1: Create Product (Seller)
     */
    public Product createProduct(Product product) {
        product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    /**
     * Story 1.1.2: Get Product by ID
     */
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    /**
     * Story 1.1.3: Update Product (Seller/Admin)
     */
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

    /**
     * Story 1.1.4: Delete Product (Soft Delete)
     */
    public void deleteProduct(String id) {
        Product existingProduct = getProductById(id);
        existingProduct.setStatus("DELETED");
        existingProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(existingProduct);
    }

    /**
     * Story 1.1.5: List seller's products (Paginated with Status Filter)
     */
    public Page<Product> getSellerProducts(String sellerId, String status, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        
        if (status != null && !status.isEmpty()) {
            return productRepository.findBySellerIdAndStatus(sellerId, status.toUpperCase(), pageable);
        }
        
        return productRepository.findBySellerId(sellerId, pageable);
    }

    /**
     * Story 1.3.1 - 1.3.4: Final Integrated Search Service
     * Handles pagination, sorting, text search, category hierarchy, and price range filters.
     */
    public Page<Product> searchProducts(String search, String categoryId, Double minPrice, Double maxPrice, 
                                        int page, int size, String sortBy, String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Story 1.3.4: Default search keyword if not provided (match all)
        String keyword = (search != null && !search.isEmpty()) ? search : "";
        
        // Story 1.3.3: Handle null price range with default values
        Double min = (minPrice != null) ? minPrice : 0.0;
        Double max = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;

        if (categoryId != null && !categoryId.isEmpty()) {
            // Story 1.3.2: Fetch all subcategory IDs recursively
            List<String> allCategoryIds = new ArrayList<>();
            allCategoryIds.add(categoryId);
            
            List<Category> allCategories = categoryRepository.findAll();
            findChildCategoryIds(categoryId, allCategories, allCategoryIds);

            // Execute complex search with Category context
            return productRepository.searchProductsWithCategory(keyword, allCategoryIds, min, max, pageable);
        }

        // Execute global search without specific Category filter
        return productRepository.searchProductsGlobal(keyword, min, max, pageable);
    }

    /**
     * Helper method to recursively find all subcategory IDs
     */
    private void findChildCategoryIds(String parentId, List<Category> allCats, List<String> resultIds) {
        for (Category cat : allCats) {
            if (parentId.equals(cat.getParentCategoryId())) {
                resultIds.add(cat.getId());
                findChildCategoryIds(cat.getId(), allCats, resultIds);
            }
        }
    }
}