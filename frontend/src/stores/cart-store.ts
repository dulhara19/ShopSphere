/**
 * Cart Store
 *
 * Manages shopping cart state with local persistence and server sync.
 *
 * AI AGENT NOTE:
 * - Cart syncs with order-service when user is authenticated
 * - Guest carts are stored locally and merged on login
 * - Optimistic updates for better UX
 */

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import { Cart, CartItem, CartTotals, AddToCartRequest } from '@/types/cart';
import { orderApi } from '@/lib/api/order';
import { CART_STORAGE_KEY, GUEST_CART_ID_KEY } from '@/config/constants';
import { getStorageItem, setStorageItem } from '@/lib/utils/storage';

interface CartState {
  // State
  cart: Cart | null;
  totals: CartTotals | null;
  isLoading: boolean;
  error: string | null;
  isDrawerOpen: boolean;

  // Actions
  fetchCart: () => Promise<void>;
  addItem: (item: AddToCartRequest) => Promise<void>;
  updateItemQuantity: (itemId: string, quantity: number) => Promise<void>;
  removeItem: (itemId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  fetchTotals: () => Promise<void>;
  applyCoupon: (code: string) => Promise<void>;
  removeCoupon: () => Promise<void>;
  mergeGuestCart: () => Promise<void>;
  setDrawerOpen: (open: boolean) => void;
  toggleDrawer: () => void;

  // Computed
  itemCount: () => number;
  subtotal: () => number;
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      // Initial state
      cart: null,
      totals: null,
      isLoading: false,
      error: null,
      isDrawerOpen: false,

      // Fetch cart from server
      fetchCart: async () => {
        set({ isLoading: true, error: null });
        try {
          const response = await orderApi.getCart();
          set({
            cart: response.data?.data || response.data,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Failed to load cart',
          });
        }
      },

      // Add item to cart
      addItem: async (item: AddToCartRequest) => {
        set({ isLoading: true, error: null });
        try {
          const response = await orderApi.addToCart(item);
          set({
            cart: response.data?.data || response.data,
            isLoading: false,
            isDrawerOpen: true, // Open drawer after adding
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Failed to add item',
          });
          throw error;
        }
      },

      // Update item quantity
      updateItemQuantity: async (itemId: string, quantity: number) => {
        const { cart } = get();
        if (!cart) return;

        // Optimistic update
        const optimisticCart = {
          ...cart,
          items: cart.items.map((item) =>
            item.id === itemId
              ? { ...item, quantity, totalPrice: item.unitPrice * quantity }
              : item
          ),
        };
        set({ cart: optimisticCart });

        try {
          const response = await orderApi.updateCartItem(itemId, { quantity });
          set({ cart: response.data?.data || response.data });
        } catch (error: any) {
          // Revert on error
          set({ cart, error: error.error?.message || 'Failed to update quantity' });
          throw error;
        }
      },

      // Remove item from cart
      removeItem: async (itemId: string) => {
        const { cart } = get();
        if (!cart) return;

        // Optimistic update
        const optimisticCart = {
          ...cart,
          items: cart.items.filter((item) => item.id !== itemId),
          itemCount: cart.itemCount - 1,
        };
        set({ cart: optimisticCart });

        try {
          const response = await orderApi.removeCartItem(itemId);
          set({ cart: response.data?.data || response.data });
        } catch (error: any) {
          // Revert on error
          set({ cart, error: error.error?.message || 'Failed to remove item' });
          throw error;
        }
      },

      // Clear entire cart
      clearCart: async () => {
        set({ isLoading: true });
        try {
          await orderApi.clearCart();
          set({
            cart: null,
            totals: null,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Failed to clear cart',
          });
        }
      },

      // Fetch cart totals (for checkout)
      fetchTotals: async () => {
        try {
          const response = await orderApi.getCartTotals();
          set({ totals: response.data?.data || response.data });
        } catch (error: any) {
          set({ error: error.error?.message || 'Failed to calculate totals' });
        }
      },

      // Apply coupon
      applyCoupon: async (code: string) => {
        set({ isLoading: true, error: null });
        try {
          const response = await orderApi.applyCoupon({ code });
          set({
            cart: response.data?.data || response.data,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Invalid coupon code',
          });
          throw error;
        }
      },

      // Remove coupon
      removeCoupon: async () => {
        set({ isLoading: true });
        try {
          const response = await orderApi.removeCoupon();
          set({
            cart: response.data?.data || response.data,
            isLoading: false,
          });
        } catch (error: any) {
          set({
            isLoading: false,
            error: error.error?.message || 'Failed to remove coupon',
          });
        }
      },

      // Merge guest cart after login
      mergeGuestCart: async () => {
        const guestCartId = getStorageItem<string | null>(GUEST_CART_ID_KEY, null);
        if (!guestCartId) return;

        try {
          const response = await orderApi.mergeCart({ guestCartId });
          set({ cart: response.data?.data || response.data });
        } catch (error) {
          // Silently fail merge
        }
      },

      // Drawer controls
      setDrawerOpen: (open) => set({ isDrawerOpen: open }),
      toggleDrawer: () => set((state) => ({ isDrawerOpen: !state.isDrawerOpen })),

      // Computed values
      itemCount: () => get().cart?.itemCount || 0,
      subtotal: () => get().cart?.subtotal || 0,
    }),
    {
      name: CART_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        cart: state.cart,
      }),
    }
  )
);

// Selector hooks
export const useCart = () => useCartStore((state) => state.cart);
export const useCartItems = () => useCartStore((state) => state.cart?.items || []);
export const useCartItemCount = () => useCartStore((state) => state.cart?.itemCount || 0);
export const useCartSubtotal = () => useCartStore((state) => state.cart?.subtotal || 0);
export const useCartLoading = () => useCartStore((state) => state.isLoading);
export const useCartDrawer = () => useCartStore((state) => state.isDrawerOpen);
