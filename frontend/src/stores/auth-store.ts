/**
 * Auth Store
 *
 * Manages authentication state including user, tokens, and login status.
 *
 * AI AGENT NOTE:
 * - Tokens are stored in localStorage for persistence
 * - User data is synced with the user service
 * - Auto-refresh is handled by the API client
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { User, AuthResponse, LoginRequest, RegisterRequest } from '@/types/user';
import { userApi } from '@/lib/api/user';
import {
  ACCESS_TOKEN_KEY,
  REFRESH_TOKEN_KEY,
  USER_STORAGE_KEY,
} from '@/config/constants';
import {
  setStorageItem,
  removeStorageItem,
  getStorageItem,
} from '@/lib/utils/storage';

interface AuthState {
  // State
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;
  initialize: () => Promise<void>;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      // Initial state
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Login action
      login: async (credentials: LoginRequest) => {
        set({ isLoading: true, error: null });
        try {
          const response = await userApi.login(credentials);
          const { accessToken, refreshToken, user } = response.data.data;

          // Store tokens
          setStorageItem(ACCESS_TOKEN_KEY, accessToken);
          setStorageItem(REFRESH_TOKEN_KEY, refreshToken);

          set({
            user,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Login failed',
          });
          throw error;
        }
      },

      // Register action
      register: async (data: RegisterRequest) => {
        set({ isLoading: true, error: null });
        try {
          const response = await userApi.register(data);
          const { accessToken, refreshToken, user } = response.data.data;

          // Store tokens
          setStorageItem(ACCESS_TOKEN_KEY, accessToken);
          setStorageItem(REFRESH_TOKEN_KEY, refreshToken);

          set({
            user,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Registration failed',
          });
          throw error;
        }
      },

      // Logout action
      logout: async () => {
        set({ isLoading: true });
        try {
          await userApi.logout();
        } catch (error) {
          // Ignore logout errors
        } finally {
          // Clear tokens
          removeStorageItem(ACCESS_TOKEN_KEY);
          removeStorageItem(REFRESH_TOKEN_KEY);

          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
            error: null,
          });
        }
      },

      // Refresh user data
      refreshUser: async () => {
        const token = getStorageItem<string | null>(ACCESS_TOKEN_KEY, null);
        if (!token) {
          set({ user: null, isAuthenticated: false });
          return;
        }

        set({ isLoading: true });
        try {
          const response = await userApi.getProfile();
          set({
            user: response.data.data,
            isAuthenticated: true,
            isLoading: false,
          });
        } catch (error) {
          // Token is invalid
          removeStorageItem(ACCESS_TOKEN_KEY);
          removeStorageItem(REFRESH_TOKEN_KEY);
          set({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
        }
      },

      // Direct setters
      setUser: (user) => set({ user, isAuthenticated: !!user }),
      setLoading: (isLoading) => set({ isLoading }),
      setError: (error) => set({ error }),
      clearError: () => set({ error: null }),

      // Initialize auth state from stored tokens
      initialize: async () => {
        const token = getStorageItem<string | null>(ACCESS_TOKEN_KEY, null);
        if (token) {
          await get().refreshUser();
        }
      },
    }),
    {
      name: USER_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);

// Selector hooks for performance
export const useUser = () => useAuthStore((state) => state.user);
export const useIsAuthenticated = () => useAuthStore((state) => state.isAuthenticated);
export const useAuthLoading = () => useAuthStore((state) => state.isLoading);
export const useAuthError = () => useAuthStore((state) => state.error);
