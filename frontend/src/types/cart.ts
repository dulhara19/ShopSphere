/**
 * Cart Types
 *
 * Types for shopping cart operations.
 * Based on: shared/contracts/order-service.yaml (cart section)
 */

import { ISO8601, UUID } from './api';

// Cart item
export interface CartItem {
  id: UUID;
  productId: UUID;
  productName: string;
  productImage: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  inStock: boolean;
}

// Cart entity
export interface Cart {
  id: UUID;
  userId?: UUID;
  sessionId?: string;
  items: CartItem[];
  itemCount: number;
  subtotal: number;
  updatedAt: ISO8601;
}

// Cart totals (for checkout)
export interface CartTotals {
  subtotal: number;
  taxAmount: number;
  shippingAmount: number;
  discountAmount: number;
  total: number;
  breakdown: {
    items: number;
    shipping: number;
    tax: number;
    discount: number;
  };
}

// Applied coupon
export interface AppliedCoupon {
  code: string;
  discountType: 'PERCENTAGE' | 'FIXED';
  discountValue: number;
  discountAmount: number;
}

// Add to cart request
export interface AddToCartRequest {
  productId: UUID;
  quantity: number;
}

// Update cart item request
export interface UpdateCartItemRequest {
  quantity: number;
}

// Merge cart request (after login)
export interface MergeCartRequest {
  guestCartId?: string;
  items?: Array<{
    productId: UUID;
    quantity: number;
  }>;
}

// Apply coupon request
export interface ApplyCouponRequest {
  code: string;
}

// Cart validation response
export interface CartValidationResponse {
  valid: boolean;
  items: Array<{
    productId: UUID;
    valid: boolean;
    message?: string;
    availableQuantity?: number;
  }>;
  messages: string[];
}
