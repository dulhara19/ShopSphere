# ShopSphere Frontend Specification

**Owner:** Team Lead (LakshanDulhara)
**Last Updated:** 2026-02-14
**Status:** Planning

---

## Overview

This document defines the unified frontend for ShopSphere e-commerce platform. The frontend is built **contract-first** - developed against defined API contracts with mocks, enabling parallel development with microservices.

### Development Strategy

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONTRACT-FIRST DEVELOPMENT                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   API Contracts (defined)                                       │
│         │                                                       │
│         ├──────────────────┬────────────────────┐              │
│         ▼                  ▼                    ▼              │
│   ┌──────────┐      ┌───────────┐      ┌─────────────┐        │
│   │ Frontend │      │ Mock APIs │      │Microservices│        │
│   │ (You)    │◄────►│ (MSW/JSON)│      │ (Team)      │        │
│   └──────────┘      └───────────┘      └─────────────┘        │
│         │                                    │                 │
│         │         When service ready         │                 │
│         └────────────────────────────────────┘                 │
│                   Swap mock → real                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology | Rationale |
|-------|------------|-----------|
| Framework | **Next.js 14+** (App Router) | SSR, API routes, file-based routing |
| Language | **TypeScript** | Type safety with API contracts |
| Styling | **Tailwind CSS** | Rapid UI development |
| UI Components | **shadcn/ui** | Accessible, customizable components |
| State Management | **Zustand** | Lightweight, TypeScript-friendly |
| Data Fetching | **TanStack Query** | Caching, mutations, optimistic updates |
| Forms | **React Hook Form + Zod** | Validation aligned with API schemas |
| Mock APIs | **MSW (Mock Service Worker)** | Intercept network requests |
| Testing | **Vitest + Playwright** | Unit + E2E testing |

---

## Project Structure

```
frontend/
├── public/
│   ├── images/
│   └── icons/
├── src/
│   ├── app/                      # Next.js App Router
│   │   ├── (auth)/               # Auth routes group
│   │   │   ├── login/
│   │   │   ├── register/
│   │   │   └── forgot-password/
│   │   ├── (shop)/               # Main shop routes
│   │   │   ├── products/
│   │   │   ├── cart/
│   │   │   ├── checkout/
│   │   │   └── orders/
│   │   ├── (user)/               # User account routes
│   │   │   ├── profile/
│   │   │   ├── addresses/
│   │   │   └── settings/
│   │   ├── (admin)/              # Admin dashboard
│   │   │   ├── orders/
│   │   │   ├── products/
│   │   │   └── users/
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── components/
│   │   ├── ui/                   # shadcn components
│   │   ├── layout/               # Header, Footer, Sidebar
│   │   ├── products/             # Product cards, grids
│   │   ├── cart/                 # Cart components
│   │   ├── checkout/             # Checkout flow
│   │   └── orders/               # Order components
│   ├── lib/
│   │   ├── api/                  # API client functions
│   │   │   ├── client.ts         # Base axios/fetch config
│   │   │   ├── user.ts           # User service APIs
│   │   │   ├── product.ts        # Product service APIs
│   │   │   ├── inventory.ts      # Inventory service APIs
│   │   │   ├── order.ts          # Order service APIs
│   │   │   ├── payment.ts        # Payment service APIs
│   │   │   ├── shipping.ts       # Shipping service APIs
│   │   │   ├── review.ts         # Review service APIs
│   │   │   ├── recommendation.ts # Recommendation APIs
│   │   │   └── notification.ts   # Notification APIs
│   │   ├── hooks/                # Custom React hooks
│   │   ├── utils/                # Helper functions
│   │   └── validations/          # Zod schemas
│   ├── stores/                   # Zustand stores
│   │   ├── auth.ts
│   │   ├── cart.ts
│   │   └── ui.ts
│   ├── types/                    # TypeScript types
│   │   ├── api.ts                # API response types
│   │   ├── user.ts
│   │   ├── product.ts
│   │   ├── order.ts
│   │   └── index.ts
│   ├── mocks/                    # MSW mock handlers
│   │   ├── handlers/
│   │   │   ├── user.ts
│   │   │   ├── product.ts
│   │   │   ├── order.ts
│   │   │   └── ...
│   │   ├── data/                 # Mock data fixtures
│   │   ├── browser.ts
│   │   └── server.ts
│   └── config/
│       ├── env.ts                # Environment config
│       └── services.ts           # Service URL mapping
├── docs/
│   └── FRONTEND-SPEC.md          # This file
├── package.json
├── tsconfig.json
├── tailwind.config.ts
└── next.config.js
```

