/** @type {import('next').NextConfig} */

const USER_SERVICE = process.env.USER_SERVICE_URL || 'http://localhost:3001';
const PRODUCT_SERVICE = process.env.PRODUCT_SERVICE_URL || 'http://localhost:3002';
const INVENTORY_SERVICE = process.env.INVENTORY_SERVICE_URL || 'http://localhost:3003';
const ORDER_SERVICE = process.env.ORDER_SERVICE_URL || 'http://localhost:3004';
const PAYMENT_SERVICE = process.env.PAYMENT_SERVICE_URL || 'http://localhost:3005';
const SHIPPING_SERVICE = process.env.SHIPPING_SERVICE_URL || 'http://localhost:3006';
const REVIEW_SERVICE = process.env.REVIEW_SERVICE_URL || 'http://localhost:3007';
const RECOMMENDATION_SERVICE = process.env.RECOMMENDATION_SERVICE_URL || 'http://localhost:3008';
const NOTIFICATION_SERVICE = process.env.NOTIFICATION_SERVICE_URL || 'http://localhost:3009';
const ANALYTICS_SERVICE = process.env.ANALYTICS_SERVICE_URL || 'http://localhost:3010';

const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  reactStrictMode: true,
  output: 'standalone',
  images: {
    unoptimized: true,
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**.shopsphere.com',
      },
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
      {
        protocol: 'https',
        hostname: 'via.placeholder.com',
      },
    ],
  },
  async rewrites() {
    return [
      // User service (3001)
      { source: '/api/auth/:path*', destination: `${USER_SERVICE}/api/auth/:path*` },
      { source: '/api/users/:path*', destination: `${USER_SERVICE}/api/users/:path*` },
      { source: '/api/admin/users/:path*', destination: `${USER_SERVICE}/api/admin/users/:path*` },
      // Product service (3002)
      { source: '/api/products/:path*', destination: `${PRODUCT_SERVICE}/api/products/:path*` },
      { source: '/api/categories/:path*', destination: `${PRODUCT_SERVICE}/api/categories/:path*` },
      { source: '/api/attributes/:path*', destination: `${PRODUCT_SERVICE}/api/attributes/:path*` },
      // Inventory service (3003)
      { source: '/api/inventory/:path*', destination: `${INVENTORY_SERVICE}/api/inventory/:path*` },
      // Order service (3004)
      { source: '/api/cart/:path*', destination: `${ORDER_SERVICE}/api/cart/:path*` },
      { source: '/api/orders/:path*', destination: `${ORDER_SERVICE}/api/orders/:path*` },
      { source: '/api/admin/orders/:path*', destination: `${ORDER_SERVICE}/api/admin/orders/:path*` },
      // Review service (3007)
      { source: '/api/reviews/:path*', destination: `${REVIEW_SERVICE}/api/reviews/:path*` },
      // Recommendation service (3008)
      { source: '/api/recommendations/:path*', destination: `${RECOMMENDATION_SERVICE}/api/recommendations/:path*` },
      { source: '/api/events/:path*', destination: `${RECOMMENDATION_SERVICE}/api/events/:path*` },
      // Notification service (3009)
      { source: '/api/notifications/:path*', destination: `${NOTIFICATION_SERVICE}/api/notifications/:path*` },
      { source: '/internal/notifications/:path*', destination: `${NOTIFICATION_SERVICE}/internal/notifications/:path*` },
      // Shipping service (3006)
      { source: '/api/shipping/:path*', destination: `${SHIPPING_SERVICE}/api/shipping/:path*` },
      // Payment service (3005)
      { source: '/api/payments/:path*', destination: `${PAYMENT_SERVICE}/api/payments/:path*` },
      { source: '/api/payment-methods/:path*', destination: `${PAYMENT_SERVICE}/api/payment-methods/:path*` },
      { source: '/api/transactions/:path*', destination: `${PAYMENT_SERVICE}/api/transactions/:path*` },
      { source: '/api/webhooks/:path*', destination: `${PAYMENT_SERVICE}/api/webhooks/:path*` },
      // Analytics service (3010)
      { source: '/api/analytics/:path*', destination: `${ANALYTICS_SERVICE}/api/analytics/:path*` },
      // Product image uploads (served from product-service)
      { source: '/uploads/:path*', destination: `${PRODUCT_SERVICE}/uploads/:path*` },
    ];
  },
};

module.exports = nextConfig;
