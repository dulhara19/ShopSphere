package com.shopsphere.product.service;

import com.shopsphere.product.dto.ProductInternalResponseDTO;
import com.shopsphere.product.dto.ProductSearchResponseDTO;
import com.shopsphere.product.dto.ProductValidationResponseDTO;
import com.shopsphere.product.dto.VariantCombinationDTO; // Added for Story 2.2.4
import com.shopsphere.product.dto.VariantSelectionResponseDTO; // Added for Story 2.2.4
import com.shopsphere.product.exception.ProductNotFoundException;
import com.shopsphere.product.model.Category;
import com.shopsphere.product.model.Product;
import com.shopsphere.product.model.ProductVariant; // Added for Story 2.2.2
import com.shopsphere.product.model.SearchAnalytics; // Added for Story 2.1.5
import com.shopsphere.product.repository.CategoryRepository;
import com.shopsphere.product.repository.ProductRepository;
import com.shopsphere.product.repository.ProductSearchRepository;
import com.shopsphere.product.repository.SearchAnalyticsRepository; // Added for Story 2.1.5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.scheduling.annotation.Async; // Added for Story 2.1.5
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet; // Added for Story 2.2.4
import java.util.List;
import java.util.Map;
import java.util.Set; // Added for Story 2.2.4
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private SearchAnalyticsRepository searchAnalyticsRepository; // Injected for Analytics

    /**
     * Story 1.1.1: Create Product (Seller)
     * Story 2.1.1: Sync with Elasticsearch
     */
    public Product createProduct(Product product) {
        product.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setStatus("ACTIVE");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
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
        
        Product updatedProduct = productRepository.save(existingProduct);
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
        
        productRepository.save(existingProduct);
        productSearchRepository.deleteById(id);
    }

    /**
     * Story 1.1.5: List seller's products
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
     * Story 2.1.5: Async method to track search queries
     * This saves the keyword and the number of results found without blocking the main thread.
     */
    @Async
    public void trackSearch(String keyword, long resultCount) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            SearchAnalytics record = SearchAnalytics.builder()
                    .query(keyword.trim().toLowerCase())
                    .resultCount(resultCount)
                    .timestamp(LocalDateTime.now())
                    .build();
            searchAnalyticsRepository.save(record);
        }
    }

    /**
     * Story 2.1.2: Advanced Full-Text Search using Elasticsearch
     * Updated to track search analytics (Story 2.1.5)
     */
    public Page<Product> searchProductsInElasticsearch(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        if (keyword == null || keyword.isEmpty()) {
            return (Page<Product>) productSearchRepository.findAll(pageable);
        }
        
        Page<Product> results = productSearchRepository.findByNameOrDescription(keyword, pageable);
        
        // Track the search request asynchronously
        trackSearch(keyword, results.getTotalElements());
        
        return results;
    }

    /**
     * Story 2.1.4: Faceted Search Implementation
     * Updated to track search analytics (Story 2.1.5) and fix aggregation red lines
     */
    @SuppressWarnings("unchecked")
    public ProductSearchResponseDTO searchWithFacets(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Query query = NativeQuery.builder()
                .withQuery(q -> q
                    .bool(b -> b
                        .should(s -> s.match(m -> m.field("name").query(keyword).fuzziness("AUTO")))
                        .should(s -> s.match(m -> m.field("description").query(keyword).fuzziness("AUTO")))
                    )
                )
                .withAggregation("category_counts", Aggregation.of(a -> a.terms(t -> t.field("categoryId"))))
                .withAggregation("brand_counts", Aggregation.of(a -> a.terms(t -> t.field("brand"))))
                .withPageable(pageable)
                .build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(query, Product.class);
        
        // Track the search request asynchronously
        if(keyword != null && !keyword.isEmpty()){
            trackSearch(keyword, searchHits.getTotalHits());
        }

        Map<String, Long> categoryFacets = new HashMap<>();
        Map<String, Long> brandFacets = new HashMap<>();

        // Extract aggregations correctly for Elasticsearch 8.x
        if (searchHits.hasAggregations()) {
            // Processing Category Facets
            org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations aggregations = 
                (org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations) searchHits.getAggregations();
            
            if (aggregations.aggregationsAsMap().containsKey("category_counts")) {
                co.elastic.clients.elasticsearch._types.aggregations.Aggregate aggregate = 
                    aggregations.aggregationsAsMap().get("category_counts").aggregation().getAggregate();
                if (aggregate.isSterms()) {
                    for (StringTermsBucket bucket : aggregate.sterms().buckets().array()) {
                        categoryFacets.put(bucket.key().stringValue(), bucket.docCount());
                    }
                }
            }

            // Processing Brand Facets
            if (aggregations.aggregationsAsMap().containsKey("brand_counts")) {
                co.elastic.clients.elasticsearch._types.aggregations.Aggregate aggregate = 
                    aggregations.aggregationsAsMap().get("brand_counts").aggregation().getAggregate();
                if (aggregate.isSterms()) {
                    for (StringTermsBucket bucket : aggregate.sterms().buckets().array()) {
                        brandFacets.put(bucket.key().stringValue(), bucket.docCount());
                    }
                }
            }
        }

        // Convert SearchHits to Page safely
        SearchPage<Product> searchPage = SearchHitSupport.searchPageFor(searchHits, query.getPageable());
        Page<Product> productPage = (Page<Product>) SearchHitSupport.unwrapSearchHits(searchPage);

        return ProductSearchResponseDTO.builder()
                .products(productPage)
                .categoryFacets(categoryFacets)
                .brandFacets(brandFacets)
                .build();
    }

    /**
     * Story 2.1.1: Bulk Indexing Logic
     */
    public void syncAllProductsToElasticsearch() {
        List<Product> allProducts = productRepository.findAll();
        productSearchRepository.saveAll(allProducts);
    }

    /**
     * Story 2.1.3: Get product name suggestions for autocomplete
     */
    public List<String> getAutocompleteSuggestions(String query) {
        Pageable pageable = PageRequest.of(0, 5);
        List<Product> products = productSearchRepository.findByNameSuggestions(query.toLowerCase(), pageable);
        
        return products.stream()
                .map(Product::getName)
                .distinct()
                .toList();
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
        return ProductValidationResponseDTO.builder().allValid(results.values().stream().allMatch(v -> v)).results(results).build();
    }

    private void findChildCategoryIds(String parentId, List<Category> allCats, List<String> resultIds) {
        for (Category cat : allCats) {
            if (parentId.equals(cat.getParentCategoryId())) {
                resultIds.add(cat.getId());
                findChildCategoryIds(cat.getId(), allCats, resultIds);
            }
        }
    }

    /**
     * Story 2.2.2: Add or Update variants for a parent product
     * Generates SKUs for variants and syncs with Elasticsearch.
     */
    public Product addProductVariants(String productId, List<ProductVariant> variants) {
        Product product = getProductById(productId);
        
        // Mark that this product now has variations
        product.setHasVariations(true);
        
        for (ProductVariant variant : variants) {
            // Auto-generate variant SKU if not provided: PARENT-SKU-ATTR1-ATTR2
            if (variant.getSku() == null || variant.getSku().isEmpty()) {
                String attrValues = String.join("-", variant.getAttributes().values());
                // Remove spaces and make uppercase for SKU standard
                attrValues = attrValues.replaceAll("\\s+", "").toUpperCase();
                variant.setSku(product.getSku() + "-" + attrValues);
            }
            
            // Set availability based on stock
            variant.setAvailable(variant.getStockQuantity() != null && variant.getStockQuantity() > 0);
        }

        product.setVariants(variants);
        product.setUpdatedAt(LocalDateTime.now());

        // Save to MongoDB
        Product updatedProduct = productRepository.save(product);
        
        // Sync to Elasticsearch
        productSearchRepository.save(updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Story 2.2.3: Variant inventory tracking
     * Updates the stock quantity of a specific variant and adjusts its availability.
     */
    public Product updateVariantStock(String productId, String sku, int newQuantity) {
        Product product = getProductById(productId);
        
        if (!product.isHasVariations() || product.getVariants() == null) {
            throw new RuntimeException("Product does not have variations.");
        }
        
        boolean variantFound = false;
        for (ProductVariant variant : product.getVariants()) {
            if (variant.getSku().equals(sku)) {
                variant.setStockQuantity(newQuantity);
                // Automatically mark as unavailable if stock is 0 (Out of stock logic)
                variant.setAvailable(newQuantity > 0);
                variantFound = true;
                break;
            }
        }
        
        if (!variantFound) {
            throw new RuntimeException("Variant with SKU: " + sku + " not found.");
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        
        // Save to MongoDB and sync with Elasticsearch
        Product updatedProduct = productRepository.save(product);
        productSearchRepository.save(updatedProduct);
        
        return updatedProduct;
    }

    /**
     * Story 2.2.4: Variant selection UI support
     * Formats variations into an easy-to-use structure for the frontend UI.
     */
    public VariantSelectionResponseDTO getVariantSelectionOptions(String productId) {
        Product product = getProductById(productId);
        
        Map<String, Set<String>> availableAttributes = new HashMap<>();
        List<VariantCombinationDTO> combinations = new ArrayList<>();

        if (!product.isHasVariations() || product.getVariants() == null || product.getVariants().isEmpty()) {
            // If the product has no variations, return empty DTOs to avoid null pointers in the frontend
            return VariantSelectionResponseDTO.builder()
                    .availableAttributes(availableAttributes)
                    .combinations(combinations)
                    .build();
        }

        for (ProductVariant variant : product.getVariants()) {
            // 1. Extract unique attributes (e.g., gets all unique sizes and colors)
            for (Map.Entry<String, String> entry : variant.getAttributes().entrySet()) {
                availableAttributes
                    .computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                    .add(entry.getValue());
            }

            // 2. Create the exact combination record
            combinations.add(VariantCombinationDTO.builder()
                    .sku(variant.getSku())
                    .attributes(variant.getAttributes())
                    // Fallback to base product price if variant doesn't have a specific price
                    .price(variant.getPrice() != null ? variant.getPrice() : product.getPrice()) 
                    .inStock(variant.isAvailable())
                    .build());
        }

        return VariantSelectionResponseDTO.builder()
                .availableAttributes(availableAttributes)
                .combinations(combinations)
                .build();
    }
}