---

## Service Configuration

### Environment-Based URL Mapping

```typescript
// src/config/services.ts

type Environment = 'local' | 'docker' | 'staging' | 'production';

const serviceUrls: Record<Environment, Record<string, string>> = {
  local: {
    user: 'http://localhost:3001',
    product: 'http://localhost:3002',
    inventory: 'http://localhost:3003',
    order: 'http://localhost:3004',
    payment: 'http://localhost:3005',
    shipping: 'http://localhost:3006',
    review: 'http://localhost:3007',
    recommendation: 'http://localhost:3008',
    notification: 'http://localhost:3009',
    analytics: 'http://localhost:3010',
  },
  docker: {
    user: 'http://user-service:3001',
    product: 'http://product-service:3002',
    inventory: 'http://inventory-service:3003',
    order: 'http://order-service:3004',
    payment: 'http://payment-service:3005',
    shipping: 'http://shipping-service:3006',
    review: 'http://review-service:3007',
    recommendation: 'http://recommendation-service:3008',
    notification: 'http://notification-service:3009',
    analytics: 'http://analytics-service:3010',
  },
  staging: {
    // API Gateway handles routing
    gateway: 'https://api-staging.shopsphere.com',
  },
  production: {
    gateway: 'https://api.shopsphere.com',
  },
};

// Feature flags for mock vs real
export const serviceStatus: Record<string, boolean> = {
  user: false,      // false = use mock, true = use real
  product: false,
  inventory: false,
  order: false,
  payment: false,
  shipping: false,
  review: false,
  recommendation: false,
  notification: false,
  analytics: false,
};
```

---

## API Contracts by Service

> All endpoints follow the standard response wrapper defined in `shared/contracts/_TEMPLATE-service.yaml`

### Standard Response Format

```typescript
// Success Response
interface ApiResponse<T> {
  success: true;
  data: T;
  meta?: PaginationMeta;
}

// Error Response
interface ErrorResponse {
  success: false;
  error: {
    code: string;
    message: string;
    timestamp: string;
    path: string;
  };
}

interface PaginationMeta {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}
```

---

### 1. User Service (Port 3001)

**Authentication:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login user | Public |
| POST | `/api/auth/logout` | Logout user | Bearer |
| POST | `/api/auth/refresh` | Refresh token | Bearer |
| POST | `/api/auth/verify-email` | Verify email | Public |
| POST | `/api/auth/forgot-password` | Request password reset | Public |
| POST | `/api/auth/reset-password` | Reset password | Public |
| GET | `/api/auth/oauth2/{provider}` | OAuth login (Google, Facebook) | Public |

**User Profile:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/me` | Get current user profile | Bearer |
| PUT | `/api/users/me` | Update profile | Bearer |
| POST | `/api/users/me/avatar` | Upload avatar | Bearer |
| PUT | `/api/users/me/password` | Change password | Bearer |

**Addresses:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/users/me/addresses` | List addresses | Bearer |
| POST | `/api/users/me/addresses` | Add address | Bearer |
| PUT | `/api/users/me/addresses/{id}` | Update address | Bearer |
| DELETE | `/api/users/me/addresses/{id}` | Delete address | Bearer |

