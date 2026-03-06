package com.shopsphere.product.service;

import com.shopsphere.product.dto.*;
import com.shopsphere.product.exception.ProductNotFoundException;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.model.ProductStatus;
import com.shopsphere.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Value("${app.upload-dir:/app/uploads}")
    private String uploadDir;

    @Value("${app.base-url:}")
    private String baseUrl;

    public Page<ProductResponse> listProducts(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByDeletedFalse(pageable).map(this::toResponse);
    }

    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    public Page<ProductResponse> getProductsByCategory(String categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByDeletedFalseAndCategoryId(categoryId, pageable).map(this::toResponse);
    }

    public Page<ProductResponse> getProductsBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByDeletedFalseAndSellerId(sellerId, pageable).map(this::toResponse);
    }

    public ProductResponse getProduct(String id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .sellerId(request.getSellerId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .categoryId(request.getCategoryId())
                .images(request.getImages() != null ? request.getImages() : List.of())
                .primaryImage(request.getPrimaryImage())
                .sku(request.getSku())
                .brand(request.getBrand())
                .status(ProductStatus.ACTIVE)
                .build();
        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCompareAtPrice() != null) product.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getPrimaryImage() != null) product.setPrimaryImage(request.getPrimaryImage());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getBrand() != null) product.setBrand(request.getBrand());

        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    public ProductResponse uploadImages(String id, List<MultipartFile> files) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        List<String> imageUrls = product.getImages() != null ? new ArrayList<>(product.getImages()) : new ArrayList<>();

        try {
            Path uploadPath = Paths.get(uploadDir, "products", id);
            Files.createDirectories(uploadPath);

            for (MultipartFile file : files) {
                String ext = getExtension(file.getOriginalFilename());
                String filename = UUID.randomUUID() + ext;
                Path filePath = uploadPath.resolve(filename);
                file.transferTo(filePath.toFile());

                String imageUrl = "/uploads/products/" + id + "/" + filename;
                imageUrls.add(imageUrl);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images", e);
        }

        product.setImages(imageUrls);
        if (product.getPrimaryImage() == null && !imageUrls.isEmpty()) {
            product.setPrimaryImage(imageUrls.get(0));
        }

        return toResponse(productRepository.save(product));
    }

    public ProductResponse deleteImage(String id, String imageUrl) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        List<String> images = new ArrayList<>(product.getImages());
        images.remove(imageUrl);
        product.setImages(images);

        if (imageUrl.equals(product.getPrimaryImage())) {
            product.setPrimaryImage(images.isEmpty() ? null : images.get(0));
        }

        return toResponse(productRepository.save(product));
    }

    public ProductResponse setPrimaryImage(String id, String imageUrl) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setPrimaryImage(imageUrl);
        return toResponse(productRepository.save(product));
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }

    // Internal methods
    public ProductInternalResponse getProductInternal(String id) {
        Product product = productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toInternalResponse(product);
    }

    public List<ProductInternalResponse> getProductsBatch(List<String> ids) {
        return productRepository.findByIdInAndDeletedFalse(ids).stream()
                .map(this::toInternalResponse)
                .collect(Collectors.toList());
    }

    public ProductValidationResponse validateProduct(String id) {
        return productRepository.findById(id)
                .map(p -> ProductValidationResponse.builder()
                        .productId(id)
                        .exists(!p.isDeleted())
                        .active(p.getStatus() == ProductStatus.ACTIVE && !p.isDeleted())
                        .build())
                .orElse(ProductValidationResponse.builder()
                        .productId(id)
                        .exists(false)
                        .active(false)
                        .build());
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .categoryId(product.getCategoryId())
                .images(product.getImages())
                .primaryImage(product.getPrimaryImage())
                .status(product.getStatus())
                .sku(product.getSku())
                .brand(product.getBrand())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ProductInternalResponse toInternalResponse(Product product) {
        return ProductInternalResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .primaryImage(product.getPrimaryImage())
                .sellerId(product.getSellerId())
                .status(product.getStatus().name())
                .build();
    }
}
