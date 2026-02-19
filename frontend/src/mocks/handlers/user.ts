/**
 * User Service Mock Handlers
 */

import { http, HttpResponse, delay } from 'msw';
import {
  mockUsers,
  mockAddresses,
  createAuthResponse,
  testCredentials,
} from '../data/users';

export const userHandlers = [
  // POST /api/auth/login
  http.post('*/api/auth/login', async ({ request }) => {
    await delay(500);
    const body = (await request.json()) as { email: string; password: string };

    const user = mockUsers.find((u) => u.email === body.email);
    if (user && body.password === 'password123') {
      return HttpResponse.json({
        success: true,
        data: createAuthResponse(user),
      });
    }

    return HttpResponse.json(
      {
        success: false,
        error: {
          code: 'AUTH_INVALID_CREDENTIALS',
          message: 'Invalid email or password',
          timestamp: new Date().toISOString(),
          path: '/api/auth/login',
        },
      },
      { status: 401 }
    );
  }),

  // POST /api/auth/register
  http.post('*/api/auth/register', async ({ request }) => {
    await delay(500);
    const body = (await request.json()) as {
      email: string;
      password: string;
      firstName: string;
      lastName: string;
    };

    const existingUser = mockUsers.find((u) => u.email === body.email);
    if (existingUser) {
      return HttpResponse.json(
        {
          success: false,
          error: {
            code: 'AUTH_EMAIL_EXISTS',
            message: 'Email already registered',
            timestamp: new Date().toISOString(),
            path: '/api/auth/register',
          },
        },
        { status: 400 }
      );
    }

    const newUser = {
      id: `user-${Date.now()}`,
      email: body.email,
      firstName: body.firstName,
      lastName: body.lastName,
      role: 'CUSTOMER' as const,
      status: 'ACTIVE' as const,
      emailVerified: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    return HttpResponse.json({
      success: true,
      data: createAuthResponse(newUser),
    });
  }),

  // POST /api/auth/refresh
  http.post('*/api/auth/refresh', async () => {
    await delay(200);
    const user = mockUsers[0];
    return HttpResponse.json({
      success: true,
      data: createAuthResponse(user),
    });
  }),

  // POST /api/auth/logout
  http.post('*/api/auth/logout', async () => {
    await delay(200);
    return HttpResponse.json({ success: true, data: null });
  }),

  // GET /api/users/me
  http.get('*/api/users/me', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: mockUsers[0],
    });
  }),

  // PUT /api/users/:id
  http.put('*/api/users/:id', async ({ request }) => {
    await delay(300);
    const body = await request.json();
    const user = { ...mockUsers[0], ...body, updatedAt: new Date().toISOString() };
    return HttpResponse.json({
      success: true,
      data: user,
    });
  }),

  // GET /api/users/me/addresses
  http.get('*/api/users/me/addresses', async () => {
    await delay(300);
    return HttpResponse.json({
      success: true,
      data: mockAddresses,
    });
  }),

  // POST /api/users/me/addresses
  http.post('*/api/users/me/addresses', async ({ request }) => {
    await delay(300);
    const body = await request.json();
    const newAddress = {
      id: `addr-${Date.now()}`,
      userId: 'user-1',
      ...body,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    return HttpResponse.json({
      success: true,
      data: newAddress,
    });
  }),

  // PUT /api/users/me/addresses/:id
  http.put('*/api/users/me/addresses/:id', async ({ params, request }) => {
    await delay(300);
    const body = await request.json();
    const address = mockAddresses.find((a) => a.id === params.id);
    if (!address) {
      return HttpResponse.json(
        {
          success: false,
          error: { code: 'NOT_FOUND', message: 'Address not found' },
        },
        { status: 404 }
      );
    }
    return HttpResponse.json({
      success: true,
      data: { ...address, ...body, updatedAt: new Date().toISOString() },
    });
  }),

  // DELETE /api/users/me/addresses/:id
  http.delete('*/api/users/me/addresses/:id', async () => {
    await delay(300);
    return HttpResponse.json({ success: true, data: null });
  }),

  // GET /api/admin/users
  http.get('*/api/admin/users', async ({ request }) => {
    await delay(500);
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '1');
    const size = parseInt(url.searchParams.get('size') || '10');

    return HttpResponse.json({
      success: true,
      data: mockUsers,
      meta: {
        page,
        size,
        totalElements: mockUsers.length,
        totalPages: Math.ceil(mockUsers.length / size),
      },
    });
  }),
];
