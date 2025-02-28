/**
 * Review Service API Client
 *
 * Handles product reviews and ratings.
 * Service: review-service (Port 3007)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_REVIEW_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_REVIEW_SERVICE_URL is correct
 */

import { reviewClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  Review,
  ReviewComment,
  RatingSummary,
  FeaturedReview,
  CreateReviewRequest,
  UpdateReviewRequest,
  AddCommentRequest,
  ReviewListParams,
} from '@/types/review';

export const reviewApi = {
  // ==========================================
  // Reviews
  // ==========================================

  /**
   * POST /api/reviews
   * Create a review (for purchased products)
   */
  createReview: (data: CreateReviewRequest) =>
    reviewClient.post<ApiResponse<Review>>('/api/reviews', data),

  /**
   * GET /api/reviews/{reviewId}
   * Get review by ID
   */
  getReview: (reviewId: string) =>
    reviewClient.get<ApiResponse<Review>>(`/api/reviews/${reviewId}`),

  /**
   * PUT /api/reviews/{reviewId}
   * Update own review
   */
  updateReview: (reviewId: string, data: UpdateReviewRequest) =>
    reviewClient.put<ApiResponse<Review>>(`/api/reviews/${reviewId}`, data),

  /**
   * DELETE /api/reviews/{reviewId}
   * Delete own review
   */
  deleteReview: (reviewId: string) =>
    reviewClient.delete<ApiResponse<void>>(`/api/reviews/${reviewId}`),

  /**
   * GET /api/reviews/product/{productId}
   * Get reviews for a product
   */
  getProductReviews: (productId: string, params: ReviewListParams) =>
    reviewClient.get<PaginatedResponse<Review>>(`/api/reviews/product/${productId}`, {
      params,
    }),

  /**
   * GET /api/reviews/user/me
   * Get my reviews
   */
  getMyReviews: (params: ReviewListParams) =>
    reviewClient.get<PaginatedResponse<Review>>('/api/reviews/user/me', { params }),

  // ==========================================
  // Ratings Summary
  // ==========================================

  /**
   * GET /api/reviews/product/{productId}/summary
   * Get rating breakdown
   */
  getRatingSummary: (productId: string) =>
    reviewClient.get<ApiResponse<RatingSummary>>(
      `/api/reviews/product/${productId}/summary`
    ),

  /**
   * GET /api/reviews/product/{productId}/featured
   * Get top helpful reviews
   */
  getFeaturedReviews: (productId: string) =>
    reviewClient.get<ApiResponse<FeaturedReview[]>>(
      `/api/reviews/product/${productId}/featured`
    ),

  // ==========================================
  // Helpfulness
  // ==========================================

  /**
   * POST /api/reviews/{reviewId}/helpful
   * Mark review as helpful
   */
  markHelpful: (reviewId: string) =>
    reviewClient.post<ApiResponse<void>>(`/api/reviews/${reviewId}/helpful`),

  /**
   * DELETE /api/reviews/{reviewId}/helpful
   * Remove helpful mark
   */
  removeHelpful: (reviewId: string) =>
    reviewClient.delete<ApiResponse<void>>(`/api/reviews/${reviewId}/helpful`),

  // ==========================================
  // Comments
  // ==========================================

  /**
   * POST /api/reviews/{reviewId}/comments
   * Add comment to review
   */
  addComment: (reviewId: string, data: AddCommentRequest) =>
    reviewClient.post<ApiResponse<ReviewComment>>(
      `/api/reviews/${reviewId}/comments`,
      data
    ),

  /**
   * DELETE /api/reviews/{reviewId}/comments/{commentId}
   * Delete comment
   */
  deleteComment: (reviewId: string, commentId: string) =>
    reviewClient.delete<ApiResponse<void>>(
      `/api/reviews/${reviewId}/comments/${commentId}`
    ),

};