**Admin:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/users` | List all users | Admin |
| PUT | `/api/admin/users/{id}/role` | Update user role | Admin |

**Types:**
```typescript
interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar?: string;
  role: 'CUSTOMER' | 'SELLER' | 'ADMIN';
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

interface Address {
  id: string;
  userId: string;
  label: string;
  fullName: string;
  phone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  isDefault: boolean;
}

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
  expiresIn: number;
}

interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
```

---

### 2. Product Service (Port 3002)

**Products:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/products` | Search/list products | Public |
| GET | `/api/products/{id}` | Get product details | Public |
| POST | `/api/products` | Create product | Seller |
| PUT | `/api/products/{id}` | Update product | Seller |
| DELETE | `/api/products/{id}` | Delete product | Seller |

**Categories:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/categories` | List categories | Public |
| GET | `/api/categories/{id}` | Get category | Public |
| GET | `/api/categories/{id}/products` | Products in category | Public |

**Search:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/products/search` | Full-text search | Public |
| POST | `/api/products/visual-search` | Search by image | Public |

**Query Parameters for `/api/products`:**
- `q` - Search query
- `category` - Category ID
- `minPrice` / `maxPrice` - Price range
- `brand` - Brand filter
- `rating` - Minimum rating
- `sort` - `price_asc`, `price_desc`, `rating`, `newest`
- `page` / `limit` - Pagination

**Types:**
```typescript
interface Product {
  id: string;
  name: string;
  slug: string;
  description: string;
  shortDescription: string;
  price: number;
  compareAtPrice?: number;
  images: ProductImage[];
  category: Category;
  brand?: string;
  sku: string;
  variants?: ProductVariant[];
  attributes: Record<string, string>;
  rating: number;
  reviewCount: number;
  status: 'ACTIVE' | 'DRAFT' | 'ARCHIVED';
  sellerId: string;
  createdAt: string;
  updatedAt: string;
}

interface ProductImage {
  id: string;
  url: string;
  alt: string;
  isPrimary: boolean;
}

interface ProductVariant {
  id: string;
  name: string;
  sku: string;
  price: number;
  attributes: Record<string, string>; // { size: 'M', color: 'Blue' }
}

interface Category {
  id: string;
  name: string;
  slug: string;
  parentId?: string;
  image?: string;
  children?: Category[];
}
```

---

### 3. Inventory Service (Port 3003)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/inventory/{productId}` | Check stock | Public |
| GET | `/api/inventory/batch` | Check multiple products | Public |
| POST | `/api/inventory/reserve` | Reserve items | Internal |
| POST | `/api/inventory/release` | Release reservation | Internal |
| POST | `/api/inventory/confirm` | Confirm reservation | Internal |
| GET | `/api/inventory/low-stock` | Low stock alerts | Seller |

**Types:**
```typescript
interface InventoryItem {
  productId: string;
  variantId?: string;
  quantity: number;
  reserved: number;
  available: number;
  warehouse: string;
  lowStockThreshold: number;
  isLowStock: boolean;
}

interface StockCheckRequest {
  items: Array<{
    productId: string;
    variantId?: string;
    quantity: number;
  }>;
}

interface StockCheckResponse {
  items: Array<{
    productId: string;
    variantId?: string;
    requested: number;
    available: number;
    isAvailable: boolean;
  }>;
  allAvailable: boolean;
}
```

---

### 4. Order Service (Port 3004)

**Cart:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/cart` | Get user's cart | Bearer |
| POST | `/api/cart/items` | Add item to cart | Bearer |
| PUT | `/api/cart/items/{itemId}` | Update quantity | Bearer |
| DELETE | `/api/cart/items/{itemId}` | Remove item | Bearer |
| DELETE | `/api/cart` | Clear cart | Bearer |
| POST | `/api/cart/merge` | Merge guest cart | Bearer |
| POST | `/api/cart/validate` | Validate for checkout | Bearer |
| GET | `/api/cart/totals` | Get calculated totals | Bearer |
| POST | `/api/cart/apply-coupon` | Apply coupon | Bearer |
| DELETE | `/api/cart/coupon` | Remove coupon | Bearer |

**Checkout:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/orders/checkout` | Create order | Bearer |

