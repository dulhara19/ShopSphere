/**
 * User Service Types
 *
 * Types for authentication, user profiles, and addresses.
 * Based on: shared/contracts/user-service.yaml
 */

import { ISO8601, UUID } from './api';

// User roles
export type UserRole = 'CUSTOMER' | 'SELLER' | 'ADMIN';

// User status
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

// User entity
export interface User {
  id: UUID;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  avatarUrl?: string;
  role: UserRole;
  roles?: UserRole[];
  status: UserStatus;
  emailVerified: boolean;
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// Address entity
export interface Address {
  id: UUID;
  userId: UUID;
  label: string;
  fullName: string;
  phone: string;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
  createdAt: ISO8601;
  updatedAt: ISO8601;
}

// Authentication requests
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

// Authentication response
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  user: User;
}

// Profile update request
export interface UpdateProfileRequest {
  firstName?: string;
  lastName?: string;
  phone?: string;
}

// Change password request
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

// Address requests
export interface CreateAddressRequest {
  label: string;
  fullName: string;
  phone: string;
  line1: string;
  line2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault?: boolean;
}

export interface UpdateAddressRequest extends Partial<CreateAddressRequest> {}

// Admin requests
export interface UpdateUserRoleRequest {
  role: UserRole;
}

export interface UpdateUserStatusRequest {
  status: UserStatus;
}

// User list filters (admin)
export interface UserListParams {
  page?: number;
  size?: number;
  role?: UserRole;
  status?: UserStatus;
  search?: string;
}
