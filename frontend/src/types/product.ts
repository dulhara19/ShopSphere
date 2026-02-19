/**
 * Product Service Types
 *
 * Types for products, categories, and search.
 * Based on: shared/contracts/product-service.yaml
 */

import { ISO8601, UUID } from './api';

// Product status
export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'OUT_OF_STOCK';

// Product image
export interface ProductImage {
  id: UUID;
  url: string;
  thumbnailUrl: string;
  isPrimary: boolean;
  sortOrder: number;
}

// Product entity
export interface Product {
  id: UUID;
  sellerId: UUID;
  name: string;
  description: string;
  price: number;
  compareAtPrice?: number;
  categoryId: UUID;
  categoryName: string;
  subcategoryId?: UUID;
  subcategoryName?: string;
  sku: string;
  brand?: string;
  images: ProductImage[];
  status: ProductStatus;
  averageRating: number;
  reviewCount: number;
  tags: string[];
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// Product summary (for listings)
export interface ProductSummary {
  id: UUID;
  name: string;
  price: number;
  compareAtPrice?: number;
  primaryImage?: string;
  averageRating: number;
  reviewCount: number;
  status: ProductStatus;
}

// Category entity
export interface Category {
  id: UUID;
  name: string;
  description?: string;
  slug: string;
  parentId?: UUID;
  imageUrl?: string;
  productCount: number;
  subcategories?: Category[];
}

// Product variant (future use)
export interface ProductVariant {
  id: UUID;
  name: string;
  sku: string;
  price: number;
  attributes: Record<string, string>;
}

// Product search/list params
export interface ProductSearchParams {
  page?: number;
  size?: number;
  category?: UUID;
  subcategory?: UUID;
  minPrice?: number;
  maxPrice?: number;
  search?: string;
  brand?: string;
  status?: ProductStatus;
  sort?: 'price_asc' | 'price_desc' | 'newest' | 'name_asc' | 'name_desc' | 'rating';
}

// Create product request (seller)
export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  compareAtPrice?: number;
  categoryId: UUID;
  subcategoryId?: UUID;
  sku: string;
  brand?: string;
  tags?: string[];
}

// Update product request
export interface UpdateProductRequest extends Partial<CreateProductRequest> {
  status?: ProductStatus;
}

// Create category request (admin)
export interface CreateCategoryRequest {
  name: string;
  description?: string;
  parentId?: UUID;
}

// Update category request
export interface UpdateCategoryRequest extends Partial<CreateCategoryRequest> {}

// Product batch request (internal)
export interface ProductBatchRequest {
  productIds: UUID[];
}

// Product validation request (internal)
export interface ProductValidationRequest {
  items: Array<{
    productId: UUID;
    quantity: number;
  }>;
}

export interface ProductValidationResponse {
  items: Array<{
    productId: UUID;
    valid: boolean;
    reason?: string;
  }>;
  allValid: boolean;
}
