package com.shopsphere.product.controller;

import com.shopsphere.product.dto.ProductInternalResponseDTO;
import com.shopsphere.product.dto.ProductSearchResponseDTO;
import com.shopsphere.product.dto.ProductValidationResponseDTO;
import com.shopsphere.product.dto.ReviewSummaryDTO; // Added for Story 2.5.1
import com.shopsphere.product.dto.VariantSelectionResponseDTO; // Added for Story 2.2.4
import com.shopsphere.product.model.Product;
import com.shopsphere.product.model.ProductVariant; // Added for Story 2.2.2
import com.shopsphere.product.model.SearchAnalytics;
import com.shopsphere.product.repository.SearchAnalyticsRepository;
import com.shopsphere.product.service.ProductService;
import com.shopsphere.product.service.ImageService;
import com.shopsphere.product.service.VisualSearchService; // Added for Story 2.4.1
import com.shopsphere.product.service.integration.ReviewIntegrationService; // Added for Story 2.5.1
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin 
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VisualSearchService visualSearchService; // Injected for Story 2.4.1

    // Injected the repository directly to resolve the red line for Analytics
    @Autowired
    private SearchAnalyticsRepository searchAnalyticsRepository;

    @Autowired
    private ReviewIntegrationService reviewIntegrationService; // Injected for Story 2.5.1

    /**
     * Story 1.1.1: Create Product (Seller)
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    /**
     * Story 1.1.2 & 2.5.1: Get product by ID with aggregated Ratings
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        // 1. Get core product details
        Product product = productService.getProductById(id);
        
        // 2. Story 2.5.1: Fetch and attach aggregated ratings from Review Service (Cached)
        ReviewSummaryDTO reviewSummary = reviewIntegrationService.getProductRatingSummary(id);
        if (reviewSummary != null && reviewSummary.getTotalReviews() != null) {
            product.setAverageRating(reviewSummary.getAverageRating());
            product.setReviewCount(reviewSummary.getTotalReviews());
        }
        
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
     * URL: GET /api/products/suggestions?q=keyword
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam(name = "q") String query) {
        List<String> suggestions = productService.getAutocompleteSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Story 2.1.4 - 2.3.5: Advanced Faceted Search API (Final Refined Version)
     * Supports complex combined filters with AND logic.
     * Returns products with categories, brands, ratings, dynamic attributes, and availability status.
     * URL Example: GET /api/products/search/facets?q=shirt&brands=Nike,Adidas&minRating=4.0&inStock=true&color=red
     */
    @GetMapping("/search/facets")
    public ResponseEntity<ProductSearchResponseDTO> searchWithFacets(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Map<String, String> allParams) {
        
        // Refined Isolation: Remove all standard parameters to leave only dynamic attributes
        Map<String, String> attributes = new HashMap<>(allParams);
        List<String> standardParams = List.of("q", "brands", "minRating", "inStock", "page", "size", "sortBy", "direction");
        standardParams.forEach(attributes::remove);

        ProductSearchResponseDTO response = productService.searchWithFacets(
                keyword, brands, minRating, attributes, inStock, page, size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Story 2.1.5: Get all search analytics (Admin only)
     * Retrieves tracked search queries and zero-result search data.
     */
    @GetMapping("/analytics/search")
    public ResponseEntity<List<SearchAnalytics>> getSearchAnalytics() {
        return ResponseEntity.ok(searchAnalyticsRepository.findAll()); 
    }

    /**
     * Story 2.2.2: Add or Update product variants
     * Allows a seller to add specific variations (e.g., Size, Color) to an existing base product.
     * URL: POST /api/products/{id}/variants
     */
    @PostMapping("/{id}/variants")
    public ResponseEntity<Product> addProductVariants(
            @PathVariable String id, 
            @RequestBody List<ProductVariant> variants) {
        
        Product updatedProduct = productService.addProductVariants(id, variants);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Story 2.2.3: Variant inventory tracking
     * Updates the stock for a specific product variant.
     * URL: PATCH /api/products/{id}/variants/{sku}/stock?quantity=50
     */
    @PatchMapping("/{id}/variants/{sku}/stock")
    public ResponseEntity<Product> updateVariantStock(
            @PathVariable String id,
            @PathVariable String sku,
            @RequestParam int quantity) {
        
        Product updatedProduct = productService.updateVariantStock(id, sku, quantity);
        return ResponseEntity.ok(updatedProduct);
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

    /**
     * Story 2.2.4: Variant selection UI support
     * Returns structured variant data for the frontend product page.
     * URL: GET /api/products/{id}/variant-options
     */
    @GetMapping("/{id}/variant-options")
    public ResponseEntity<VariantSelectionResponseDTO> getVariantOptions(@PathVariable String id) {
        VariantSelectionResponseDTO response = productService.getVariantSelectionOptions(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Story 2.3.2: Internal API to update product rating
     */
    @PatchMapping("/internal/{id}/rating")
    public ResponseEntity<Void> updateProductRating(
            @PathVariable String id,
            @RequestParam Double averageRating,
            @RequestParam Integer reviewCount) {
        
        productService.updateProductRating(id, averageRating, reviewCount);
        return ResponseEntity.ok().build();
    }

    /**
     * Story 2.4.1: Image Upload and Feature Extraction for Visual Search
     * URL: POST /api/products/visual-search
     */
    @PostMapping("/visual-search")
    public ResponseEntity<List<Double>> uploadImageForSearch(@RequestParam("image") MultipartFile file) {
        try {
            // Extract features using AI model
            List<Double> visualFeatures = visualSearchService.extractFeatures(file);
            return ResponseEntity.ok(visualFeatures);
        } catch (Exception e) {
            throw new RuntimeException("Error processing image: " + e.getMessage());
        }
    }

    /**
     * Story 2.4.2: Find similar products using visual search
     * This endpoint receives an image and returns a list of similar products based on vector similarity.
     * URL: POST /api/products/visual-search/similar
     */
    @PostMapping("/visual-search/similar")
    public ResponseEntity<List<Product>> findSimilarProducts(@RequestParam("image") MultipartFile file) {
        try {
            List<Product> similarProducts = productService.findSimilarProducts(file);
            return ResponseEntity.ok(similarProducts);
        } catch (Exception e) {
            throw new RuntimeException("Error performing visual search: " + e.getMessage());
        }
    }
}