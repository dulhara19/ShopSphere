/**
 * Order Types
 *
 * Types for orders and checkout.
 * Based on: shared/contracts/order-service.yaml
 */

import { ISO8601, UUID } from './api';
import { Address } from './user';

// Order status
export type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED';

// Payment status
export type PaymentStatus = 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';

// Order item
export interface OrderItem {
  id: UUID;
  productId: UUID;
  productName: string;
  productImage: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

// Status history item
export interface StatusHistoryItem {
  status: OrderStatus;
  timestamp: ISO8601;
  note?: string;
}

// Order entity
export interface Order {
  id: UUID;
  orderNumber: string;
  userId: UUID;
  status: OrderStatus;
  items: OrderItem[];
  shippingAddress: Address;
  billingAddress?: Address;
  subtotal: number;
  shippingAmount: number;
  taxAmount: number;
  discountAmount: number;
  totalAmount: number;
  paymentStatus: PaymentStatus;
  trackingNumber?: string;
  notes?: string;
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// Order detail (with history)
export interface OrderDetail extends Order {
  statusHistory: StatusHistoryItem[];
}

// Order summary (for list)
export interface OrderSummary {
  id: UUID;
  orderNumber: string;
  status: OrderStatus;
  itemCount: number;
  totalAmount: number;
  paymentStatus: PaymentStatus;
  createdAt: ISO8601;
}

// Checkout request
export interface CheckoutRequest {
  shippingAddressId: UUID;
  billingAddressId?: UUID;
  paymentMethodId?: string;
  couponCode?: string;
  notes?: string;
}

// Checkout response
export interface CheckoutResponse {
  order: Order;
  paymentIntent?: {
    clientSecret: string;
    amount: number;
  };
}

// Cancel order request
export interface CancelOrderRequest {
  reason?: string;
}

// Order list params
export interface OrderListParams {
  page?: number;
  size?: number;
  status?: OrderStatus;
  startDate?: string;
  endDate?: string;
}

// Admin order list params
export interface AdminOrderListParams extends OrderListParams {
  userId?: UUID;
  orderNumber?: string;
}

// Update order status (admin/internal)
export interface UpdateOrderStatusRequest {
  status: OrderStatus;
  note?: string;
}
