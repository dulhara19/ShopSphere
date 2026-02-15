/**
 * Order Service Mock Handlers
 */

import { http, HttpResponse, delay } from 'msw';
import { mockCart, mockOrders, getOrderSummaries } from '../data/orders';

let currentCart = { ...mockCart };

export const orderHandlers = [
  // GET /api/cart
  http.get('*/api/cart', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: currentCart,
    });
  }),

  // POST /api/cart/items
  http.post('*/api/cart/items', async ({ request }) => {
    await delay(300);
    const body = (await request.json()) as { productId: string; quantity: number };

    // Add item (simplified)
    const newItem = {
      id: `cart-item-${Date.now()}`,
      productId: body.productId,
      productName: 'New Product',
      productImage: 'https://via.placeholder.com/200',
      quantity: body.quantity,
      unitPrice: 99.99,
      totalPrice: 99.99 * body.quantity,
      inStock: true,
    };

    currentCart = {
      ...currentCart,
      items: [...currentCart.items, newItem],
      itemCount: currentCart.itemCount + body.quantity,
      subtotal: currentCart.subtotal + newItem.totalPrice,
      updatedAt: new Date().toISOString(),
    };

    return HttpResponse.json({
      success: true,
      data: currentCart,
    });
  }),

  // PUT /api/cart/items/:itemId
  http.put('*/api/cart/items/:itemId', async ({ params, request }) => {
    await delay(200);
    const body = (await request.json()) as { quantity: number };
    const itemId = params.itemId as string;

    currentCart = {
      ...currentCart,
      items: currentCart.items.map((item) =>
        item.id === itemId
          ? {
              ...item,
              quantity: body.quantity,
              totalPrice: item.unitPrice * body.quantity,
            }
          : item
      ),
      updatedAt: new Date().toISOString(),
    };

    // Recalculate totals
    currentCart.itemCount = currentCart.items.reduce((acc, item) => acc + item.quantity, 0);
    currentCart.subtotal = currentCart.items.reduce((acc, item) => acc + item.totalPrice, 0);

    return HttpResponse.json({
      success: true,
      data: currentCart,
    });
  }),

  // DELETE /api/cart/items/:itemId
  http.delete('*/api/cart/items/:itemId', async ({ params }) => {
    await delay(200);
    const itemId = params.itemId as string;

    currentCart = {
      ...currentCart,
      items: currentCart.items.filter((item) => item.id !== itemId),
      updatedAt: new Date().toISOString(),
    };

    currentCart.itemCount = currentCart.items.reduce((acc, item) => acc + item.quantity, 0);
    currentCart.subtotal = currentCart.items.reduce((acc, item) => acc + item.totalPrice, 0);

    return HttpResponse.json({
      success: true,
      data: currentCart,
    });
  }),

  // DELETE /api/cart
  http.delete('*/api/cart', async () => {
    await delay(200);
    currentCart = {
      ...currentCart,
      items: [],
      itemCount: 0,
      subtotal: 0,
      updatedAt: new Date().toISOString(),
    };
    return HttpResponse.json({ success: true, data: null });
  }),

  // GET /api/cart/totals
  http.get('*/api/cart/totals', async () => {
    await delay(300);
    const subtotal = currentCart.subtotal;
    const shippingAmount = subtotal > 100 ? 0 : 9.99;
    const taxAmount = subtotal * 0.08;
    const total = subtotal + shippingAmount + taxAmount;

    return HttpResponse.json({
      success: true,
      data: {
        subtotal,
        taxAmount,
        shippingAmount,
        discountAmount: 0,
        total,
        breakdown: {
          items: subtotal,
          shipping: shippingAmount,
          tax: taxAmount,
          discount: 0,
        },
      },
    });
  }),

  // POST /api/cart/validate
  http.post('*/api/cart/validate', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: {
        valid: true,
        items: currentCart.items.map((item) => ({
          productId: item.productId,
          valid: true,
        })),
        messages: [],
      },
    });
  }),

  // POST /api/orders/checkout
  http.post('*/api/orders/checkout', async ({ request }) => {
    await delay(1000);
    const body = await request.json();

    const newOrder = {
      id: `order-${Date.now()}`,
      orderNumber: `SS-${new Date().toISOString().slice(0, 10).replace(/-/g, '')}-${Math.floor(Math.random() * 1000)}`,
      userId: 'user-1',
      status: 'PENDING',
      items: currentCart.items.map((item) => ({
        id: `order-item-${Date.now()}`,
        productId: item.productId,
        productName: item.productName,
        productImage: item.productImage,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        totalPrice: item.totalPrice,
      })),
      subtotal: currentCart.subtotal,
      shippingAmount: currentCart.subtotal > 100 ? 0 : 9.99,
      taxAmount: currentCart.subtotal * 0.08,
      discountAmount: 0,
      totalAmount: currentCart.subtotal * 1.08 + (currentCart.subtotal > 100 ? 0 : 9.99),
      paymentStatus: 'PENDING',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    // Clear cart after checkout
    currentCart = {
      ...currentCart,
      items: [],
      itemCount: 0,
      subtotal: 0,
    };

    return HttpResponse.json({
      success: true,
      data: {
        order: newOrder,
        paymentIntent: {
          clientSecret: 'pi_mock_secret_123',
          amount: newOrder.totalAmount,
        },
      },
    });
  }),

  // GET /api/orders
  http.get('*/api/orders', async ({ request }) => {
    await delay(500);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const size = parseInt(url.searchParams.get('size') || '10');

    const summaries = getOrderSummaries();

    return HttpResponse.json({
      success: true,
      data: summaries,
      meta: {
        page,
        size,
        totalElements: summaries.length,
        totalPages: Math.ceil(summaries.length / size),
      },
    });
  }),

  // GET /api/orders/:id
  http.get('*/api/orders/:id', async ({ params }) => {
    await delay(300);
    const order = mockOrders.find((o) => o.id === params.id);

    if (!order) {
      return HttpResponse.json(
        {
          success: false,
          error: { code: 'NOT_FOUND', message: 'Order not found' },
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      success: true,
      data: order,
    });
  }),

  // POST /api/orders/:id/cancel
  http.post('*/api/orders/:id/cancel', async ({ params }) => {
    await delay(500);
    const order = mockOrders.find((o) => o.id === params.id);

    if (!order) {
      return HttpResponse.json(
        {
          success: false,
          error: { code: 'NOT_FOUND', message: 'Order not found' },
        },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      success: true,
      data: { ...order, status: 'CANCELLED' },
    });
  }),
];
