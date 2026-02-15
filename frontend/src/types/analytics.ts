/**
 * Analytics Service Types
 *
 * Types for analytics and reporting.
 * Based on: shared/contracts/analytics-service.yaml
 */

import { ISO8601, UUID } from './api';

// Time granularity
export type TimeGranularity = 'HOUR' | 'DAY' | 'WEEK' | 'MONTH';

// Analytics event (for tracking)
export interface AnalyticsEvent {
  event: string;
  properties: Record<string, unknown>;
  timestamp?: ISO8601;
}

// Common events to track
export type CommonEventType =
  | 'page_view'
  | 'product_viewed'
  | 'product_added_to_cart'
  | 'checkout_started'
  | 'order_completed'
  | 'search_performed';

// Dashboard metrics
export interface DashboardMetrics {
  totalRevenue: number;
  totalOrders: number;
  averageOrderValue: number;
  newCustomers: number;
  returningCustomers: number;
  conversionRate: number;
  period: {
    start: ISO8601;
    end: ISO8601;
  };
}

// Real-time metrics
export interface RealTimeMetrics {
  activeUsers: number;
  ordersToday: number;
  revenueToday: number;
  itemsInCarts: number;
  checkoutsInProgress: number;
  lastUpdated: ISO8601;
}

// KPI metrics
export interface KpiMetrics {
  totalRevenue: {
    value: number;
    change: number;
    trend: 'up' | 'down' | 'stable';
  };
  totalOrders: {
    value: number;
    change: number;
    trend: 'up' | 'down' | 'stable';
  };
  averageOrderValue: {
    value: number;
    change: number;
    trend: 'up' | 'down' | 'stable';
  };
  customerCount: {
    value: number;
    change: number;
    trend: 'up' | 'down' | 'stable';
  };
}

// Sales data point
export interface SalesDataPoint {
  date: ISO8601;
  revenue: number;
  orders: number;
  averageOrderValue: number;
}

// Sales analytics
export interface SalesAnalytics {
  dataPoints: SalesDataPoint[];
  summary: {
    totalRevenue: number;
    totalOrders: number;
    averageOrderValue: number;
  };
  granularity: TimeGranularity;
}

// Category sales
export interface CategorySales {
  categoryId: UUID;
  categoryName: string;
  revenue: number;
  orders: number;
  percentage: number;
}

// Region sales
export interface RegionSales {
  region: string;
  country: string;
  revenue: number;
  orders: number;
  percentage: number;
}

// Top selling product
export interface TopSellingProduct {
  productId: UUID;
  productName: string;
  productImage?: string;
  unitsSold: number;
  revenue: number;
}

// Product performance
export interface ProductPerformance {
  productId: UUID;
  productName: string;
  views: number;
  addedToCart: number;
  purchased: number;
  conversionRate: number;
  revenue: number;
}

// Customer overview
export interface CustomerOverview {
  totalCustomers: number;
  newCustomers: number;
  returningCustomers: number;
  churnRate: number;
  averageLifetimeValue: number;
}

// Cohort data
export interface CohortData {
  cohort: string;
  period: ISO8601;
  customers: number;
  retention: number[];
}

// Customer LTV
export interface CustomerLTV {
  segment: string;
  customerCount: number;
  averageLTV: number;
  totalRevenue: number;
}

// Analytics params
export interface AnalyticsParams {
  startDate: string;
  endDate: string;
  granularity?: TimeGranularity;
}

// Export report request
export interface ExportReportRequest {
  type: 'sales' | 'orders' | 'customers' | 'products';
  format: 'csv' | 'pdf';
  startDate: string;
  endDate: string;
  filters?: Record<string, unknown>;
}

// Export report response
export interface ExportReportResponse {
  reportId: UUID;
  downloadUrl: string;
  expiresAt: ISO8601;
}
