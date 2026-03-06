/**
 * Payment Service API Client
 *
 * Handles payment processing, methods, and refunds.
 * Service: payment-service (Port 3005)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_PAYMENT_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_PAYMENT_SERVICE_URL is correct
 * - Configure NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY
 */

import { paymentClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  PaymentIntent,
  PaymentMethod,
  Payment,
  Refund,
  Transaction,
  CreatePaymentIntentRequest,
  ConfirmPaymentRequest,
  ProcessRefundRequest,
  SavePaymentMethodRequest,
  TransactionListParams,
} from '@/types/payment';

export const paymentApi = {
  // ==========================================
  // Payment Processing
  // ==========================================

  /**
   * POST /api/payments/create-intent
   * Create a Stripe payment intent
   */
  createPaymentIntent: (data: CreatePaymentIntentRequest) =>
    paymentClient.post<ApiResponse<PaymentIntent>>('/api/payments/create-intent', data),

  /**
   * POST /api/payments/{id}/confirm
   * Confirm payment completion
   */
  confirmPayment: (id: string, data?: ConfirmPaymentRequest) =>
    paymentClient.post<ApiResponse<Payment>>(`/api/payments/${id}/confirm`, data),

  /**
   * GET /api/payments/{id}/status
   * Get payment status
   */
  getPaymentStatus: (id: string) =>
    paymentClient.get<ApiResponse<Payment>>(`/api/payments/${id}/status`),

  // ==========================================
  // Payment Methods
  // ==========================================

  /**
   * GET /api/payment-methods
   * List saved payment methods
   */
  getPaymentMethods: () =>
    paymentClient.get<ApiResponse<PaymentMethod[]>>('/api/payment-methods'),

  /**
   * POST /api/payment-methods
   * Save a new payment method
   */
  savePaymentMethod: (data: SavePaymentMethodRequest) =>
    paymentClient.post<ApiResponse<PaymentMethod>>('/api/payment-methods', data),

  /**
   * DELETE /api/payment-methods/{id}
   * Remove a payment method
   */
  deletePaymentMethod: (id: string) =>
    paymentClient.delete<ApiResponse<void>>(`/api/payment-methods/${id}`),

  /**
   * PUT /api/payment-methods/{id}/default
   * Set as default payment method
   */
  setDefaultPaymentMethod: (id: string) =>
    paymentClient.put<ApiResponse<PaymentMethod>>(`/api/payment-methods/${id}/default`),

  // ==========================================
  // Refunds
  // ==========================================

  /**
   * POST /api/payments/{id}/refund
   * Process a refund
   */
  processRefund: (id: string, data: ProcessRefundRequest) =>
    paymentClient.post<ApiResponse<Refund>>(`/api/payments/${id}/refund`, data),

  /**
   * GET /api/payments/{id}/refunds
   * Get all refunds for a payment
   */
  getRefunds: (paymentId: string) =>
    paymentClient.get<ApiResponse<Refund[]>>(`/api/payments/${paymentId}/refunds`),

  /**
   * GET /api/refunds/{refundId}
   * Get refund details
   */
  getRefund: (refundId: string) =>
    paymentClient.get<ApiResponse<Refund>>(`/api/refunds/${refundId}`),

  // ==========================================
  // Transactions
  // ==========================================

  /**
   * GET /api/transactions
   * List transactions
   */
  getTransactions: (params: TransactionListParams) =>
    paymentClient.get<PaginatedResponse<Transaction>>('/api/transactions', { params }),

  /**
   * GET /api/transactions/{id}
   * Get transaction details
   */
  getTransaction: (id: string) =>
    paymentClient.get<ApiResponse<Transaction>>(`/api/transactions/${id}`),
};
