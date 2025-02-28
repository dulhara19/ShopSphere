/**
 * Product Service API Client
 *
 * Handles products, categories, and search.
 * Service: product-service (Port 3002)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_PRODUCT_SERVICE_URL is correct
 */

import { productClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  Product,
  ProductSummary,
  Category,
  ProductSearchParams,
  CreateProductRequest,
  UpdateProductRequest,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  ProductBatchRequest,
  ProductValidationRequest,
  ProductValidationResponse,
} from '@/types/product';

export const productApi = {
  // ==========================================
  // Products
  // ==========================================

  /**
   * GET /api/products
   * Search and list products with filters
   */
  getProducts: (params: ProductSearchParams) =>
    productClient.get<PaginatedResponse<ProductSummary>>('/api/products', { params }),

  /**
   * GET /api/products/{id}
   * Get product details by ID
   */
  getProduct: (id: string) =>
    productClient.get<ApiResponse<Product>>(`/api/products/${id}`),

  /**
   * POST /api/products
   * Create a new product (seller only)
   */
  createProduct: (data: CreateProductRequest) =>
    productClient.post<ApiResponse<Product>>('/api/products', data),

  /**
   * PUT /api/products/{id}
   * Update product (seller/admin)
   */
  updateProduct: (id: string, data: UpdateProductRequest) =>
    productClient.put<ApiResponse<Product>>(`/api/products/${id}`, data),

  /**
   * DELETE /api/products/{id}
   * Soft delete product
   */
  deleteProduct: (id: string) =>
    productClient.delete<ApiResponse<void>>(`/api/products/${id}`),

  /**
   * GET /api/products/seller/me
   * Get seller's own products
   */
  getSellerProducts: (params: ProductSearchParams) =>
    productClient.get<PaginatedResponse<Product>>('/api/products/seller/me', { params }),

  // ==========================================
  // Product Images
  // ==========================================

  /**
   * POST /api/products/{id}/images
   * Upload product images (max 10)
   */
  uploadImages: (id: string, files: File[]) => {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return productClient.post<ApiResponse<Product>>(
      `/api/products/${id}/images`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
  },

  /**
   * DELETE /api/products/{id}/images?imageUrl=
   * Delete a product image
   */
  deleteImage: (productId: string, imageUrl: string) =>
    productClient.delete<ApiResponse<void>>(`/api/products/${productId}/images`, {
      params: { imageUrl },
    }),

  /**
   * PATCH /api/products/{id}/primary-image
   * Set image as primary
   */
  setPrimaryImage: (productId: string, imageUrl: string) =>
    productClient.patch<ApiResponse<Product>>(
      `/api/products/${productId}/primary-image`,
      { imageUrl }
    ),

  // ==========================================
  // Categories
  // ==========================================

  /**
   * GET /api/categories
   * Get all categories (hierarchical)
   */
  getCategories: () =>
    productClient.get<ApiResponse<Category[]>>('/api/categories'),

  /**
   * GET /api/categories/{id}
   * Get category with subcategories
   */
  getCategory: (id: string) =>
    productClient.get<ApiResponse<Category>>(`/api/categories/${id}`),

  /**
   * GET /api/categories/{id}/subcategories
   * Get subcategories
   */
  getSubcategories: (id: string) =>
    productClient.get<ApiResponse<Category[]>>(`/api/categories/${id}/subcategories`),

  /**
   * POST /api/categories
   * Create category (admin only)
   */
  createCategory: (data: CreateCategoryRequest) =>
    productClient.post<ApiResponse<Category>>('/api/categories', data),

  /**
   * PUT /api/categories/{id}
   * Update category (admin only)
   */
  updateCategory: (id: string, data: UpdateCategoryRequest) =>
    productClient.put<ApiResponse<Category>>(`/api/categories/${id}`, data),

  /**
   * DELETE /api/categories/{id}
   * Delete category (admin only, if no products)
   */
  deleteCategory: (id: string) =>
    productClient.delete<ApiResponse<void>>(`/api/categories/${id}`),

  // ==========================================
  // Internal Endpoints
  // ==========================================

  /**
   * POST /api/products/batch
   * Batch get products by IDs
   */
  batchGetProducts: (data: ProductBatchRequest) =>
    productClient.post<ApiResponse<Product[]>>('/api/products/batch', data),

  /**
   * POST /api/products/validate
   * Validate products exist and are active
   */
  validateProducts: (data: ProductValidationRequest) =>
    productClient.post<ApiResponse<ProductValidationResponse>>(
      '/api/products/validate',
      data
    ),
};
