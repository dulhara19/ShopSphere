/**
 * Recommendation Service Types
 *
 * Types for product recommendations and user tracking.
 * Based on: shared/contracts/recommendation-service.yaml
 */

import { ISO8601, UUID } from './api';
import { ProductSummary } from './product';

// Event type for tracking
export type TrackingEventType =
  | 'view'
  | 'click'
  | 'add_to_cart'
  | 'purchase'
  | 'search'
  | 'wishlist';

// Recommendation type
export type RecommendationType =
  | 'personalized'
  | 'similar'
  | 'trending'
  | 'bought_together'
  | 'recently_viewed';

// Tracking event
export interface TrackingEvent {
  eventType: TrackingEventType;
  productId?: UUID;
  categoryId?: UUID;
  searchQuery?: string;
  metadata?: Record<string, unknown>;
  timestamp?: ISO8601;
}

// Batch tracking events
export interface BatchTrackingRequest {
  events: TrackingEvent[];
}

// Recommendation set
export interface RecommendationSet {
  type: RecommendationType;
  title: string;
  products: ProductSummary[];
}

// Recently viewed item
export interface RecentlyViewedItem {
  productId: UUID;
  product: ProductSummary;
  viewedAt: ISO8601;
}

// Search suggestion
export interface SearchSuggestion {
  query: string;
  category?: string;
  popularity: number;
}

// Visual search request
export interface VisualSearchRequest {
  image: File | string; // File or base64
}

// Visual search response
export interface VisualSearchResponse {
  products: ProductSummary[];
  confidence: number;
  detectedLabels?: string[];
}

// Recommendation params
export interface RecommendationParams {
  limit?: number;
  categoryId?: UUID;
  excludeProductIds?: UUID[];
}

// Trending params
export interface TrendingParams {
  limit?: number;
  categoryId?: UUID;
  period?: 'day' | 'week' | 'month';
}

// Homepage recommendations
export interface HomepageRecommendations {
  trending: ProductSummary[];
  personalized?: ProductSummary[];
  newArrivals: ProductSummary[];
  bestSellers: ProductSummary[];
}
