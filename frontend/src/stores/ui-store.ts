/**
 * UI Store
 *
 * Manages global UI state including modals, drawers, and notifications.
 *
 * AI AGENT NOTE:
 * - Controls mobile navigation, search modal, and other UI elements
 * - Theme preference is stored in localStorage
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

type Theme = 'light' | 'dark' | 'system';

interface UIState {
  // Mobile Navigation
  isMobileNavOpen: boolean;
  setMobileNavOpen: (open: boolean) => void;
  toggleMobileNav: () => void;

  // Search Modal
  isSearchOpen: boolean;
  setSearchOpen: (open: boolean) => void;
  toggleSearch: () => void;

  // Auth Modal
  isAuthModalOpen: boolean;
  authModalView: 'login' | 'register' | 'forgot-password';
  openAuthModal: (view?: 'login' | 'register' | 'forgot-password') => void;
  closeAuthModal: () => void;
  setAuthModalView: (view: 'login' | 'register' | 'forgot-password') => void;

  // Theme
  theme: Theme;
  setTheme: (theme: Theme) => void;

  // Sidebar (Admin/Seller)
  isSidebarCollapsed: boolean;
  setSidebarCollapsed: (collapsed: boolean) => void;
  toggleSidebar: () => void;

  // Loading overlay
  isGlobalLoading: boolean;
  globalLoadingMessage: string | null;
  showGlobalLoading: (message?: string) => void;
  hideGlobalLoading: () => void;

  // Notifications badge
  unreadNotificationCount: number;
  setUnreadNotificationCount: (count: number) => void;
  incrementNotificationCount: () => void;
  clearNotificationCount: () => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      // Mobile Navigation
      isMobileNavOpen: false,
      setMobileNavOpen: (open) => set({ isMobileNavOpen: open }),
      toggleMobileNav: () =>
        set((state) => ({ isMobileNavOpen: !state.isMobileNavOpen })),

      // Search Modal
      isSearchOpen: false,
      setSearchOpen: (open) => set({ isSearchOpen: open }),
      toggleSearch: () => set((state) => ({ isSearchOpen: !state.isSearchOpen })),

      // Auth Modal
      isAuthModalOpen: false,
      authModalView: 'login',
      openAuthModal: (view = 'login') =>
        set({ isAuthModalOpen: true, authModalView: view }),
      closeAuthModal: () => set({ isAuthModalOpen: false }),
      setAuthModalView: (view) => set({ authModalView: view }),

      // Theme
      theme: 'system',
      setTheme: (theme) => {
        set({ theme });
        // Apply theme to document
        if (typeof window !== 'undefined') {
          const root = document.documentElement;
          root.classList.remove('light', 'dark');
          if (theme === 'system') {
            const systemTheme = window.matchMedia('(prefers-color-scheme: dark)')
              .matches
              ? 'dark'
              : 'light';
            root.classList.add(systemTheme);
          } else {
            root.classList.add(theme);
          }
        }
      },

      // Sidebar
      isSidebarCollapsed: false,
      setSidebarCollapsed: (collapsed) => set({ isSidebarCollapsed: collapsed }),
      toggleSidebar: () =>
        set((state) => ({ isSidebarCollapsed: !state.isSidebarCollapsed })),

      // Global Loading
      isGlobalLoading: false,
      globalLoadingMessage: null,
      showGlobalLoading: (message) =>
        set({ isGlobalLoading: true, globalLoadingMessage: message || null }),
      hideGlobalLoading: () =>
        set({ isGlobalLoading: false, globalLoadingMessage: null }),

      // Notifications
      unreadNotificationCount: 0,
      setUnreadNotificationCount: (count) => set({ unreadNotificationCount: count }),
      incrementNotificationCount: () =>
        set((state) => ({
          unreadNotificationCount: state.unreadNotificationCount + 1,
        })),
      clearNotificationCount: () => set({ unreadNotificationCount: 0 }),
    }),
    {
      name: 'shopsphere_ui',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        theme: state.theme,
        isSidebarCollapsed: state.isSidebarCollapsed,
      }),
    }
  )
);

// Selector hooks
export const useTheme = () => useUIStore((state) => state.theme);
export const useMobileNav = () => useUIStore((state) => state.isMobileNavOpen);
export const useSearchModal = () => useUIStore((state) => state.isSearchOpen);
export const useAuthModal = () =>
  useUIStore((state) => ({
    isOpen: state.isAuthModalOpen,
    view: state.authModalView,
  }));
export const useSidebar = () => useUIStore((state) => state.isSidebarCollapsed);
export const useGlobalLoading = () =>
  useUIStore((state) => ({
    isLoading: state.isGlobalLoading,
    message: state.globalLoadingMessage,
  }));
export const useUnreadNotifications = () =>
  useUIStore((state) => state.unreadNotificationCount);
