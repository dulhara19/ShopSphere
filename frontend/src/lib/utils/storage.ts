/**
 * Storage Utilities
 *
 * Safe localStorage/sessionStorage operations with JSON parsing
 */

/**
 * Get item from localStorage with JSON parsing
 */
export function getStorageItem<T>(key: string, defaultValue: T): T {
  if (typeof window === 'undefined') return defaultValue;

  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch {
    return defaultValue;
  }
}

/**
 * Set item in localStorage with JSON stringification
 */
export function setStorageItem<T>(key: string, value: T): void {
  if (typeof window === 'undefined') return;

  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error(`Error saving to localStorage: ${key}`, error);
  }
}

/**
 * Remove item from localStorage
 */
export function removeStorageItem(key: string): void {
  if (typeof window === 'undefined') return;

  try {
    localStorage.removeItem(key);
  } catch (error) {
    console.error(`Error removing from localStorage: ${key}`, error);
  }
}

/**
 * Clear all app-specific items from localStorage
 */
export function clearAppStorage(): void {
  if (typeof window === 'undefined') return;

  const appKeys = [
    'shopsphere_access_token',
    'shopsphere_refresh_token',
    'shopsphere_user',
    'shopsphere_cart',
    'shopsphere_guest_cart_id',
  ];

  appKeys.forEach((key) => {
    try {
      localStorage.removeItem(key);
    } catch (error) {
      console.error(`Error clearing localStorage: ${key}`, error);
    }
  });
}

/**
 * Get item from sessionStorage
 */
export function getSessionItem<T>(key: string, defaultValue: T): T {
  if (typeof window === 'undefined') return defaultValue;

  try {
    const item = sessionStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch {
    return defaultValue;
  }
}

/**
 * Set item in sessionStorage
 */
export function setSessionItem<T>(key: string, value: T): void {
  if (typeof window === 'undefined') return;

  try {
    sessionStorage.setItem(key, JSON.stringify(value));
  } catch (error) {
    console.error(`Error saving to sessionStorage: ${key}`, error);
  }
}

/**
 * Check if localStorage is available
 */
export function isStorageAvailable(): boolean {
  if (typeof window === 'undefined') return false;

  try {
    const testKey = '__storage_test__';
    localStorage.setItem(testKey, testKey);
    localStorage.removeItem(testKey);
    return true;
  } catch {
    return false;
  }
}
