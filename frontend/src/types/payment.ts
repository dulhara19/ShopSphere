/**
 * Payment Service Types
 *
 * Types for payment processing.
 * Aligned with backend DTOs in payment-service.
 */

import { ISO8601, UUID } from './api';

// === Payment Processing ===

// Maps to CreatePaymentIntentRequest.java
export interface CreatePaymentIntentRequest {
  orderId: string;
  amount: number;
  currency?: string;
  userId: string;
  paymentMethodId?: string;
  saveCard?: boolean;
  description?: string;
}

// Maps to CreatePaymentIntentResponse.java
export interface PaymentIntent {
  paymentId: string;
  clientSecret: string;
  status: string;
  orderId: string;
  message?: string;
}

// Maps to ConfirmPaymentRequest.java
export interface ConfirmPaymentRequest {
  paymentId: string;
  paymentMethodId?: string;
}

// Maps to PaymentStatusResponse.java
export interface PaymentStatusResponse {
  paymentId: string;
  orderId: string;
  status: string;
  amount: number;
  currency: string;
  paymentMethod: string;
  failureReason?: string;
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// === Payment Methods ===

// Maps to PaymentMethodResponse.java
export interface PaymentMethod {
  id: string;
  cardBrand: string;
  lastFourDigits: string;
  expiryMonth: number;
  expiryYear: number;
  isDefault: boolean;
  createdAt: ISO8601;
}

// Maps to CardPaymentMethodRequest.java
export interface SavePaymentMethodRequest {
  userId: string;
  cardToken: string;
  setAsDefault?: boolean;
}

// === Refunds ===

// Maps to RefundRequest.java
export interface ProcessRefundRequest {
  amount?: number;
  reason: string;
}

// Maps to RefundResponse.java
export interface Refund {
  refundId: string;
  paymentId: string;
  amount: number;
  reason: string;
  status: string;
  failureReason?: string;
  createdAt: ISO8601;
}

// === Transactions ===

// Maps to TransactionResponse.java
export interface Transaction {
  transactionId: string;
  orderId?: string;
  userId?: string;
  amount: number;
  currency?: string;
  type: 'PAYMENT' | 'REFUND';
  status: string;
  description?: string;
  createdAt: ISO8601;
}

export interface TransactionListParams {
  userId: string;
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
}

// Legacy aliases for backward compatibility
export type Payment = PaymentStatusResponse;
export type RefundStatus = string;
export type PaymentIntentStatus = string;
