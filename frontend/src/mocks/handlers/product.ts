/**
 * Product Service Mock Handlers
 */

import { http, HttpResponse, delay } from 'msw';
import { mockProducts, mockCategories, getProductSummaries } from '../data/products';

export const productHandlers = [
  // GET /api/products
  http.get('*/api/products', async ({ request }) => {
    await delay(500);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const size = parseInt(url.searchParams.get('size') || '12');
    const category = url.searchParams.get('category');
    const search = url.searchParams.get('search');
    const minPrice = url.searchParams.get('minPrice');
    const maxPrice = url.searchParams.get('maxPrice');
    const sort = url.searchParams.get('sort');

    let products = [...mockProducts];

    // Filter by category
    if (category) {
      products = products.filter((p) => p.categoryId === category);
    }

    // Filter by search
    if (search) {
      const searchLower = search.toLowerCase();
      products = products.filter(
        (p) =>
          p.name.toLowerCase().includes(searchLower) ||
          p.description.toLowerCase().includes(searchLower)
      );
    }

    // Filter by price
    if (minPrice) {
      products = products.filter((p) => p.price >= parseFloat(minPrice));
    }
    if (maxPrice) {
      products = products.filter((p) => p.price <= parseFloat(maxPrice));
    }

    // Sort
    if (sort) {
      switch (sort) {
        case 'price_asc':
          products.sort((a, b) => a.price - b.price);
          break;
        case 'price_desc':
          products.sort((a, b) => b.price - a.price);
          break;
        case 'newest':
          products.sort(
            (a, b) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          );
          break;
        case 'rating':
          products.sort((a, b) => b.averageRating - a.averageRating);
          break;
      }
    }

    // Paginate
    const start = (page - 1) * size;
    const paginatedProducts = products.slice(start, start + size);

    // Convert to summaries
    const summaries = paginatedProducts.map((p) => ({
      id: p.id,
      name: p.name,
      price: p.price,
      compareAtPrice: p.compareAtPrice,
      primaryImage: p.images.find((i) => i.isPrimary)?.url || p.images[0]?.url,
      averageRating: p.averageRating,
      reviewCount: p.reviewCount,
      status: p.status,
    }));

    return HttpResponse.json({
      success: true,
      data: summaries,
      meta: {
        page,
        size,
        totalElements: products.length,
        totalPages: Math.ceil(products.length / size),
      },
    });
  }),

  // GET /api/products/:id
  http.get('*/api/products/:id', async ({ params }) => {
    await delay(300);
    const product = mockProducts.find((p) => p.id === params.id);

    if (!product) {
      return HttpResponse.json(
        {
          success: false,
          error: {
            code: 'NOT_FOUND',
            message: 'Product not found',
            timestamp: new Date().toISOString(),
            path: `/api/products/${params.id}`,
          },
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      success: true,
      data: product,
    });
  }),

  // GET /api/categories
  http.get('*/api/categories', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: mockCategories,
    });
  }),

  // GET /api/categories/:id
  http.get('*/api/categories/:id', async ({ params }) => {
    await delay(200);
    const category = mockCategories.find((c) => c.id === params.id);

    if (!category) {
      return HttpResponse.json(
        {
          success: false,
          error: { code: 'NOT_FOUND', message: 'Category not found' },
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      success: true,
      data: category,
    });
  }),

  // POST /api/products (create)
  http.post('*/api/products', async ({ request }) => {
    await delay(500);
    const body = await request.json();
    const newProduct = {
      id: `prod-${Date.now()}`,
      sellerId: 'user-2',
      ...body,
      images: [],
      status: 'ACTIVE',
      averageRating: 0,
      reviewCount: 0,
      tags: body.tags || [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    return HttpResponse.json({
      success: true,
      data: newProduct,
    });
  }),

  // POST /internal/products/batch
  http.post('*/internal/products/batch', async ({ request }) => {
    await delay(300);
    const body = (await request.json()) as { productIds: string[] };
    const products = mockProducts.filter((p) => body.productIds.includes(p.id));
    return HttpResponse.json({
      success: true,
      data: products,
    });
  }),
];
