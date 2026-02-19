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
  RealTimeMetrics,
  KpiMetrics,
  SalesAnalytics,
  CategorySales,
  RegionSales,
  TopSellingProduct,
  ProductPerformance,
  CustomerOverview,
  CohortData,
  CustomerLTV,
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
   * GET /api/analytics/dashboard
   * Get main dashboard metrics
   */
  getDashboard: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<DashboardMetrics>>('/api/analytics/dashboard', {
      params,
    }),

  /**
   * GET /api/analytics/dashboard/real-time
   * Get real-time live metrics
   */
  getRealTimeMetrics: () =>
    analyticsClient.get<ApiResponse<RealTimeMetrics>>(
      '/api/analytics/dashboard/real-time'
    ),

  /**
   * GET /api/analytics/dashboard/kpis
   * Get key performance indicators
   */
  getKpis: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<KpiMetrics>>('/api/analytics/dashboard/kpis', {
      params,
    }),

  // ==========================================
  // Sales Analytics
  // ==========================================

  /**
   * GET /api/analytics/sales
   * Get sales analytics with granularity
   */
  getSalesAnalytics: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<SalesAnalytics>>('/api/analytics/sales', {
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
   * GET /api/analytics/sales/by-region
   * Get sales by region
   */
  getSalesByRegion: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<RegionSales[]>>('/api/analytics/sales/by-region', {
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
   * GET /api/analytics/products/performance
   * Get product performance metrics
   */
  getProductPerformance: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<ProductPerformance[]>>(
      '/api/analytics/products/performance',
      { params }
    ),

  // ==========================================
  // Customer Analytics
  // ==========================================

  /**
   * GET /api/analytics/customers/overview
   * Get customer overview metrics
   */
  getCustomerOverview: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<CustomerOverview>>(
      '/api/analytics/customers/overview',
      { params }
    ),

  /**
   * GET /api/analytics/customers/cohorts
   * Get cohort analysis
   */
  getCohortAnalysis: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<CohortData[]>>('/api/analytics/customers/cohorts', {
      params,
    }),

  /**
   * GET /api/analytics/customers/lifetime-value
   * Get customer lifetime value analysis
   */
  getCustomerLTV: (params: AnalyticsParams) =>
    analyticsClient.get<ApiResponse<CustomerLTV[]>>(
      '/api/analytics/customers/lifetime-value',
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
