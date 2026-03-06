/**
 * Order Service API Client
 *
 * Handles shopping cart, checkout, and order management.
 * Service: order-service (Port 3004)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_ORDER_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_ORDER_SERVICE_URL is correct
 */

import { orderClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  Cart,
  CartTotals,
  CartValidationResponse,
  AddToCartRequest,
  UpdateCartItemRequest,
  MergeCartRequest,
  ApplyCouponRequest,
} from '@/types/cart';
import {
  Order,
  OrderDetail,
  OrderSummary,
  CheckoutRequest,
  CheckoutResponse,
  CancelOrderRequest,
  OrderListParams,
  AdminOrderListParams,
  UpdateOrderStatusRequest,
} from '@/types/order';

export const orderApi = {
  // ==========================================
  // Cart
  // ==========================================

  /**
   * GET /api/cart
   * Get user's shopping cart
   */
  getCart: () => orderClient.get<ApiResponse<Cart>>('/api/cart'),

  /**
   * POST /api/cart/items
   * Add item to cart
   */
  addToCart: (data: AddToCartRequest) =>
    orderClient.post<ApiResponse<Cart>>('/api/cart/items', data),

  /**
   * PUT /api/cart/items/{itemId}
   * Update item quantity
   */
  updateCartItem: (itemId: string, data: UpdateCartItemRequest) =>
    orderClient.put<ApiResponse<Cart>>(`/api/cart/items/${itemId}`, data),

  /**
   * DELETE /api/cart/items/{itemId}
   * Remove item from cart
   */
  removeCartItem: (itemId: string) =>
    orderClient.delete<ApiResponse<Cart>>(`/api/cart/items/${itemId}`),

  /**
   * DELETE /api/cart
   * Clear entire cart
   */
  clearCart: () => orderClient.delete<ApiResponse<void>>('/api/cart'),

  /**
   * POST /api/cart/merge
   * Merge guest cart with user cart after login
   */
  mergeCart: (data: MergeCartRequest) =>
    orderClient.post<ApiResponse<Cart>>('/api/cart/merge', data),

  /**
   * POST /api/cart/validate
   * Validate cart for checkout (stock, prices)
   */
  validateCart: () =>
    orderClient.post<ApiResponse<CartValidationResponse>>('/api/cart/validate'),

  /**
   * GET /api/cart/totals
   * Get cart totals with shipping and tax
   */
  getCartTotals: () =>
    orderClient.get<ApiResponse<CartTotals>>('/api/cart/totals'),

  /**
   * POST /api/cart/apply-coupon
   * Apply coupon code
   */
  applyCoupon: (data: ApplyCouponRequest) =>
    orderClient.post<ApiResponse<Cart>>('/api/cart/apply-coupon', data),

  /**
   * DELETE /api/cart/coupon
   * Remove applied coupon
   */
  removeCoupon: () => orderClient.delete<ApiResponse<Cart>>('/api/cart/coupon'),

  // ==========================================
  // Checkout
  // ==========================================

  /**
   * POST /api/orders/checkout
   * Create order from cart
   */
  checkout: (data: CheckoutRequest) =>
    orderClient.post<ApiResponse<CheckoutResponse>>('/api/orders/checkout', data),

  // ==========================================
  // Orders
  // ==========================================

  /**
   * GET /api/orders
   * List user's orders
   */
  getOrders: (params: OrderListParams) =>
    orderClient.get<PaginatedResponse<OrderSummary>>('/api/orders', { params }),

  /**
   * GET /api/orders/{id}
   * Get order details
   */
  getOrder: (id: string) =>
    orderClient.get<ApiResponse<OrderDetail>>(`/api/orders/${id}`),

  /**
   * GET /api/orders/{id}/status
   * Get order status with history
   */
  getOrderStatus: (id: string) =>
    orderClient.get<ApiResponse<OrderDetail>>(`/api/orders/${id}/status`),

  /**
   * POST /api/orders/{id}/cancel
   * Cancel an order
   */
  cancelOrder: (id: string, data?: CancelOrderRequest) =>
    orderClient.post<ApiResponse<Order>>(`/api/orders/${id}/cancel`, data),

  // ==========================================
  // Admin Endpoints
  // ==========================================

  /**
   * GET /api/admin/orders
   * List all orders (admin)
   */
  listAllOrders: (params: AdminOrderListParams) =>
    orderClient.get<PaginatedResponse<OrderSummary>>('/api/admin/orders', { params }),

  /**
   * PUT /api/admin/orders/{id}/status
   * Update order status (admin)
   */
  updateOrderStatus: (id: string, data: UpdateOrderStatusRequest) =>
    orderClient.put<ApiResponse<Order>>(`/api/admin/orders/${id}/status`, data),

  // ==========================================
  // Internal Endpoints
  // ==========================================

  /**
   * GET /internal/orders/{id}
   * Get order (internal)
   */
  getOrderInternal: (id: string) =>
    orderClient.get<ApiResponse<Order>>(`/internal/orders/${id}`),

  /**
   * PUT /internal/orders/{id}/status
   * Update order status (internal)
   */
  updateOrderStatusInternal: (id: string, data: UpdateOrderStatusRequest) =>
    orderClient.put<ApiResponse<Order>>(`/internal/orders/${id}/status`, data),
};
