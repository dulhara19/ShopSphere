/**
 * Recommendation Service API Client
 *
 * Handles product recommendations and user tracking.
 * Service: recommendation-service (Port 3008)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_RECOMMENDATION_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_RECOMMENDATION_SERVICE_URL is correct
 */

import { recommendationClient } from './client';
import { ApiResponse } from '@/types/api';
import {
  TrackingEvent,
  BatchTrackingRequest,
  RecommendationSet,
  RecentlyViewedItem,
  SearchSuggestion,
  VisualSearchResponse,
  RecommendationParams,
  TrendingParams,
  HomepageRecommendations,
} from '@/types/recommendation';
import { ProductSummary } from '@/types/product';

export const recommendationApi = {
  // ==========================================
  // User Tracking
  // ==========================================

  /**
   * POST /api/events/track
   * Track user behavior (view, click, purchase)
   */
  trackEvent: (event: TrackingEvent) =>
    recommendationClient.post<ApiResponse<void>>('/api/events/track', event),

  /**
   * POST /api/events/batch
   * Batch track events
   */
  trackEventsBatch: (data: BatchTrackingRequest) =>
    recommendationClient.post<ApiResponse<void>>('/api/events/batch', data),

  // ==========================================
  // Recently Viewed
  // ==========================================

  /**
   * GET /api/recommendations/recently-viewed
   * Get user's recently viewed products
   */
  getRecentlyViewed: (limit = 10) =>
    recommendationClient.get<ApiResponse<RecentlyViewedItem[]>>(
      '/api/recommendations/recently-viewed',
      { params: { limit } }
    ),

  /**
   * DELETE /api/recommendations/recently-viewed
   * Clear recently viewed history
   */
  clearRecentlyViewed: () =>
    recommendationClient.delete<ApiResponse<void>>('/api/recommendations/recently-viewed'),

  // ==========================================
  // Personalized Recommendations
  // ==========================================

  /**
   * GET /api/recommendations/for-you
   * Get personalized recommendations
   */
  getForYou: (params?: RecommendationParams) =>
    recommendationClient.get<ApiResponse<ProductSummary[]>>(
      '/api/recommendations/for-you',
      { params }
    ),

  /**
   * GET /api/recommendations/homepage
   * Get homepage recommendations
   */
  getHomepageRecommendations: () =>
    recommendationClient.get<ApiResponse<HomepageRecommendations>>(
      '/api/recommendations/homepage'
    ),

  // ==========================================
  // Product-Based Recommendations
  // ==========================================

  /**
   * GET /api/recommendations/similar/{productId}
   * Get similar products
   */
  getSimilarProducts: (productId: string, params?: RecommendationParams) =>
    recommendationClient.get<ApiResponse<ProductSummary[]>>(
      `/api/recommendations/similar/${productId}`,
      { params }
    ),

  /**
   * GET /api/recommendations/also-bought/{productId}
   * Get "frequently bought together" products
   */
  getAlsoBought: (productId: string, params?: RecommendationParams) =>
    recommendationClient.get<ApiResponse<ProductSummary[]>>(
      `/api/recommendations/also-bought/${productId}`,
      { params }
    ),

  // ==========================================
  // Trending
  // ==========================================

  /**
   * GET /api/recommendations/trending
   * Get trending products
   */
  getTrending: (params?: TrendingParams) =>
    recommendationClient.get<ApiResponse<ProductSummary[]>>(
      '/api/recommendations/trending',
      { params }
    ),

  /**
   * GET /api/recommendations/trending/category/{categoryId}
   * Get trending products in a category
   */
  getTrendingInCategory: (categoryId: string, params?: TrendingParams) =>
    recommendationClient.get<ApiResponse<ProductSummary[]>>(
      `/api/recommendations/trending/category/${categoryId}`,
      { params }
    ),

  // ==========================================
  // Search
  // ==========================================

  /**
   * GET /api/recommendations/search-suggestions
   * Get search autocomplete suggestions
   */
  getSearchSuggestions: (query: string) =>
    recommendationClient.get<ApiResponse<SearchSuggestion[]>>(
      '/api/recommendations/search-suggestions',
      { params: { query } }
    ),

  /**
   * POST /api/recommendations/visual-search
   * Image-based product search
   */
  visualSearch: (file: File) => {
    const formData = new FormData();
    formData.append('image', file);
    return recommendationClient.post<ApiResponse<VisualSearchResponse>>(
      '/api/recommendations/visual-search',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
  },
};
