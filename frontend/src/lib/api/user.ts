/**
 * User Service API Client
 *
 * Handles authentication, user profiles, and addresses.
 * Service: user-service (Port 3001)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_USER_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_USER_SERVICE_URL is correct
 */

import { userClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  User,
  Address,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RefreshTokenRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  UpdateProfileRequest,
  ChangePasswordRequest,
  CreateAddressRequest,
  UpdateAddressRequest,
  UpdateUserRoleRequest,
  UpdateUserStatusRequest,
  UserListParams,
} from '@/types/user';

export const userApi = {
  // ==========================================
  // Authentication
  // ==========================================

  /**
   * POST /api/auth/login
   * Login with email and password
   */
  login: (data: LoginRequest) =>
    userClient.post<ApiResponse<AuthResponse>>('/api/auth/login', data),

  /**
   * POST /api/auth/register
   * Register a new user account
   */
  register: (data: RegisterRequest) =>
    userClient.post<ApiResponse<AuthResponse>>('/api/auth/register', data),

  /**
   * POST /api/auth/refresh
   * Refresh access token using refresh token
   */
  refreshToken: (data: RefreshTokenRequest) =>
    userClient.post<ApiResponse<AuthResponse>>('/api/auth/refresh', data),

  /**
   * POST /api/auth/logout
   * Logout and invalidate tokens
   */
  logout: () => userClient.post<ApiResponse<void>>('/api/auth/logout'),

  /**
   * POST /api/auth/forgot-password
   * Request password reset email
   */
  forgotPassword: (data: ForgotPasswordRequest) =>
    userClient.post<ApiResponse<{ message: string }>>('/api/auth/forgot-password', data),

  /**
   * POST /api/auth/reset-password
   * Reset password with token
   */
  resetPassword: (data: ResetPasswordRequest) =>
    userClient.post<ApiResponse<void>>('/api/auth/reset-password', data),

  // ==========================================
  // User Profile
  // ==========================================

  /**
   * GET /api/users/me
   * Get current user's profile
   */
  getProfile: () => userClient.get<ApiResponse<User>>('/api/users/me'),

  /**
   * GET /api/users/{id}
   * Get user by ID
   */
  getUserById: (id: string) =>
    userClient.get<ApiResponse<User>>(`/api/users/${id}`),

  /**
   * PUT /api/users/{id}
   * Update user profile
   */
  updateProfile: (id: string, data: UpdateProfileRequest) =>
    userClient.put<ApiResponse<User>>(`/api/users/${id}`, data),

  /**
   * PUT /api/users/me/password
   * Change password
   */
  changePassword: (data: ChangePasswordRequest) =>
    userClient.put<ApiResponse<void>>('/api/users/me/password', data),

  /**
   * POST /api/users/me/avatar
   * Upload avatar image
   */
  uploadAvatar: (file: File) => {
    const formData = new FormData();
    formData.append('avatar', file);
    return userClient.post<ApiResponse<{ avatarUrl: string }>>(
      '/api/users/me/avatar',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
  },

  /**
   * DELETE /api/users/{id}
   * Soft delete user account
   */
  deleteAccount: (id: string) =>
    userClient.delete<ApiResponse<void>>(`/api/users/${id}`),

  // ==========================================
  // Addresses
  // ==========================================

  /**
   * GET /api/users/me/addresses
   * Get all addresses for current user
   */
  getAddresses: () =>
    userClient.get<ApiResponse<Address[]>>('/api/users/me/addresses'),

  /**
   * POST /api/users/me/addresses
   * Create a new address
   */
  createAddress: (data: CreateAddressRequest) =>
    userClient.post<ApiResponse<Address>>('/api/users/me/addresses', data),

  /**
   * PUT /api/users/me/addresses/{id}
   * Update an address
   */
  updateAddress: (id: string, data: UpdateAddressRequest) =>
    userClient.put<ApiResponse<Address>>(`/api/users/me/addresses/${id}`, data),

  /**
   * DELETE /api/users/me/addresses/{id}
   * Delete an address
   */
  deleteAddress: (id: string) =>
    userClient.delete<ApiResponse<void>>(`/api/users/me/addresses/${id}`),

  /**
   * PUT /api/users/me/addresses/{id}/default
   * Set address as default
   */
  setDefaultAddress: (id: string) =>
    userClient.put<ApiResponse<Address>>(`/api/users/me/addresses/${id}/default`),

  // ==========================================
  // Admin Endpoints
  // ==========================================

  /**
   * GET /api/admin/users
   * List all users (admin only)
   */
  listUsers: (params: UserListParams) =>
    userClient.get<PaginatedResponse<User>>('/api/admin/users', { params }),

  /**
   * PUT /api/admin/users/{id}/role
   * Update user role (admin only)
   */
  updateUserRole: (id: string, data: UpdateUserRoleRequest) =>
    userClient.put<ApiResponse<User>>(`/api/admin/users/${id}/role`, data),

  /**
   * PUT /api/admin/users/{id}/status
   * Update user status (admin only)
   */
  updateUserStatus: (id: string, data: UpdateUserStatusRequest) =>
    userClient.put<ApiResponse<User>>(`/api/admin/users/${id}/status`, data),
};
