import { NextRequest, NextResponse } from 'next/server';

/**
 * API Proxy Middleware
 *
 * Rewrites /api/* and /internal/* requests to the correct backend service.
 * This runs server-side inside the Next.js process, so it can resolve
 * Docker service hostnames (e.g. http://user-service:3001).
 *
 * Why middleware instead of next.config.js rewrites?
 * - next.config.js rewrites are baked at build time in standalone mode
 * - Middleware reads process.env at RUNTIME, so Docker env vars work
 */

// Longest-prefix-first order matters for correct matching
// e.g. /api/admin/orders must match before /api/admin
const ROUTE_MAP: [string, string][] = [
  // User service
  ['/api/admin/users', 'USER_SERVICE_URL'],
  ['/api/auth', 'USER_SERVICE_URL'],
  ['/api/users', 'USER_SERVICE_URL'],
  // Product service
  ['/api/products', 'PRODUCT_SERVICE_URL'],
  ['/api/categories', 'PRODUCT_SERVICE_URL'],
  ['/api/attributes', 'PRODUCT_SERVICE_URL'],
  ['/uploads', 'PRODUCT_SERVICE_URL'],
  // Inventory service
  ['/api/inventory', 'INVENTORY_SERVICE_URL'],
  // Order service
  ['/api/admin/orders', 'ORDER_SERVICE_URL'],
  ['/api/cart', 'ORDER_SERVICE_URL'],
  ['/api/orders', 'ORDER_SERVICE_URL'],
  // Payment service
  ['/api/payments', 'PAYMENT_SERVICE_URL'],
  ['/api/payment-methods', 'PAYMENT_SERVICE_URL'],
  ['/api/transactions', 'PAYMENT_SERVICE_URL'],
  ['/api/webhooks', 'PAYMENT_SERVICE_URL'],
  // Shipping service
  ['/api/shipping', 'SHIPPING_SERVICE_URL'],
  // Review service
  ['/api/reviews', 'REVIEW_SERVICE_URL'],
  // Recommendation service
  ['/api/recommendations', 'RECOMMENDATION_SERVICE_URL'],
  ['/api/events', 'RECOMMENDATION_SERVICE_URL'],
  // Notification service
  ['/api/notifications', 'NOTIFICATION_SERVICE_URL'],
  ['/internal/notifications', 'NOTIFICATION_SERVICE_URL'],
  // Analytics service
  ['/api/analytics', 'ANALYTICS_SERVICE_URL'],
];

const DEFAULT_URLS: Record<string, string> = {
  USER_SERVICE_URL: 'http://localhost:3001',
  PRODUCT_SERVICE_URL: 'http://localhost:3002',
  INVENTORY_SERVICE_URL: 'http://localhost:3003',
  ORDER_SERVICE_URL: 'http://localhost:3004',
  PAYMENT_SERVICE_URL: 'http://localhost:3005',
  SHIPPING_SERVICE_URL: 'http://localhost:3006',
  REVIEW_SERVICE_URL: 'http://localhost:3007',
  RECOMMENDATION_SERVICE_URL: 'http://localhost:3008',
  NOTIFICATION_SERVICE_URL: 'http://localhost:3009',
  ANALYTICS_SERVICE_URL: 'http://localhost:3010',
};

export function middleware(request: NextRequest) {
  const { pathname, search } = request.nextUrl;

  for (const [prefix, envKey] of ROUTE_MAP) {
    if (pathname.startsWith(prefix)) {
      const serviceUrl = process.env[envKey] || DEFAULT_URLS[envKey];
      const destination = new URL(pathname + search, serviceUrl);
      return NextResponse.rewrite(destination);
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/api/:path*', '/internal/:path*', '/uploads/:path*'],
};
