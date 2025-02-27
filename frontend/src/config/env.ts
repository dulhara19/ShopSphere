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

  // Individual Service URLs (empty = use Next.js proxy rewrites, avoids CORS)
  services: {
    user: process.env.NEXT_PUBLIC_USER_SERVICE_URL || '',
    product: process.env.NEXT_PUBLIC_PRODUCT_SERVICE_URL || '',
    inventory: process.env.NEXT_PUBLIC_INVENTORY_SERVICE_URL || '',
    order: process.env.NEXT_PUBLIC_ORDER_SERVICE_URL || '',
    payment: process.env.NEXT_PUBLIC_PAYMENT_SERVICE_URL || '',
    shipping: process.env.NEXT_PUBLIC_SHIPPING_SERVICE_URL || '',
    review: process.env.NEXT_PUBLIC_REVIEW_SERVICE_URL || '',
    recommendation: process.env.NEXT_PUBLIC_RECOMMENDATION_SERVICE_URL || '',
    notification: process.env.NEXT_PUBLIC_NOTIFICATION_SERVICE_URL || '',
    analytics: process.env.NEXT_PUBLIC_ANALYTICS_SERVICE_URL || '',
  },

  // Stripe
  stripePublishableKey: process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY || '',

  // MSW
  enableMsw: process.env.NEXT_PUBLIC_ENABLE_MSW === 'true',
} as const;

export type ServiceName = keyof typeof env.services;