**Orders:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/orders` | List user's orders | Bearer |
| GET | `/api/orders/{id}` | Get order details | Bearer |
| GET | `/api/orders/{id}/status` | Get status history | Bearer |
| POST | `/api/orders/{id}/cancel` | Cancel order | Bearer |

**Admin:**
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/admin/orders` | List all orders | Admin |
| PUT | `/api/admin/orders/{id}/status` | Update status | Admin |

**Types:**
```typescript
interface Cart {
  id: string;
  userId?: string;
  sessionId?: string;
  items: CartItem[];
  subtotal: number;
  itemCount: number;
  appliedCoupon?: AppliedCoupon;
  createdAt: string;
  updatedAt: string;
}

interface CartItem {
  id: string;
  productId: string;
  variantId?: string;
  productName: string;
  productImage: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

interface CartTotals {
  subtotal: number;
  taxAmount: number;
  shippingAmount: number;
  discountAmount: number;
  total: number;
  breakdown: {
    items: number;
    shipping: number;
    tax: number;
    discount: number;
  };
}

interface CheckoutRequest {
  shippingAddressId: string;
  billingAddressId: string;
  paymentMethod: 'card' | 'paypal';
  couponCode?: string;
  notes?: string;
}

interface Order {
  id: string;
  orderNumber: string;
  userId: string;
  status: OrderStatus;
  items: OrderItem[];
  shippingAddress: Address;
  billingAddress: Address;
  subtotal: number;
  taxAmount: number;
  shippingAmount: number;
  discountAmount: number;
  totalAmount: number;
  paymentId?: string;
  paymentStatus: PaymentStatus;
  trackingNumber?: string;
  notes?: string;
  statusHistory: StatusHistoryItem[];
  createdAt: string;
  updatedAt: string;
}

type OrderStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED';

type PaymentStatus =
  | 'PENDING'
  | 'COMPLETED'
  | 'FAILED'
  | 'REFUNDED';

interface OrderItem {
  id: string;
  productId: string;
  productName: string;
  productImage: string;
  variantId?: string;
  variantName?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

interface StatusHistoryItem {
  status: OrderStatus;
  timestamp: string;
  note?: string;
}
```

---

### 5. Payment Service (Port 3005)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/payments/create-intent` | Create payment intent | Bearer |
| POST | `/api/payments/confirm` | Confirm payment | Bearer |
| GET | `/api/payments/{id}` | Get payment details | Bearer |
| POST | `/api/payments/refund` | Process refund | Admin |
| GET | `/api/payments/methods` | List saved payment methods | Bearer |
| POST | `/api/payments/methods` | Save payment method | Bearer |
| DELETE | `/api/payments/methods/{id}` | Remove payment method | Bearer |

**Types:**
```typescript
interface PaymentIntent {
  id: string;
  clientSecret: string;
  amount: number;
  currency: string;
  status: 'pending' | 'processing' | 'succeeded' | 'failed';
  orderId: string;
}

interface CreatePaymentIntentRequest {
  orderId: string;
  amount: number;
  currency: string;
  paymentMethodId?: string;
}

interface PaymentMethod {
  id: string;
  type: 'card' | 'paypal';
  card?: {
    brand: string;
    last4: string;
    expiryMonth: number;
    expiryYear: number;
  };
  isDefault: boolean;
}
```

---

### 6. Shipping Service (Port 3006)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/shipping/calculate-rate` | Get shipping rates | Bearer |
| POST | `/api/shipping/create-label` | Generate shipping label | Internal |
| GET | `/api/shipping/{trackingNumber}` | Track shipment | Public |
| POST | `/api/shipping/validate-address` | Validate address | Bearer |

