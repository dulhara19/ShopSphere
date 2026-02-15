/**
 * API Client Base Configuration
 *
 * Axios instance with interceptors for authentication and error handling.
 *
 * AI AGENT NOTE:
 * This file configures the base HTTP client. When integrating real services:
 * 1. Update the service URL in .env.local
 * 2. Set NEXT_PUBLIC_ENABLE_<SERVICE>_SERVICE=true
 * 3. The client will automatically route to real APIs
 */

import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { getServiceUrl, ServiceName } from '@/config/services';
import { ApiError } from '@/types/api';
import {
  ACCESS_TOKEN_KEY,
  REFRESH_TOKEN_KEY,
  TOKEN_REFRESH_THRESHOLD,
} from '@/config/constants';
import { getStorageItem, setStorageItem, removeStorageItem } from '@/lib/utils/storage';

// Token refresh state
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: Error) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token!);
    }
  });
  failedQueue = [];
};

/**
 * Create an axios instance for a specific service
 */
export function createServiceClient(service: ServiceName): AxiosInstance {
  const client = axios.create({
    baseURL: getServiceUrl(service),
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor - add auth token
  client.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = getStorageItem<string | null>(ACCESS_TOKEN_KEY, null);
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response interceptor - handle errors and token refresh
  client.interceptors.response.use(
    (response) => response,
    async (error: AxiosError<ApiError>) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & {
        _retry?: boolean;
      };

      // Handle 401 Unauthorized
      if (error.response?.status === 401 && !originalRequest._retry) {
        if (isRefreshing) {
          // Wait for token refresh
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject });
          })
            .then((token) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              return client(originalRequest);
            })
            .catch((err) => Promise.reject(err));
        }

        originalRequest._retry = true;
        isRefreshing = true;

        const refreshToken = getStorageItem<string | null>(REFRESH_TOKEN_KEY, null);

        if (refreshToken) {
          try {
            // Import userApi dynamically to avoid circular dependency
            const { userApi } = await import('./user');
            const response = await userApi.refreshToken({ refreshToken });

            const { accessToken, refreshToken: newRefreshToken } = response.data;

            setStorageItem(ACCESS_TOKEN_KEY, accessToken);
            setStorageItem(REFRESH_TOKEN_KEY, newRefreshToken);

            processQueue(null, accessToken);

            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            return client(originalRequest);
          } catch (refreshError) {
            processQueue(refreshError as Error, null);
            // Clear tokens and redirect to login
            removeStorageItem(ACCESS_TOKEN_KEY);
            removeStorageItem(REFRESH_TOKEN_KEY);

            if (typeof window !== 'undefined') {
              window.location.href = '/login';
            }
            return Promise.reject(refreshError);
          } finally {
            isRefreshing = false;
          }
        } else {
          // No refresh token, redirect to login
          if (typeof window !== 'undefined') {
            window.location.href = '/login';
          }
        }
      }

      // Transform error response
      const apiError: ApiError = error.response?.data || {
        success: false,
        error: {
          code: 'UNKNOWN_ERROR',
          message: error.message || 'An unexpected error occurred',
          timestamp: new Date().toISOString(),
          path: originalRequest?.url || '',
        },
      };

      return Promise.reject(apiError);
    }
  );

  return client;
}

// Pre-created service clients
export const userClient = createServiceClient('user');
export const productClient = createServiceClient('product');
export const inventoryClient = createServiceClient('inventory');
export const orderClient = createServiceClient('order');
export const paymentClient = createServiceClient('payment');
export const shippingClient = createServiceClient('shipping');
export const reviewClient = createServiceClient('review');
export const recommendationClient = createServiceClient('recommendation');
export const notificationClient = createServiceClient('notification');
export const analyticsClient = createServiceClient('analytics');
