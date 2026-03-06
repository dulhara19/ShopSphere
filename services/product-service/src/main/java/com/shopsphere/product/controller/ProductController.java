package com.shopsphere.product.controller;

import com.shopsphere.product.dto.ProductInternalResponseDTO;
import com.shopsphere.product.dto.ProductSearchResponseDTO;
import com.shopsphere.product.dto.ProductValidationResponseDTO;
import com.shopsphere.product.dto.ReviewSummaryDTO;
import com.shopsphere.product.dto.VariantSelectionResponseDTO;
import com.shopsphere.product.exception.BadRequestException;
import com.shopsphere.product.exception.ImageProcessingException;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.model.ProductVariant;
import com.shopsphere.product.model.SearchAnalytics;
import com.shopsphere.product.service.ProductService;
import com.shopsphere.product.service.ImageService;
import com.shopsphere.product.service.VisualSearchService;
import com.shopsphere.product.service.integration.ReviewIntegrationService;
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
    private VisualSearchService visualSearchService;

    @Autowired
    private ReviewIntegrationService reviewIntegrationService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);

        ReviewSummaryDTO reviewSummary = reviewIntegrationService.getProductRatingSummary(id);
        if (reviewSummary != null && reviewSummary.getTotalReviews() != null) {
            product.setAverageRating(reviewSummary.getAverageRating());
            product.setReviewCount(reviewSummary.getTotalReviews());
            product.setRatingBreakdown(reviewSummary.getRatingBreakdown());
        }

        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

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

    @PostMapping("/sync-to-elastic")
    public ResponseEntity<String> syncToElastic() {
        productService.syncAllProductsToElasticsearch();
        return ResponseEntity.ok("Synchronization successful!");
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<Page<Product>> advancedSearch(
            @RequestParam(name = "q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Product> results = productService.searchProductsInElasticsearch(keyword, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam(name = "q") String query) {
        List<String> suggestions = productService.getAutocompleteSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/search/facets")
    public ResponseEntity<ProductSearchResponseDTO> searchWithFacets(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam Map<String, String> allParams) {

        Map<String, String> attributes = new HashMap<>(allParams);
        List<String> standardParams = List.of("q", "brands", "minRating", "inStock", "page", "size", "sortBy", "direction");
        standardParams.forEach(attributes::remove);

        ProductSearchResponseDTO response = productService.searchWithFacets(
                keyword, brands, minRating, attributes, inStock, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/search")
    public ResponseEntity<List<SearchAnalytics>> getSearchAnalytics() {
        return ResponseEntity.ok(productService.getSearchAnalytics());
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<Product> addProductVariants(
            @PathVariable String id,
            @RequestBody List<ProductVariant> variants) {

        Product updatedProduct = productService.addProductVariants(id, variants);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/variants/{sku}/stock")
    public ResponseEntity<Product> updateVariantStock(
            @PathVariable String id,
            @PathVariable String sku,
            @RequestParam int quantity) {

        Product updatedProduct = productService.updateVariantStock(id, sku, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Product> uploadProductImages(
            @PathVariable String id,
            @RequestParam("files") MultipartFile[] files) throws IOException {

        Product product = productService.getProductById(id);

        List<String> currentImages = product.getImages() != null ? product.getImages() : new ArrayList<>();
        if (currentImages.size() + files.length > 10) {
            throw new BadRequestException("Maximum 10 images allowed per product.");
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

    @PatchMapping("/{id}/primary-image")
    public ResponseEntity<Product> setPrimaryImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {

        Product product = productService.getProductById(id);

        if (product.getImages() == null || !product.getImages().contains(imageUrl)) {
            throw new BadRequestException("Image URL not found in product's gallery.");
        }

        product.setPrimaryImage(imageUrl);
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<Product> deleteProductImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {

        Product product = productService.getProductById(id);
        List<String> images = product.getImages();

        if (images == null || !images.contains(imageUrl)) {
            throw new BadRequestException("Image URL not found in product gallery.");
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

    @GetMapping("/internal/{id}")
    public ResponseEntity<ProductInternalResponseDTO> getProductInternal(@PathVariable String id) {
        ProductInternalResponseDTO response = productService.getProductInternal(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductInternalResponseDTO>> getProductsBatch(@RequestBody List<String> ids) {
        List<ProductInternalResponseDTO> response = productService.getProductsByIds(ids);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ProductValidationResponseDTO> validateProducts(@RequestBody List<String> ids) {
        ProductValidationResponseDTO response = productService.validateProducts(ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/variant-options")
    public ResponseEntity<VariantSelectionResponseDTO> getVariantOptions(@PathVariable String id) {
        VariantSelectionResponseDTO response = productService.getVariantSelectionOptions(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/internal/{id}/rating")
    public ResponseEntity<Void> updateProductRating(
            @PathVariable String id,
            @RequestBody ReviewSummaryDTO ratingData) {

        productService.updateProductRating(
            id,
            ratingData.getAverageRating(),
            ratingData.getTotalReviews(),
            ratingData.getRatingBreakdown()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/visual-search")
    public ResponseEntity<List<Double>> uploadImageForSearch(@RequestParam("image") MultipartFile file) {
        try {
            List<Double> visualFeatures = visualSearchService.extractFeatures(file);
            return ResponseEntity.ok(visualFeatures);
        } catch (Exception e) {
            throw new ImageProcessingException("Error processing image: " + e.getMessage(), e);
        }
    }

    @PostMapping("/visual-search/similar")
    public ResponseEntity<List<Product>> findSimilarProducts(@RequestParam("image") MultipartFile file) {
        try {
            List<Product> similarProducts = productService.findSimilarProducts(file);
            return ResponseEntity.ok(similarProducts);
        } catch (Exception e) {
            throw new ImageProcessingException("Error performing visual search: " + e.getMessage(), e);
        }
    }
}
