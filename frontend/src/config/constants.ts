/**
 * Application Constants
 */

// Pagination
export const DEFAULT_PAGE_SIZE = 12;
export const MAX_PAGE_SIZE = 100;

// Cart
export const MAX_CART_ITEM_QUANTITY = 99;
export const CART_STORAGE_KEY = 'shopsphere_cart';
export const GUEST_CART_ID_KEY = 'shopsphere_guest_cart_id';

// Auth
export const ACCESS_TOKEN_KEY = 'shopsphere_access_token';
export const REFRESH_TOKEN_KEY = 'shopsphere_refresh_token';
export const USER_STORAGE_KEY = 'shopsphere_user';
export const TOKEN_REFRESH_THRESHOLD = 60 * 1000; // 1 minute before expiry

// Stock Reservation
export const RESERVATION_TIMEOUT_MINUTES = 15;

// Image
export const PRODUCT_IMAGE_PLACEHOLDER = '/images/placeholder-product.svg';
export const USER_AVATAR_PLACEHOLDER = '/images/placeholder-avatar.svg';
export const MAX_PRODUCT_IMAGES = 10;

// Search
export const SEARCH_DEBOUNCE_MS = 300;
export const MIN_SEARCH_LENGTH = 2;

// Toast
export const TOAST_DURATION = 5000;

// Order Statuses
export const ORDER_STATUSES = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  PROCESSING: 'PROCESSING',
  SHIPPED: 'SHIPPED',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
} as const;

export const ORDER_STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pending',
  CONFIRMED: 'Confirmed',
  PROCESSING: 'Processing',
  SHIPPED: 'Shipped',
  DELIVERED: 'Delivered',
  CANCELLED: 'Cancelled',
};

export const ORDER_STATUS_COLORS: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  PROCESSING: 'bg-purple-100 text-purple-800',
  SHIPPED: 'bg-indigo-100 text-indigo-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
};

// Payment Statuses
export const PAYMENT_STATUSES = {
  PENDING: 'PENDING',
  PAID: 'PAID',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED',
} as const;

// User Roles
export const USER_ROLES = {
  CUSTOMER: 'CUSTOMER',
  SELLER: 'SELLER',
  ADMIN: 'ADMIN',
} as const;

// Sort Options
export const PRODUCT_SORT_OPTIONS = [
  { value: 'newest', label: 'Newest' },
  { value: 'price_asc', label: 'Price: Low to High' },
  { value: 'price_desc', label: 'Price: High to Low' },
  { value: 'name_asc', label: 'Name: A to Z' },
  { value: 'name_desc', label: 'Name: Z to A' },
  { value: 'rating', label: 'Highest Rated' },
] as const;

// Price Range
export const PRICE_RANGES = [
  { min: 0, max: 25, label: 'Under $25' },
  { min: 25, max: 50, label: '$25 - $50' },
  { min: 50, max: 100, label: '$50 - $100' },
  { min: 100, max: 200, label: '$100 - $200' },
  { min: 200, max: null, label: 'Over $200' },
] as const;

// Rating Options
export const RATING_OPTIONS = [
  { value: 4, label: '4 stars & up' },
  { value: 3, label: '3 stars & up' },
  { value: 2, label: '2 stars & up' },
  { value: 1, label: '1 star & up' },
] as const;

// Countries (for shipping)
export const COUNTRIES = [
  { code: 'US', name: 'United States' },
  { code: 'CA', name: 'Canada' },
  { code: 'GB', name: 'United Kingdom' },
  { code: 'AU', name: 'Australia' },
  { code: 'DE', name: 'Germany' },
  { code: 'FR', name: 'France' },
  { code: 'JP', name: 'Japan' },
] as const;

// Currency
export const DEFAULT_CURRENCY = 'USD';
export const CURRENCY_SYMBOL = '$';
