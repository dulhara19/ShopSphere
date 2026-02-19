/**
 * API Response Types
 *
 * Standard response wrappers for all API calls.
 */

// Pagination metadata
export interface PaginationMeta {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Success response wrapper
export interface ApiResponse<T> {
  success: true;
  data: T;
  meta?: PaginationMeta;
}

// Paginated response
export interface PaginatedResponse<T> {
  success: true;
  data: T[];
  meta: PaginationMeta;
}

// Error response
export interface ApiError {
  success: false;
  error: {
    code: string;
    message: string;
    timestamp: string;
    path: string;
    details?: Record<string, string[]>;
  };
}

// Union type for API responses
export type ApiResult<T> = ApiResponse<T> | ApiError;
export type PaginatedResult<T> = PaginatedResponse<T> | ApiError;

// Request params for pagination
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

// Common sort options
export type SortDirection = 'asc' | 'desc';

export interface SortParam {
  field: string;
  direction: SortDirection;
}

// ID types
export type UUID = string;
export type ISO8601 = string;
