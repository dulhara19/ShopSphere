package com.shopsphere.product.controller;

import com.shopsphere.product.model.Product;
import com.shopsphere.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin 
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

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
     * Used by sellers to manage their own catalog.
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
     * URL Example: GET /api/products?search=macbook&categoryId=electronics&minPrice=1000&maxPrice=5000&page=0&size=20&sortBy=price&direction=asc
     * * Handles:
     * - Text search (name and description)
     * - Category filtering (including subcategories)
     * - Price range filtering
     * - Pagination (Default 20 items per page)
     * - Dynamic sorting (by price, name, date)
     */
    @GetMapping
    public ResponseEntity<Page<Product>> listAllProducts(
            @RequestParam(required = false) String search, // 1.3.4: Text keyword search
            @RequestParam(required = false) String categoryId, // 1.3.2: Category filter
            @RequestParam(required = false) Double minPrice, // 1.3.3: Price range min
            @RequestParam(required = false) Double maxPrice, // 1.3.3: Price range max
            @RequestParam(defaultValue = "0") int page, // 1.3.1: Pagination page
            @RequestParam(defaultValue = "20") int size, // 1.3.1: Pagination size
            @RequestParam(defaultValue = "createdAt") String sortBy, // 1.3.5: Sorting field
            @RequestParam(defaultValue = "desc") String direction) { // 1.3.5: Sorting direction
        
        // Pass all parameters to the service layer for processing
        Page<Product> products = productService.searchProducts(
                search, categoryId, minPrice, maxPrice, page, size, sortBy, direction);
        
        return ResponseEntity.ok(products);
    }
}