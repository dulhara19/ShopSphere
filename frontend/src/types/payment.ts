/**
 * Payment Service Types
 *
 * Types for payment processing.
 * Based on: shared/contracts/payment-service.yaml
 */

import { ISO8601, UUID } from './api';

// Payment status
export type PaymentIntentStatus = 'pending' | 'processing' | 'succeeded' | 'failed';

// Refund status
export type RefundStatus = 'INITIATED' | 'COMPLETED' | 'FAILED';

// Payment intent
export interface PaymentIntent {
  id: string;
  clientSecret: string;
  amount: number;
  currency: string;
  status: PaymentIntentStatus;
  orderId: UUID;
}

// Payment method
export interface PaymentMethod {
  id: string;
  type: 'card' | 'paypal';
  card?: {
    brand: string;
    last4: string;
    expiryMonth: number;
    expiryYear: number;
  };
  isDefault: boolean;
  createdAt: ISO8601;
}

// Payment record
export interface Payment {
  id: UUID;
  orderId: UUID;
  amount: number;
  currency: string;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  paymentMethod: string;
  transactionId: string;
  createdAt: ISO8601;
}

// Refund record
export interface Refund {
  id: UUID;
  paymentId: UUID;
  amount: number;
  reason: string;
  status: RefundStatus;
  createdAt: ISO8601;
}

// Transaction record
export interface Transaction {
  id: UUID;
  type: 'PAYMENT' | 'REFUND';
  amount: number;
  currency: string;
  status: string;
  orderId: UUID;
  orderNumber: string;
  createdAt: ISO8601;
}

// Create payment intent request
export interface CreatePaymentIntentRequest {
  orderId: UUID;
  amount: number;
  currency?: string;
  paymentMethodId?: string;
}

// Confirm payment request
export interface ConfirmPaymentRequest {
  paymentIntentId: string;
}

// Process refund request
export interface ProcessRefundRequest {
  paymentId: UUID;
  amount?: number;
  reason: string;
}

// Save payment method request
export interface SavePaymentMethodRequest {
  paymentMethodId: string;
  setAsDefault?: boolean;
}

// Transaction list params
export interface TransactionListParams {
  page?: number;
  size?: number;
  type?: 'PAYMENT' | 'REFUND';
  startDate?: string;
  endDate?: string;
}
