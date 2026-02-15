/**
 * Review Service Mock Handlers
 */

import { http, HttpResponse, delay } from 'msw';
import { mockReviews, getMockRatingSummary } from '../data/reviews';

export const reviewHandlers = [
  // GET /api/reviews/product/:productId
  http.get('*/api/reviews/product/:productId', async ({ params, request }) => {
    await delay(400);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const size = parseInt(url.searchParams.get('size') || '10');

    const productReviews = mockReviews.filter(
      (r) => r.productId === params.productId
    );

    return HttpResponse.json({
      success: true,
      data: productReviews,
      meta: {
        page,
        size,
        totalElements: productReviews.length,
        totalPages: Math.ceil(productReviews.length / size),
      },
    });
  }),

  // GET /api/reviews/product/:productId/summary
  http.get('*/api/reviews/product/:productId/summary', async ({ params }) => {
    await delay(200);
    const summary = getMockRatingSummary(params.productId as string);
    return HttpResponse.json({
      success: true,
      data: summary,
    });
  }),

  // GET /api/reviews/product/:productId/featured
  http.get('*/api/reviews/product/:productId/featured', async ({ params }) => {
    await delay(200);
    const featured = mockReviews
      .filter((r) => r.productId === params.productId)
      .sort((a, b) => b.helpfulCount - a.helpfulCount)
      .slice(0, 3);

    return HttpResponse.json({
      success: true,
      data: featured,
    });
  }),

  // POST /api/reviews
  http.post('*/api/reviews', async ({ request }) => {
    await delay(500);
    const body = await request.json();

    const newReview = {
      id: `review-${Date.now()}`,
      userId: 'user-1',
      userName: 'John D.',
      ...body,
      helpfulCount: 0,
      unhelpfulCount: 0,
      verified: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    return HttpResponse.json({
      success: true,
      data: newReview,
    });
  }),

  // GET /api/reviews/:reviewId
  http.get('*/api/reviews/:reviewId', async ({ params }) => {
    await delay(200);
    const review = mockReviews.find((r) => r.id === params.reviewId);

    if (!review) {
      return HttpResponse.json(
        {
          success: false,
          error: { code: 'NOT_FOUND', message: 'Review not found' },
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      success: true,
      data: review,
    });
  }),

  // POST /api/reviews/:reviewId/helpful
  http.post('*/api/reviews/:reviewId/helpful', async ({ params }) => {
    await delay(200);
    return HttpResponse.json({ success: true, data: null });
  }),

  // GET /api/reviews/user/me
  http.get('*/api/reviews/user/me', async ({ request }) => {
    await delay(300);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const size = parseInt(url.searchParams.get('size') || '10');

    const userReviews = mockReviews.filter((r) => r.userId === 'user-1');

    return HttpResponse.json({
      success: true,
      data: userReviews,
      meta: {
        page,
        size,
        totalElements: userReviews.length,
        totalPages: Math.ceil(userReviews.length / size),
      },
    });
  }),
];
