package com.shopsphere.product.controller;

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
     * Story 1.4.1: Upload multiple images for a product
     * Validates max 10 images and file types via ImageService.
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<Product> uploadProductImages(
            @PathVariable String id,
            @RequestParam("files") MultipartFile[] files) throws IOException {
        
        Product product = productService.getProductById(id);
        
        // Acceptance Criteria: Max 10 images per product
        List<String> currentImages = product.getImages() != null ? product.getImages() : new ArrayList<>();
        if (currentImages.size() + files.length > 10) {
            throw new RuntimeException("Maximum 10 images allowed per product.");
        }

        for (MultipartFile file : files) {
            String imageUrl = imageService.uploadImage(file);
            currentImages.add(imageUrl);
        }

        product.setImages(currentImages);
        
        // Story 1.4.2: Automatically set the first image as primary if none exists
        if (product.getPrimaryImage() == null && !currentImages.isEmpty()) {
            product.setPrimaryImage(currentImages.get(0));
        }

        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }
}