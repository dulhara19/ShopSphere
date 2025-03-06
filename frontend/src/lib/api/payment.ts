/**
 * Payment Service API Client
 *
 * Handles payment processing, methods, and refunds.
 * Service: payment-service (Port 3005)
 *
 * Aligned with backend controllers:
 * - PaymentController (/api/payments)
 * - PaymentMethodController (/api/payment-methods)
 * - RefundController (/api/payments/{id}/refund)
 * - TransactionController (/api/transactions)
 */

import { paymentClient } from './client';
import {
  PaymentIntent,
  PaymentMethod,
  PaymentStatusResponse,
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
  // Payment Processing (PaymentController)
  // ==========================================

  /** POST /api/payments/create-intent */
  createPaymentIntent: (data: CreatePaymentIntentRequest) =>
    paymentClient.post<PaymentIntent>('/api/payments/create-intent', data),

  /** POST /api/payments/{id}/confirm */
  confirmPayment: (id: string, data: ConfirmPaymentRequest) =>
    paymentClient.post<PaymentStatusResponse>(`/api/payments/${id}/confirm`, data),

  /** GET /api/payments/{id}/status */
  getPaymentStatus: (id: string) =>
    paymentClient.get<PaymentStatusResponse>(`/api/payments/${id}/status`),

  /** POST /api/payments/{id}/retry */
  retryPayment: (id: string, data: ConfirmPaymentRequest) =>
    paymentClient.post<PaymentStatusResponse>(`/api/payments/${id}/retry`, data),

  // ==========================================
  // Payment Methods (PaymentMethodController)
  // ==========================================

  /** GET /api/payment-methods?userId={userId} */
  getPaymentMethods: (userId: string) =>
    paymentClient.get<PaymentMethod[]>('/api/payment-methods', {
      params: { userId },
    }),

  /** POST /api/payment-methods?userId={userId} */
  savePaymentMethod: (data: SavePaymentMethodRequest) =>
    paymentClient.post<PaymentMethod>('/api/payment-methods', data, {
      params: { userId: data.userId },
    }),

  /** DELETE /api/payment-methods/{id} */
  deletePaymentMethod: (id: string) =>
    paymentClient.delete<void>(`/api/payment-methods/${id}`),

  /** PUT /api/payment-methods/{id}/default?userId={userId} */
  setDefaultPaymentMethod: (userId: string, id: string) =>
    paymentClient.put<PaymentMethod>(`/api/payment-methods/${id}/default`, null, {
      params: { userId },
    }),

  // ==========================================
  // Refunds (RefundController)
  // ==========================================

  /** POST /api/payments/{id}/refund */
  processRefund: (paymentId: string, data: ProcessRefundRequest) =>
    paymentClient.post<Refund>(`/api/payments/${paymentId}/refund`, data),

  /** GET /api/payments/{id}/refunds */
  getRefunds: (paymentId: string, page = 0, size = 10) =>
    paymentClient.get(`/api/payments/${paymentId}/refunds`, {
      params: { page, size },
    }),

  /** GET /api/payments/refunds/{refundId} */
  getRefund: (refundId: string) =>
    paymentClient.get<Refund>(`/api/payments/refunds/${refundId}`),

  // ==========================================
  // Transactions (TransactionController)
  // ==========================================

  /** GET /api/transactions?userId={userId} */
  getTransactions: (params: TransactionListParams) =>
    paymentClient.get('/api/transactions', { params }),

  /** GET /api/transactions/{id} */
  getTransaction: (id: string) =>
    paymentClient.get<Transaction>(`/api/transactions/${id}`),
};
