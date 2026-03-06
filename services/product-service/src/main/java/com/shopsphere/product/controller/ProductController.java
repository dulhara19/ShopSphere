package com.shopsphere.product.controller;

import com.shopsphere.product.dto.*;
import com.shopsphere.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId) {

        Page<ProductResponse> result;
        if (keyword != null && !keyword.isBlank()) {
            result = productService.searchProducts(keyword, page, size);
        } else if (categoryId != null && !categoryId.isBlank()) {
            result = productService.getProductsByCategory(categoryId, page, size);
        } else {
            result = productService.listProducts(page, size, sortBy, direction);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new MessageResponse("Product deleted successfully"));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<ProductResponse>> getSellerProducts(
            @PathVariable String sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId, page, size));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductResponse> uploadImages(
            @PathVariable String id,
            @RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok(productService.uploadImages(id, images));
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<ProductResponse> deleteImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {
        return ResponseEntity.ok(productService.deleteImage(id, imageUrl));
    }

    @PatchMapping("/{id}/primary-image")
    public ResponseEntity<ProductResponse> setPrimaryImage(
            @PathVariable String id,
            @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(productService.setPrimaryImage(id, body.get("imageUrl")));
    }
}
