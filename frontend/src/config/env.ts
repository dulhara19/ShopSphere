/**
 * Environment Configuration
 *
 * Centralizes all environment variables with type safety.
 * AI Agent Note: Update NEXT_PUBLIC_ENABLE_* flags when services become available.
 */

export const env = {
  // Environment
  nodeEnv: process.env.NODE_ENV || 'development',
  appEnv: process.env.NEXT_PUBLIC_APP_ENV || 'development',

  // App Info
  appName: process.env.NEXT_PUBLIC_APP_NAME || 'ShopSphere',
  appUrl: process.env.NEXT_PUBLIC_APP_URL || 'http://localhost:3000',

  // API Gateway (for staging/production)
  apiGatewayUrl: process.env.NEXT_PUBLIC_API_GATEWAY_URL || '',

  // Individual Service URLs
  services: {
    user: process.env.NEXT_PUBLIC_USER_SERVICE_URL || 'http://localhost:3001',
    product: process.env.NEXT_PUBLIC_PRODUCT_SERVICE_URL || 'http://localhost:3002',
    inventory: process.env.NEXT_PUBLIC_INVENTORY_SERVICE_URL || 'http://localhost:3003',
    order: process.env.NEXT_PUBLIC_ORDER_SERVICE_URL || 'http://localhost:3004',
    payment: process.env.NEXT_PUBLIC_PAYMENT_SERVICE_URL || 'http://localhost:3005',
    shipping: process.env.NEXT_PUBLIC_SHIPPING_SERVICE_URL || 'http://localhost:3006',
    review: process.env.NEXT_PUBLIC_REVIEW_SERVICE_URL || 'http://localhost:3007',
    recommendation: process.env.NEXT_PUBLIC_RECOMMENDATION_SERVICE_URL || 'http://localhost:3008',
    notification: process.env.NEXT_PUBLIC_NOTIFICATION_SERVICE_URL || 'http://localhost:3009',
    analytics: process.env.NEXT_PUBLIC_ANALYTICS_SERVICE_URL || 'http://localhost:3010',
  },

  // Stripe
  stripePublishableKey: process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY || '',

  // MSW
  enableMsw: process.env.NEXT_PUBLIC_ENABLE_MSW === 'true',
} as const;

export type ServiceName = keyof typeof env.services;
