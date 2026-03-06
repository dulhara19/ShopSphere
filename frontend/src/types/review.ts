/**
 * Review Service Types
 *
 * Types for product reviews and ratings.
 * Based on: shared/contracts/review-service.yaml
 */

import { ISO8601, UUID } from './api';

// Review entity
export interface Review {
  id: UUID;
  productId: UUID;
  userId: UUID;
  userName: string;
  userAvatar?: string;
  rating: 1 | 2 | 3 | 4 | 5;
  title: string;
  content: string;
  images?: string[];
  helpfulCount: number;
  unhelpfulCount: number;
  verified: boolean;
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// Review comment
export interface ReviewComment {
  id: UUID;
  reviewId: UUID;
  userId: UUID;
  userName: string;
  content: string;
  createdAt: ISO8601;
}

// Rating distribution
export interface RatingDistribution {
  1: number;
  2: number;
  3: number;
  4: number;
  5: number;
}

// Rating summary
export interface RatingSummary {
  productId: UUID;
  averageRating: number;
  totalReviews: number;
  distribution: RatingDistribution;
  verifiedPurchaseCount: number;
}

// Featured review
export interface FeaturedReview extends Review {
  featured: boolean;
  featuredReason?: 'most_helpful' | 'most_recent' | 'staff_pick';
}

// Create review request
export interface CreateReviewRequest {
  productId: UUID;
  rating: 1 | 2 | 3 | 4 | 5;
  title: string;
  content: string;
  images?: string[];
}

// Update review request
export interface UpdateReviewRequest {
  rating?: 1 | 2 | 3 | 4 | 5;
  title?: string;
  content?: string;
  images?: string[];
}

// Add comment request
export interface AddCommentRequest {
  content: string;
}

// Review list params
export interface ReviewListParams {
  page?: number;
  size?: number;
  sort?: 'newest' | 'oldest' | 'highest_rating' | 'lowest_rating' | 'most_helpful';
  rating?: 1 | 2 | 3 | 4 | 5;
  verifiedOnly?: boolean;
}

// Admin review moderation
export interface ReviewModerationRequest {
  action: 'approve' | 'reject';
  reason?: string;
}
