/**
 * Mock Product Data
 */

import { Product, ProductSummary, Category } from '@/types/product';

export const mockCategories: Category[] = [
  {
    id: 'cat-1',
    name: 'Electronics',
    description: 'Electronic devices and accessories',
    slug: 'electronics',
    imageUrl: 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400',
    productCount: 45,
    subcategories: [
      {
        id: 'cat-1-1',
        name: 'Smartphones',
        slug: 'smartphones',
        parentId: 'cat-1',
        productCount: 20,
      },
      {
        id: 'cat-1-2',
        name: 'Laptops',
        slug: 'laptops',
        parentId: 'cat-1',
        productCount: 15,
      },
      {
        id: 'cat-1-3',
        name: 'Accessories',
        slug: 'accessories',
        parentId: 'cat-1',
        productCount: 10,
      },
    ],
  },
  {
    id: 'cat-2',
    name: 'Clothing',
    description: 'Fashion and apparel',
    slug: 'clothing',
    imageUrl: 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=400',
    productCount: 120,
    subcategories: [
      {
        id: 'cat-2-1',
        name: "Men's Wear",
        slug: 'mens-wear',
        parentId: 'cat-2',
        productCount: 50,
      },
      {
        id: 'cat-2-2',
        name: "Women's Wear",
        slug: 'womens-wear',
        parentId: 'cat-2',
        productCount: 70,
      },
    ],
  },
  {
    id: 'cat-3',
    name: 'Home & Garden',
    description: 'Home decor and garden supplies',
    slug: 'home-garden',
    imageUrl: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
    productCount: 80,
  },
  {
    id: 'cat-4',
    name: 'Sports',
    description: 'Sports equipment and activewear',
    slug: 'sports',
    imageUrl: 'https://images.unsplash.com/photo-1461896836934- voices-of-active-lifestyle?w=400',
    productCount: 60,
  },
];

export const mockProducts: Product[] = [
  {
    id: 'prod-1',
    sellerId: 'user-2',
    name: 'Premium Wireless Headphones',
    description:
      'High-quality wireless headphones with active noise cancellation, 30-hour battery life, and premium sound quality. Perfect for music lovers and professionals.',
    price: 299.99,
    compareAtPrice: 349.99,
    categoryId: 'cat-1',
    categoryName: 'Electronics',
    subcategoryId: 'cat-1-3',
    subcategoryName: 'Accessories',
    sku: 'WH-PRO-001',
    brand: 'AudioTech',
    images: [
      {
        id: 'img-1-1',
        url: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
      {
        id: 'img-1-2',
        url: 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=200',
        isPrimary: false,
        sortOrder: 1,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.5,
    reviewCount: 128,
    tags: ['wireless', 'noise-cancelling', 'premium'],
    createdAt: '2024-01-10T10:00:00Z',
    updatedAt: '2024-06-01T14:30:00Z',
  },
  {
    id: 'prod-2',
    sellerId: 'user-2',
    name: 'Minimalist Leather Watch',
    description:
      'Elegant minimalist watch with genuine leather strap and Japanese quartz movement. Water-resistant up to 30 meters.',
    price: 149.99,
    categoryId: 'cat-1',
    categoryName: 'Electronics',
    subcategoryId: 'cat-1-3',
    subcategoryName: 'Accessories',
    sku: 'WTC-MIN-001',
    brand: 'TimeClass',
    images: [
      {
        id: 'img-2-1',
        url: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.8,
    reviewCount: 89,
    tags: ['minimalist', 'leather', 'elegant'],
    createdAt: '2024-02-15T08:00:00Z',
    updatedAt: '2024-05-20T11:00:00Z',
  },
  {
    id: 'prod-3',
    sellerId: 'user-2',
    name: 'Running Shoes Pro',
    description:
      'Professional running shoes with advanced cushioning technology and breathable mesh upper. Perfect for marathon runners.',
    price: 189.99,
    compareAtPrice: 219.99,
    categoryId: 'cat-4',
    categoryName: 'Sports',
    sku: 'RUN-PRO-001',
    brand: 'SpeedFit',
    images: [
      {
        id: 'img-3-1',
        url: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.6,
    reviewCount: 234,
    tags: ['running', 'professional', 'cushioning'],
    createdAt: '2024-03-01T09:00:00Z',
    updatedAt: '2024-06-05T16:00:00Z',
  },
  {
    id: 'prod-4',
    sellerId: 'user-2',
    name: 'Organic Cotton T-Shirt',
    description:
      '100% organic cotton t-shirt with a comfortable fit. Sustainably sourced and eco-friendly.',
    price: 39.99,
    categoryId: 'cat-2',
    categoryName: 'Clothing',
    subcategoryId: 'cat-2-1',
    subcategoryName: "Men's Wear",
    sku: 'TSH-ORG-001',
    brand: 'EcoWear',
    images: [
      {
        id: 'img-4-1',
        url: 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.3,
    reviewCount: 456,
    tags: ['organic', 'cotton', 'sustainable'],
    createdAt: '2024-01-20T12:00:00Z',
    updatedAt: '2024-04-15T10:00:00Z',
  },
  {
    id: 'prod-5',
    sellerId: 'user-2',
    name: 'Smart Home Hub',
    description:
      'Central control hub for all your smart home devices. Works with Alexa, Google Assistant, and HomeKit.',
    price: 129.99,
    categoryId: 'cat-1',
    categoryName: 'Electronics',
    sku: 'SMH-HUB-001',
    brand: 'SmartLife',
    images: [
      {
        id: 'img-5-1',
        url: 'https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1558089687-f282ffcbc126?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.4,
    reviewCount: 167,
    tags: ['smart-home', 'hub', 'automation'],
    createdAt: '2024-02-28T14:00:00Z',
    updatedAt: '2024-06-08T09:00:00Z',
  },
  {
    id: 'prod-6',
    sellerId: 'user-2',
    name: 'Premium Coffee Maker',
    description:
      'Professional-grade coffee maker with built-in grinder and programmable brewing. Makes the perfect cup every time.',
    price: 249.99,
    compareAtPrice: 299.99,
    categoryId: 'cat-3',
    categoryName: 'Home & Garden',
    sku: 'CFM-PRO-001',
    brand: 'BrewMaster',
    images: [
      {
        id: 'img-6-1',
        url: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600',
        thumbnailUrl:
          'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=200',
        isPrimary: true,
        sortOrder: 0,
      },
    ],
    status: 'ACTIVE',
    averageRating: 4.7,
    reviewCount: 312,
    tags: ['coffee', 'kitchen', 'premium'],
    createdAt: '2024-03-15T11:00:00Z',
    updatedAt: '2024-05-30T15:00:00Z',
  },
];

export const getProductSummaries = (): ProductSummary[] =>
  mockProducts.map((p) => ({
    id: p.id,
    name: p.name,
    price: p.price,
    compareAtPrice: p.compareAtPrice,
    primaryImage: p.images.find((i) => i.isPrimary)?.url || p.images[0]?.url,
    averageRating: p.averageRating,
    reviewCount: p.reviewCount,
    status: p.status,
  }));
