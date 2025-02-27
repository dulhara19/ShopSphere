/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  images: {
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
    if (process.env.NODE_ENV === 'development') {
      return [
        // User service (3001)
        { source: '/api/auth/:path*', destination: 'http://localhost:3001/api/auth/:path*' },
        { source: '/api/users/:path*', destination: 'http://localhost:3001/api/users/:path*' },
        { source: '/api/admin/users/:path*', destination: 'http://localhost:3001/api/admin/users/:path*' },
        // Product service (3002)
        { source: '/api/products/:path*', destination: 'http://localhost:3002/api/products/:path*' },
        { source: '/api/categories/:path*', destination: 'http://localhost:3002/api/categories/:path*' },
        // Inventory service (3003)
        { source: '/api/inventory/:path*', destination: 'http://localhost:3003/api/inventory/:path*' },
        // Order service (3004)
        { source: '/api/cart/:path*', destination: 'http://localhost:3004/api/cart/:path*' },
        { source: '/api/orders/:path*', destination: 'http://localhost:3004/api/orders/:path*' },
        { source: '/api/admin/orders/:path*', destination: 'http://localhost:3004/api/admin/orders/:path*' },
        // Review service (3007)
        { source: '/api/reviews/:path*', destination: 'http://localhost:3007/api/reviews/:path*' },
        // Recommendation service (3008)
        { source: '/api/recommendations/:path*', destination: 'http://localhost:3008/api/recommendations/:path*' },
        { source: '/api/events/:path*', destination: 'http://localhost:3008/api/events/:path*' },
        // Notification service (3009)
        { source: '/api/notifications/:path*', destination: 'http://localhost:3009/api/notifications/:path*' },
        // Analytics service (3010)
        { source: '/api/analytics/:path*', destination: 'http://localhost:3010/api/analytics/:path*' },
      ];
    }
    return [];
  },
};

module.exports = nextConfig;