**Types:**
```typescript
interface ShippingRate {
  carrier: string;
  service: string;
  rate: number;
  currency: string;
  estimatedDays: number;
  deliveryDate: string;
}

interface ShippingRateRequest {
  fromAddress: Address;
  toAddress: Address;
  items: Array<{
    weight: number;
    dimensions: { length: number; width: number; height: number };
  }>;
}

interface TrackingInfo {
  trackingNumber: string;
  carrier: string;
  status: 'pending' | 'in_transit' | 'out_for_delivery' | 'delivered';
  estimatedDelivery: string;
  events: TrackingEvent[];
}

interface TrackingEvent {
  timestamp: string;
  location: string;
  description: string;
  status: string;
}
```

---

### 7. Review Service (Port 3007)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/reviews/product/{productId}` | Get product reviews | Public |
| POST | `/api/reviews` | Create review | Bearer |
| PUT | `/api/reviews/{id}` | Update review | Bearer |
| DELETE | `/api/reviews/{id}` | Delete review | Bearer |
| POST | `/api/reviews/{id}/helpful` | Mark as helpful | Bearer |
| GET | `/api/reviews/user/me` | My reviews | Bearer |

**Types:**
```typescript
interface Review {
  id: string;
  productId: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  rating: number;
  title: string;
  content: string;
  images?: string[];
  helpfulCount: number;
  verifiedPurchase: boolean;
  createdAt: string;
  updatedAt: string;
}

interface CreateReviewRequest {
  productId: string;
  rating: number;
  title: string;
  content: string;
  images?: string[];
}

interface ReviewSummary {
  productId: string;
  averageRating: number;
  totalReviews: number;
  ratingDistribution: {
    1: number;
    2: number;
    3: number;
    4: number;
    5: number;
  };
}
```

---

### 8. Recommendation Service (Port 3008)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/recommendations/for-you` | Personalized recommendations | Bearer |
| GET | `/api/recommendations/similar/{productId}` | Similar products | Public |
| GET | `/api/recommendations/trending` | Trending products | Public |
| GET | `/api/recommendations/recently-viewed` | Recently viewed | Bearer |
| GET | `/api/recommendations/bought-together/{productId}` | Frequently bought together | Public |

**Types:**
```typescript
interface RecommendationSet {
  type: 'personalized' | 'similar' | 'trending' | 'bought_together';
  title: string;
  products: Product[];
}
```

---

### 9. Notification Service (Port 3009)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/notifications` | Get user notifications | Bearer |
| PUT | `/api/notifications/{id}/read` | Mark as read | Bearer |
| PUT | `/api/notifications/read-all` | Mark all as read | Bearer |
| GET | `/api/notifications/preferences` | Get preferences | Bearer |
| PUT | `/api/notifications/preferences` | Update preferences | Bearer |
| GET | `/api/notifications/unread-count` | Get unread count | Bearer |

**Types:**
```typescript
interface Notification {
  id: string;
  type: 'order' | 'promotion' | 'system' | 'review';
  title: string;
  message: string;
  data?: Record<string, any>;
  isRead: boolean;
  createdAt: string;
}

interface NotificationPreferences {
  email: {
    orderUpdates: boolean;
    promotions: boolean;
    reviews: boolean;
  };
  push: {
    orderUpdates: boolean;
    promotions: boolean;
    reviews: boolean;
  };
  sms: {
    orderUpdates: boolean;
  };
}
```

---

### 10. Analytics Service (Port 3010)

> Mostly internal, limited frontend exposure

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/analytics/events` | Track user event | Bearer |
| GET | `/api/admin/analytics/dashboard` | Admin dashboard | Admin |

**Types:**
```typescript
interface AnalyticsEvent {
  event: string;
  properties: Record<string, any>;
  timestamp?: string;
}

