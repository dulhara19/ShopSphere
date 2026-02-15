/**
 * Mock Order and Cart Data
 */

import { Cart, CartItem } from '@/types/cart';
import { Order, OrderSummary, OrderDetail } from '@/types/order';
import { mockProducts } from './products';
import { mockAddresses } from './users';

export const mockCartItems: CartItem[] = [
  {
    id: 'cart-item-1',
    productId: 'prod-1',
    productName: 'Premium Wireless Headphones',
    productImage:
      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
    quantity: 1,
    unitPrice: 299.99,
    totalPrice: 299.99,
    inStock: true,
  },
  {
    id: 'cart-item-2',
    productId: 'prod-4',
    productName: 'Organic Cotton T-Shirt',
    productImage:
      'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=200',
    quantity: 2,
    unitPrice: 39.99,
    totalPrice: 79.98,
    inStock: true,
  },
];

export const mockCart: Cart = {
  id: 'cart-1',
  userId: 'user-1',
  items: mockCartItems,
  itemCount: 3,
  subtotal: 379.97,
  updatedAt: new Date().toISOString(),
};

export const mockOrders: OrderDetail[] = [
  {
    id: 'order-1',
    orderNumber: 'SS-20240610-001',
    userId: 'user-1',
    status: 'DELIVERED',
    items: [
      {
        id: 'order-item-1',
        productId: 'prod-2',
        productName: 'Minimalist Leather Watch',
        productImage:
          'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200',
        quantity: 1,
        unitPrice: 149.99,
        totalPrice: 149.99,
      },
    ],
    shippingAddress: mockAddresses[0],
    subtotal: 149.99,
    shippingAmount: 9.99,
    taxAmount: 12.0,
    discountAmount: 0,
    totalAmount: 171.98,
    paymentStatus: 'PAID',
    trackingNumber: 'TRK123456789',
    statusHistory: [
      { status: 'PENDING', timestamp: '2024-06-01T10:00:00Z' },
      { status: 'CONFIRMED', timestamp: '2024-06-01T10:30:00Z' },
      { status: 'PROCESSING', timestamp: '2024-06-02T08:00:00Z' },
      { status: 'SHIPPED', timestamp: '2024-06-03T14:00:00Z' },
      { status: 'DELIVERED', timestamp: '2024-06-06T11:00:00Z' },
    ],
    createdAt: '2024-06-01T10:00:00Z',
    updatedAt: '2024-06-06T11:00:00Z',
  },
  {
    id: 'order-2',
    orderNumber: 'SS-20240615-002',
    userId: 'user-1',
    status: 'SHIPPED',
    items: [
      {
        id: 'order-item-2',
        productId: 'prod-1',
        productName: 'Premium Wireless Headphones',
        productImage:
          'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
        quantity: 1,
        unitPrice: 299.99,
        totalPrice: 299.99,
      },
      {
        id: 'order-item-3',
        productId: 'prod-5',
        productName: 'Smart Home Hub',
        productImage:
          'https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=200',
        quantity: 1,
        unitPrice: 129.99,
        totalPrice: 129.99,
      },
    ],
    shippingAddress: mockAddresses[1],
    subtotal: 429.98,
    shippingAmount: 0,
    taxAmount: 34.4,
    discountAmount: 20.0,
    totalAmount: 444.38,
    paymentStatus: 'PAID',
    trackingNumber: 'TRK987654321',
    statusHistory: [
      { status: 'PENDING', timestamp: '2024-06-15T09:00:00Z' },
      { status: 'CONFIRMED', timestamp: '2024-06-15T09:15:00Z' },
      { status: 'PROCESSING', timestamp: '2024-06-15T14:00:00Z' },
      { status: 'SHIPPED', timestamp: '2024-06-16T10:00:00Z' },
    ],
    createdAt: '2024-06-15T09:00:00Z',
    updatedAt: '2024-06-16T10:00:00Z',
  },
  {
    id: 'order-3',
    orderNumber: 'SS-20240620-003',
    userId: 'user-1',
    status: 'PROCESSING',
    items: [
      {
        id: 'order-item-4',
        productId: 'prod-3',
        productName: 'Running Shoes Pro',
        productImage:
          'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=200',
        quantity: 1,
        unitPrice: 189.99,
        totalPrice: 189.99,
      },
    ],
    shippingAddress: mockAddresses[0],
    subtotal: 189.99,
    shippingAmount: 9.99,
    taxAmount: 16.0,
    discountAmount: 0,
    totalAmount: 215.98,
    paymentStatus: 'PAID',
    statusHistory: [
      { status: 'PENDING', timestamp: '2024-06-20T14:00:00Z' },
      { status: 'CONFIRMED', timestamp: '2024-06-20T14:05:00Z' },
      { status: 'PROCESSING', timestamp: '2024-06-20T16:00:00Z' },
    ],
    createdAt: '2024-06-20T14:00:00Z',
    updatedAt: '2024-06-20T16:00:00Z',
  },
];

export const getOrderSummaries = (): OrderSummary[] =>
  mockOrders.map((o) => ({
    id: o.id,
    orderNumber: o.orderNumber,
    status: o.status,
    itemCount: o.items.length,
    totalAmount: o.totalAmount,
    paymentStatus: o.paymentStatus,
    createdAt: o.createdAt,
  }));
