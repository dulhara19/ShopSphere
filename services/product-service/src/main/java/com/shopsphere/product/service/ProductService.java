package com.shopsphere.product.service;

import com.shopsphere.product.dto.ProductInternalResponseDTO;
import com.shopsphere.product.dto.ProductValidationResponseDTO;
import com.shopsphere.product.exception.ProductNotFoundException;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.repository.CategoryRepository;
import com.shopsphere.product.repository.ProductRepository;
import com.shopsphere.product.repository.ProductSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    /**
     * Story 1.1.1: Create Product (Seller)
     * Story 2.1.1: Sync with Elasticsearch
     */
    public Product createProduct(Product product) {
        product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        // Save to MongoDB
        Product savedProduct = productRepository.save(product);
        
        // Sync to Elasticsearch
        productSearchRepository.save(savedProduct);
        
        return savedProduct;
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
     * Story 2.1.1: Sync with Elasticsearch
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
        
        // Update MongoDB
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Update Elasticsearch index
        productSearchRepository.save(updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Story 1.1.4: Delete Product (Soft Delete)
     * Story 2.1.1: Remove or Update in Elasticsearch
     */
    public void deleteProduct(String id) {
        Product existingProduct = getProductById(id);
        existingProduct.setStatus("DELETED");
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        // Soft delete in MongoDB
        productRepository.save(existingProduct);
        
        // Remove from Elasticsearch search index to prevent appearing in searches
        productSearchRepository.deleteById(id);
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
     * Story 1.3.1 - 1.3.4: Final Integrated Search Service (MongoDB)
     */
    public Page<Product> searchProducts(String search, String categoryId, Double minPrice, Double maxPrice, 
                                        int page, int size, String sortBy, String direction) {
        
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String keyword = (search != null && !search.isEmpty()) ? search : "";
        Double min = (minPrice != null) ? minPrice : 0.0;
        Double max = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;

        if (categoryId != null && !categoryId.isEmpty()) {
            List<String> allCategoryIds = new ArrayList<>();
            allCategoryIds.add(categoryId);
            
            List<Category> allCategories = categoryRepository.findAll();
            findChildCategoryIds(categoryId, allCategories, allCategoryIds);

            return productRepository.searchProductsWithCategory(keyword, allCategoryIds, min, max, pageable);
        }

        return productRepository.searchProductsGlobal(keyword, min, max, pageable);
    }

    /**
     * Story 2.1.2: Advanced Full-Text Search using Elasticsearch
     * Provides high-performance search across name and description with fuzzy matching.
     */
    public Page<Product> searchProductsInElasticsearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        if (keyword == null || keyword.isEmpty()) {
            return (Page<Product>) productSearchRepository.findAll(pageable);
        }
        
        return productSearchRepository.findByNameOrDescription(keyword, pageable);
    }

    /**
     * Story 2.1.1: Bulk Indexing Logic
     * Manually sync all existing products from MongoDB to Elasticsearch index.
     */
    public void syncAllProductsToElasticsearch() {
        List<Product> allProducts = productRepository.findAll();
        productSearchRepository.saveAll(allProducts);
    }

    /**
     * Story 1.5.1: Get product by ID for internal services
     */
    public ProductInternalResponseDTO getProductInternal(String id) {
        Product product = getProductById(id);
        
        return ProductInternalResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .isAvailable("ACTIVE".equalsIgnoreCase(product.getStatus()))
                .build();
    }

    /**
     * Story 1.5.2: Batch get products by a list of IDs
     */
    public List<ProductInternalResponseDTO> getProductsByIds(List<String> ids) {
        Iterable<Product> products = productRepository.findAllById(ids);
        List<ProductInternalResponseDTO> responseList = new ArrayList<>();
        
        products.forEach(product -> {
            responseList.add(ProductInternalResponseDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .status(product.getStatus())
                    .isAvailable("ACTIVE".equalsIgnoreCase(product.getStatus()))
                    .build());
        });
        
        return responseList;
    }

    /**
     * Story 1.5.3: Validate products availability
     */
    public ProductValidationResponseDTO validateProducts(List<String> ids) {
        Iterable<Product> products = productRepository.findAllById(ids);
        Map<String, Boolean> results = new HashMap<>();
        
        for (String id : ids) {
            results.put(id, false);
        }

        products.forEach(product -> {
            if ("ACTIVE".equalsIgnoreCase(product.getStatus())) {
                results.put(product.getId(), true);
            }
        });

        boolean allValid = results.values().stream().allMatch(v -> v);

        return ProductValidationResponseDTO.builder()
                .allValid(allValid)
                .results(results)
                .build();
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