// Events to track:
// - page_view
// - product_viewed
// - product_added_to_cart
// - checkout_started
// - order_completed
// - search_performed
```

---

## Mock API Strategy

### MSW (Mock Service Worker) Setup

```typescript
// src/mocks/handlers/order.ts
import { http, HttpResponse } from 'msw';
import { mockCart, mockOrders } from '../data/order';

export const orderHandlers = [
  // Get cart
  http.get('*/api/cart', () => {
    return HttpResponse.json({
      success: true,
      data: mockCart,
    });
  }),

  // Add to cart
  http.post('*/api/cart/items', async ({ request }) => {
    const body = await request.json();
    // Add to mockCart...
    return HttpResponse.json({
      success: true,
      data: mockCart,
    });
  }),

  // List orders
  http.get('*/api/orders', () => {
    return HttpResponse.json({
      success: true,
      data: mockOrders,
      meta: { page: 1, limit: 20, total: mockOrders.length, totalPages: 1 },
    });
  }),
];
```

### Switching Mock ↔ Real

```typescript
// src/lib/api/client.ts
import { serviceStatus, getServiceUrl } from '@/config/services';

export async function apiCall<T>(
  service: string,
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  // If mock mode, MSW will intercept
  // If real mode, call actual service
  const baseUrl = serviceStatus[service]
    ? getServiceUrl(service)
    : ''; // Empty = MSW intercepts

  const response = await fetch(`${baseUrl}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  return response.json();
}
```

---

## Implementation Phases

### Phase 1: Foundation (Week 1-2)
- [ ] Project setup (Next.js, TypeScript, Tailwind)
- [ ] Component library setup (shadcn/ui)
- [ ] Layout components (Header, Footer, Sidebar)
- [ ] Authentication pages (Login, Register, Forgot Password)
- [ ] MSW setup with mock data
- [ ] API client foundation

### Phase 2: Product & Search (Week 3-4)
- [ ] Home page with featured products
- [ ] Product listing with filters
- [ ] Product detail page
- [ ] Category navigation
- [ ] Search functionality
- [ ] Product recommendations section

### Phase 3: Cart & Checkout (Week 5-6)
- [ ] Shopping cart (add, update, remove)
- [ ] Cart sidebar/drawer
- [ ] Checkout flow (multi-step)
- [ ] Address selection/creation
- [ ] Payment method selection
- [ ] Order confirmation

### Phase 4: User Account (Week 7-8)
- [ ] User profile management
- [ ] Address book
- [ ] Order history
- [ ] Order detail & tracking
- [ ] Notification center
- [ ] Settings & preferences

### Phase 5: Reviews & Social (Week 9-10)
- [ ] Product reviews display
- [ ] Write review flow
- [ ] Review images upload
- [ ] Helpful votes
- [ ] Recently viewed products

### Phase 6: Admin Dashboard (Week 11-12)
- [ ] Admin layout
- [ ] Orders management
- [ ] Order status updates
- [ ] User management
- [ ] Basic analytics view

---

## Integration Checklist

When a microservice is ready, follow this checklist:

1. [ ] Team member raises PR to `dev` branch
2. [ ] PR reviewed against contract compliance
3. [ ] PR merged to `dev`
4. [ ] Update `serviceStatus[service] = true` in config
5. [ ] Test against real service
6. [ ] Update `frontend-integration.md` tracker
7. [ ] Remove/disable mock handlers for that service

---

## Related Documents

- `_bmad-output/master/environment-config.md` - Service URL mappings
- `_bmad-output/master/frontend-integration.md` - Integration tracker
- `_bmad-output/planning-artifacts/integration-strategy.md` - Overall strategy
- `shared/contracts/_TEMPLATE-service.yaml` - API contract template
- `shared/event-schemas/_TEMPLATE-events.json` - Event schema template

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Initial specification created | Team Lead |
