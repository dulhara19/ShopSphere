package com.shopsphere.product.controller;

import com.shopsphere.product.dto.ProductInternalResponseDTO;
import com.shopsphere.product.dto.ProductSearchResponseDTO;
import com.shopsphere.product.dto.ProductValidationResponseDTO;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.service.ProductService;
import com.shopsphere.product.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin 
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    /**
     * Story 1.1.1: Create Product (Seller)
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    /**
     * Story 1.1.2: Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Story 1.1.3: Update product (Seller/Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Story 1.1.4: Delete Product (Soft Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); 
    }

    /**
     * Story 1.1.5: List seller's products (Paginated with Status Filter)
     */
    @GetMapping("/seller/me")
    public ResponseEntity<Page<Product>> getMyProducts(
            @RequestHeader("X-Seller-Id") String sellerId, 
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        
        Page<Product> products = productService.getSellerProducts(sellerId, status, page, size, sortBy);
        return ResponseEntity.ok(products);
    }

    /**
     * Story 1.3.1 - 1.3.5: Unified Product Search & Listing
     */
    @GetMapping
    public ResponseEntity<Page<Product>> listAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Page<Product> products = productService.searchProducts(
                search, categoryId, minPrice, maxPrice, page, size, sortBy, direction);
        
        return ResponseEntity.ok(products);
    }

    /**
     * Story 2.1.1: Bulk Sync Endpoint
     * Manually sync all existing MongoDB products to Elasticsearch index.
     */
    @PostMapping("/sync-to-elastic")
    public ResponseEntity<String> syncToElastic() {
        productService.syncAllProductsToElasticsearch();
        return ResponseEntity.ok("Synchronization successful!");
    }

    /**
     * Story 2.1.2: Advanced Full-Text Search using Elasticsearch
     */
    @GetMapping("/search/advanced")
    public ResponseEntity<Page<Product>> advancedSearch(
            @RequestParam(name = "q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<Product> results = productService.searchProductsInElasticsearch(keyword, page, size);
        return ResponseEntity.ok(results);
    }

    /**
     * Story 2.1.3: Autocomplete API for search bar
     * URL: GET /api/products/suggestions?q=iph
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam(name = "q") String query) {
        List<String> suggestions = productService.getAutocompleteSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Story 2.1.4: Faceted Search API
     * Returns products along with category and brand aggregations.
     * URL: GET /api/products/search/facets?q=keyword
     */
    @GetMapping("/search/facets")
    public ResponseEntity<ProductSearchResponseDTO> searchWithFacets(
            @RequestParam(name = "q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        ProductSearchResponseDTO response = productService.searchWithFacets(keyword, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Story 1.4.1: Upload multiple images for a product
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<Product> uploadProductImages(
            @PathVariable String id,
            @RequestParam("files") MultipartFile[] files) throws IOException {
        
        Product product = productService.getProductById(id);
        
        List<String> currentImages = product.getImages() != null ? product.getImages() : new ArrayList<>();
        if (currentImages.size() + files.length > 10) {
            throw new RuntimeException("Maximum 10 images allowed per product.");
        }

        for (MultipartFile file : files) {
            String imageUrl = imageService.uploadImage(file);
            currentImages.add(imageUrl);
        }

        product.setImages(currentImages);
        
        if (product.getPrimaryImage() == null && !currentImages.isEmpty()) {
            product.setPrimaryImage(currentImages.get(0));
        }

        // Image එක් කිරීමෙන් පසු product එක update කිරීම
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Story 1.4.2: Set primary image for a product
     */
    @PatchMapping("/{id}/primary-image")
    public ResponseEntity<Product> setPrimaryImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {
        
        Product product = productService.getProductById(id);
        
        if (product.getImages() == null || !product.getImages().contains(imageUrl)) {
            throw new RuntimeException("Image URL not found in product's gallery.");
        }

        product.setPrimaryImage(imageUrl);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Story 1.4.3: Delete product image from gallery and storage
     */
    @DeleteMapping("/{id}/images")
    public ResponseEntity<Product> deleteProductImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {
        
        Product product = productService.getProductById(id);
        List<String> images = product.getImages();

        if (images == null || !images.contains(imageUrl)) {
            throw new RuntimeException("Image URL not found in product gallery.");
        }

        images.remove(imageUrl);
        imageService.deleteImage(imageUrl);

        if (imageUrl.equals(product.getPrimaryImage())) {
            product.setPrimaryImage(images.isEmpty() ? null : images.get(0));
        }

        product.setImages(images);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Story 1.5.1: Get product detail for internal microservices
     */
    @GetMapping("/internal/{id}")
    public ResponseEntity<ProductInternalResponseDTO> getProductInternal(@PathVariable String id) {
        ProductInternalResponseDTO response = productService.getProductInternal(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Story 1.5.2: Batch get products (Internal use)
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ProductInternalResponseDTO>> getProductsBatch(@RequestBody List<String> ids) {
        List<ProductInternalResponseDTO> response = productService.getProductsByIds(ids);
        return ResponseEntity.ok(response);
    }

    /**
     * Story 1.5.3: Validate product existence and status (Internal use)
     */
    @PostMapping("/validate")
    public ResponseEntity<ProductValidationResponseDTO> validateProducts(@RequestBody List<String> ids) {
        ProductValidationResponseDTO response = productService.validateProducts(ids);
        return ResponseEntity.ok(response);
    }
}