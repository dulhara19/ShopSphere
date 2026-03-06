package com.shopsphere.product.controller;

import com.shopsphere.product.dto.ProductInternalResponse;
import com.shopsphere.product.dto.ProductValidationResponse;
import com.shopsphere.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductInternalResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductInternal(id));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductInternalResponse>> getProductsBatch(@RequestBody List<String> ids) {
        return ResponseEntity.ok(productService.getProductsBatch(ids));
    }

    @GetMapping("/{id}/validate")
    public ResponseEntity<ProductValidationResponse> validateProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.validateProduct(id));
    }
}
