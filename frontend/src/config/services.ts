/**
 * Service Configuration
 *
 * Controls which services use real APIs vs mocks.
 *
 * AI AGENT INTEGRATION GUIDE:
 * ===========================
 * When a backend service becomes ready:
 * 1. Set the corresponding flag to `true` in serviceStatus
 * 2. Ensure the service URL is correct in .env.local
 * 3. Test the integration
 * 4. Update FRONTEND-CONFIG.md documentation
 *
 * Example: When user-service is deployed:
 * - Set: serviceStatus.user = true
 * - Set: NEXT_PUBLIC_USER_SERVICE_URL=http://localhost:3001
 */

import { env, ServiceName } from './env';

// ===========================================
// SERVICE STATUS FLAGS
// ===========================================
// false = use MSW mock handlers
// true = use real backend service
export const serviceStatus: Record<ServiceName, boolean> = {
  user: process.env.NEXT_PUBLIC_ENABLE_USER_SERVICE === 'true',
  product: process.env.NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE === 'true',
  inventory: process.env.NEXT_PUBLIC_ENABLE_INVENTORY_SERVICE === 'true',
  order: process.env.NEXT_PUBLIC_ENABLE_ORDER_SERVICE === 'true',
  payment: process.env.NEXT_PUBLIC_ENABLE_PAYMENT_SERVICE === 'true',
  shipping: process.env.NEXT_PUBLIC_ENABLE_SHIPPING_SERVICE === 'true',
  review: process.env.NEXT_PUBLIC_ENABLE_REVIEW_SERVICE === 'true',
  recommendation: process.env.NEXT_PUBLIC_ENABLE_RECOMMENDATION_SERVICE === 'true',
  notification: process.env.NEXT_PUBLIC_ENABLE_NOTIFICATION_SERVICE === 'true',
  analytics: process.env.NEXT_PUBLIC_ENABLE_ANALYTICS_SERVICE === 'true',
};

// ===========================================
// SERVICE PORTS (for reference)
// ===========================================
export const servicePorts: Record<ServiceName, number> = {
  user: 3001,
  product: 3002,
  inventory: 3003,
  order: 3004,
  payment: 3005,
  shipping: 3006,
  review: 3007,
  recommendation: 3008,
  notification: 3009,
  analytics: 3010,
};

// ===========================================
// HELPER FUNCTIONS
// ===========================================

/**
 * Get the base URL for a service
 * Returns empty string if using mocks (MSW will intercept)
 */
export function getServiceUrl(service: ServiceName): string {
  // If using gateway (staging/production)
  if (env.apiGatewayUrl) {
    return env.apiGatewayUrl;
  }

  // If service is enabled, return its URL
  if (serviceStatus[service]) {
    return env.services[service];
  }

  // Return empty string - MSW will intercept the request
  return '';
}

/**
 * Check if a service is using real API
 */
export function isServiceEnabled(service: ServiceName): boolean {
  return serviceStatus[service];
}

/**
 * Get all enabled services
 */
export function getEnabledServices(): ServiceName[] {
  return (Object.keys(serviceStatus) as ServiceName[]).filter(
    (service) => serviceStatus[service]
  );
}

/**
 * Get all mock services
 */
export function getMockServices(): ServiceName[] {
  return (Object.keys(serviceStatus) as ServiceName[]).filter(
    (service) => !serviceStatus[service]
  );
}

// ===========================================
// SERVICE ENDPOINT PREFIXES
// ===========================================
export const apiPrefixes = {
  user: '/api',
  product: '/api',
  inventory: '/api',
  order: '/api',
  payment: '/api',
  shipping: '/api',
  review: '/api',
  recommendation: '/api',
  notification: '/api',
  analytics: '/api',
} as const;
