/**
 * MSW Handlers Index
 *
 * Combines all service handlers.
 */

import { userHandlers } from './user';
import { productHandlers } from './product';
import { orderHandlers } from './order';
import { reviewHandlers } from './review';

// Additional simple handlers for other services
import { http, HttpResponse, delay } from 'msw';
import { getProductSummaries } from '../data/products';

const inventoryHandlers = [
  http.get('*/api/inventory/:productId', async () => {
    await delay(200);
    return HttpResponse.json({
      success: true,
      data: {
        productId: 'prod-1',
        quantity: 100,
        reserved: 5,
        available: 95,
        lowStockThreshold: 10,
        isLowStock: false,
        warehouse: 'MAIN',
        updatedAt: new Date().toISOString(),
      },
    });
  }),

  http.post('*/api/inventory/check-availability', async () => {
    await delay(200);
    return HttpResponse.json({
      success: true,
      data: {
        items: [{ productId: 'prod-1', requested: 1, available: 95, isAvailable: true }],
        allAvailable: true,
      },
    });
  }),
];

const recommendationHandlers = [
  http.get('*/api/recommendations/trending', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: getProductSummaries().slice(0, 4),
    });
  }),

  http.get('*/api/recommendations/for-you', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: getProductSummaries().slice(0, 6),
    });
  }),

  http.get('*/api/recommendations/similar/:productId', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: getProductSummaries().slice(0, 4),
    });
  }),

  http.get('*/api/recommendations/homepage', async () => {
    await delay(400);
    const products = getProductSummaries();
    return HttpResponse.json({
      success: true,
      data: {
        trending: products.slice(0, 4),
        newArrivals: products.slice(2, 6),
        bestSellers: products.slice(0, 4),
      },
    });
  }),
];

const notificationHandlers = [
  http.get('*/api/notifications', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: [
        {
          id: 'notif-1',
          type: 'order',
          title: 'Order Shipped',
          message: 'Your order #SS-20240615-002 has been shipped!',
          isRead: false,
          createdAt: new Date().toISOString(),
        },
      ],
      meta: { page: 1, size: 10, totalElements: 1, totalPages: 1 },
    });
  }),

  http.get('*/api/notifications/unread-count', async () => {
    await delay(100);
    return HttpResponse.json({
      success: true,
      data: { count: 3 },
    });
  }),

  http.get('*/api/notifications/preferences', async () => {
    await delay(200);
    return HttpResponse.json({
      success: true,
      data: {
        email: { orderUpdates: true, promotions: false, reviews: true, newsletter: false },
        sms: { orderUpdates: true, promotions: false },
        push: { orderUpdates: true, promotions: true, reviews: true },
      },
    });
  }),
];

const paymentHandlers = [
  http.post('*/api/payments/create-intent', async () => {
    await delay(500);
    return HttpResponse.json({
      success: true,
      data: {
        id: 'pi_mock_123',
        clientSecret: 'pi_mock_secret_123',
        amount: 399.99,
        currency: 'usd',
        status: 'pending',
        orderId: 'order-123',
      },
    });
  }),

  http.get('*/api/payment-methods', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: [
        {
          id: 'pm_1',
          type: 'card',
          card: { brand: 'visa', last4: '4242', expiryMonth: 12, expiryYear: 2026 },
          isDefault: true,
          createdAt: '2024-01-15T10:00:00Z',
        },
      ],
    });
  }),
];

const shippingHandlers = [
  http.post('*/api/shipping/calculate-rate', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: [
        {
          carrier: 'USPS',
          service: 'Priority Mail',
          rate: 9.99,
          currency: 'USD',
          estimatedDays: 3,
          deliveryDate: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
        },
        {
          carrier: 'FedEx',
          service: 'Express',
          rate: 14.99,
          currency: 'USD',
          estimatedDays: 2,
          deliveryDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        },
      ],
    });
  }),

  http.get('*/api/shipping/:trackingNumber', async ({ params }) => {
    await delay(400);
    return HttpResponse.json({
      success: true,
      data: {
        trackingNumber: params.trackingNumber,
        carrier: 'USPS',
        status: 'in_transit',
        estimatedDelivery: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        events: [
          {
            timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
            location: 'New York, NY',
            description: 'Package picked up',
            status: 'picked_up',
          },
          {
            timestamp: new Date().toISOString(),
            location: 'Newark, NJ',
            description: 'In transit to destination',
            status: 'in_transit',
          },
        ],
      },
    });
  }),
];

// Combine all handlers
export const handlers = [
  ...userHandlers,
  ...productHandlers,
  ...orderHandlers,
  ...reviewHandlers,
  ...inventoryHandlers,
  ...recommendationHandlers,
  ...notificationHandlers,
  ...paymentHandlers,
  ...shippingHandlers,
];
