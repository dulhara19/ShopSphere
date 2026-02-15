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
    // Only in development - proxy API calls to backend services
    if (process.env.NODE_ENV === 'development') {
      return [
        // These rewrites will be used when real services are available
        // For now, MSW intercepts all API calls
      ];
    }
    return [];
  },
};

module.exports = nextConfig;
