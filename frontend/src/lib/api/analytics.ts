/**
 * Analytics Service API Client
 *
 * Handles analytics events and reporting.
 * Service: analytics-service (Port 3010)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_ANALYTICS_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_ANALYTICS_SERVICE_URL is correct
 */

import { analyticsClient } from './client';
import { ApiResponse } from '@/types/api';
import {
  AnalyticsEvent,
  DashboardMetrics,
  SalesAnalytics,
  CategorySales,
  TopSellingProduct,
  ProductPerformance,
  AnalyticsParams,
  ExportReportRequest,
  ExportReportResponse,
} from '@/types/analytics';

export const analyticsApi = {
  // ==========================================
  // Event Tracking
  // ==========================================

  /**
   * POST /api/analytics/events
   * Track a user event
   */
  trackEvent: (event: AnalyticsEvent) =>
    analyticsClient.post<ApiResponse<void>>('/api/analytics/events', event),

  // ==========================================
  // Dashboard
  // ==========================================

  /**
   * GET /api/analytics/dashboard/admin
   * Get admin dashboard metrics
   */
  getDashboard: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<DashboardMetrics>>('/api/analytics/dashboard/admin', {
      params,
    }),

  /**
   * GET /api/analytics/dashboard/seller
   * Get seller dashboard metrics
   */
  getSellerDashboard: (sellerId: string, params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<DashboardMetrics>>(
      '/api/analytics/dashboard/seller',
      { params: { ...params, sellerId } }
    ),

  // ==========================================
  // Sales Analytics
  // ==========================================

  /**
   * GET /api/analytics/sales/by-date
   * Get sales analytics by date with granularity
   */
  getSalesAnalytics: (params: AnalyticsParams & { granularity?: string }) =>
    analyticsClient.get<ApiResponse<SalesAnalytics>>('/api/analytics/sales/by-date', {
      params,
    }),

  /**
   * GET /api/analytics/sales/summary
   * Get sales summary
   */
  getSalesSummary: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<SalesAnalytics['summary']>>(
      '/api/analytics/sales/summary',
      { params }
    ),

  /**
   * GET /api/analytics/sales/by-category
   * Get sales by category
   */
  getSalesByCategory: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<CategorySales[]>>(
      '/api/analytics/sales/by-category',
      { params }
    ),

  /**
   * GET /api/analytics/sales/funnel
   * Get conversion funnel data
   */
  getConversionFunnel: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<unknown>>('/api/analytics/sales/funnel', {
      params,
    }),

  // ==========================================
  // Product Analytics
  // ==========================================

  /**
   * GET /api/analytics/products/top-selling
   * Get top selling products
   */
  getTopSellingProducts: (params: AnalyticsParams & { limit?: number }) =>
    analyticsClient.get<ApiResponse<TopSellingProduct[]>>(
      '/api/analytics/products/top-selling',
      { params }
    ),

  /**
   * GET /api/analytics/products/{productId}/performance
   * Get product performance metrics
   */
  getProductPerformance: (productId: string, params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<ProductPerformance>>(
      `/api/analytics/products/${productId}/performance`,
      { params }
    ),

  // ==========================================
  // Reports
  // ==========================================

  /**
   * POST /api/analytics/reports/export
   * Export report
   */
  exportReport: (data: ExportReportRequest) =>
    analyticsClient.post<ApiResponse<ExportReportResponse>>(
      '/api/analytics/reports/export',
      data
    ),
};
