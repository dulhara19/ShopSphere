# ShopSphere Frontend Configuration Guide

> **Purpose**: This document serves as a comprehensive map of all configurations, API endpoints, and integration points in the frontend. It enables AI agents and developers to quickly understand and update the frontend as microservices evolve.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Directory Structure](#directory-structure)
3. [Environment Configuration](#environment-configuration)
4. [Service Configuration](#service-configuration)
5. [API Client Layer](#api-client-layer)
6. [State Management](#state-management)
7. [Mock Service Workers (MSW)](#mock-service-workers-msw)
8. [Integration Checklist](#integration-checklist)
9. [Type Definitions](#type-definitions)
10. [Component Architecture](#component-architecture)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Next.js Frontend                          │
├─────────────────────────────────────────────────────────────────┤
│  Pages (App Router)  │  Components  │  Stores (Zustand)         │
├─────────────────────────────────────────────────────────────────┤
│                    TanStack Query (Server State)                 │
├─────────────────────────────────────────────────────────────────┤
│                    API Client Layer (Axios)                      │
├─────────────────────────────────────────────────────────────────┤
│         MSW (Mock)  ←──── Feature Flags ────→  Real APIs        │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                     10 Microservices                             │
│  User │ Product │ Inventory │ Order │ Payment │ Shipping        │
│  Review │ Recommendation │ Notification │ Analytics             │
└─────────────────────────────────────────────────────────────────┘
```

### Key Technologies
- **Framework**: Next.js 14+ (App Router)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS + shadcn/ui
- **State**: Zustand (client) + TanStack Query (server)
- **HTTP Client**: Axios
- **Mocking**: MSW (Mock Service Worker)
- **Validation**: Zod

---

## Directory Structure

```
frontend/
├── src/
│   ├── app/                    # Next.js App Router pages
│   │   ├── (auth)/             # Auth route group
│   │   │   ├── login/
│   │   │   ├── register/
│   │   │   └── forgot-password/
│   │   ├── (shop)/             # Shop route group (public)
│   │   │   ├── products/
│   │   │   ├── cart/
│   │   │   └── checkout/
│   │   ├── (user)/             # User account route group
│   │   │   ├── profile/
│   │   │   ├── orders/
│   │   │   └── settings/
│   │   └── (admin)/            # Admin dashboard route group
│   │       ├── dashboard/
│   │       ├── products/
│   │       └── orders/
│   │
│   ├── components/             # React components
│   │   ├── ui/                 # shadcn/ui base components
│   │   ├── layout/             # Layout components
│   │   ├── products/           # Product-specific components
│   │   ├── cart/               # Cart components
│   │   └── admin/              # Admin components
│   │
│   ├── config/                 # ⭐ CONFIGURATION FILES
│   │   ├── env.ts              # Environment variables
│   │   ├── services.ts         # Service URLs & feature flags
│   │   └── constants.ts        # App constants
│   │
│   ├── lib/                    # Utilities and libraries
│   │   ├── api/                # ⭐ API CLIENT LAYER
│   │   │   ├── client.ts       # Base Axios client
│   │   │   ├── user.ts         # User service API
│   │   │   ├── product.ts      # Product service API
│   │   │   ├── inventory.ts    # Inventory service API
│   │   │   ├── order.ts        # Order service API
│   │   │   ├── payment.ts      # Payment service API
│   │   │   ├── shipping.ts     # Shipping service API
│   │   │   ├── review.ts       # Review service API
│   │   │   ├── recommendation.ts # Recommendation service API
│   │   │   ├── notification.ts # Notification service API
│   │   │   └── analytics.ts    # Analytics service API
│   │   └── utils/              # Utility functions
│   │
│   ├── stores/                 # ⭐ ZUSTAND STORES
│   │   ├── auth-store.ts       # Authentication state
│   │   ├── cart-store.ts       # Shopping cart state
│   │   └── ui-store.ts         # UI state (modals, theme)
│   │
│   ├── types/                  # ⭐ TYPESCRIPT TYPES
│   │   ├── api.ts              # API response wrappers
│   │   ├── user.ts             # User types
│   │   ├── product.ts          # Product types
│   │   ├── cart.ts             # Cart types
│   │   ├── order.ts            # Order types
│   │   ├── payment.ts          # Payment types
│   │   ├── shipping.ts         # Shipping types
│   │   ├── inventory.ts        # Inventory types
│   │   ├── review.ts           # Review types
│   │   ├── notification.ts     # Notification types
│   │   ├── recommendation.ts   # Recommendation types
│   │   └── analytics.ts        # Analytics types
│   │
│   └── mocks/                  # ⭐ MSW MOCK HANDLERS
│       ├── browser.ts          # MSW browser setup
│       ├── data/               # Mock data files
│       │   ├── users.ts
│       │   ├── products.ts
│       │   ├── orders.ts
│       │   └── reviews.ts
│       └── handlers/           # Request handlers
│           ├── user.ts
│           ├── product.ts
│           ├── order.ts
│           ├── review.ts
│           └── recommendation.ts
│
├── .env.local                  # Local environment (git-ignored)
├── .env.example                # Environment template
└── docs/
    ├── plan.md                 # Development plan
    └── FRONTEND-CONFIG.md      # This file
```

---

## Environment Configuration

### File: `.env.local` (create from `.env.example`)

```bash
# ═══════════════════════════════════════════════════════════════
# ENVIRONMENT
# ═══════════════════════════════════════════════════════════════
NEXT_PUBLIC_APP_ENV=development

# ═══════════════════════════════════════════════════════════════
# SERVICE URLs - Update when services are deployed
# ═══════════════════════════════════════════════════════════════
NEXT_PUBLIC_USER_SERVICE_URL=http://localhost:3001
NEXT_PUBLIC_PRODUCT_SERVICE_URL=http://localhost:3002
NEXT_PUBLIC_INVENTORY_SERVICE_URL=http://localhost:3003
NEXT_PUBLIC_ORDER_SERVICE_URL=http://localhost:3004
NEXT_PUBLIC_PAYMENT_SERVICE_URL=http://localhost:3005
NEXT_PUBLIC_SHIPPING_SERVICE_URL=http://localhost:3006
NEXT_PUBLIC_REVIEW_SERVICE_URL=http://localhost:3007
NEXT_PUBLIC_RECOMMENDATION_SERVICE_URL=http://localhost:3008
NEXT_PUBLIC_NOTIFICATION_SERVICE_URL=http://localhost:3009
NEXT_PUBLIC_ANALYTICS_SERVICE_URL=http://localhost:3010

# ═══════════════════════════════════════════════════════════════
# FEATURE FLAGS - Set to 'true' when service is ready
# ═══════════════════════════════════════════════════════════════
NEXT_PUBLIC_ENABLE_USER_SERVICE=false
NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE=false
NEXT_PUBLIC_ENABLE_INVENTORY_SERVICE=false
NEXT_PUBLIC_ENABLE_ORDER_SERVICE=false
NEXT_PUBLIC_ENABLE_PAYMENT_SERVICE=false
NEXT_PUBLIC_ENABLE_SHIPPING_SERVICE=false
NEXT_PUBLIC_ENABLE_REVIEW_SERVICE=false
NEXT_PUBLIC_ENABLE_RECOMMENDATION_SERVICE=false
NEXT_PUBLIC_ENABLE_NOTIFICATION_SERVICE=false
NEXT_PUBLIC_ENABLE_ANALYTICS_SERVICE=false

# ═══════════════════════════════════════════════════════════════
# MOCK SERVICE WORKER
# ═══════════════════════════════════════════════════════════════
NEXT_PUBLIC_ENABLE_MSW=true
```

### File: `src/config/env.ts`

```typescript
// Centralized environment variable access
export const env = {
  appEnv: process.env.NEXT_PUBLIC_APP_ENV || 'development',
  enableMsw: process.env.NEXT_PUBLIC_ENABLE_MSW === 'true',

  services: {
    user: process.env.NEXT_PUBLIC_USER_SERVICE_URL || 'http://localhost:3001',
    product: process.env.NEXT_PUBLIC_PRODUCT_SERVICE_URL || 'http://localhost:3002',
    // ... all 10 services
  },

  enableService: {
    user: process.env.NEXT_PUBLIC_ENABLE_USER_SERVICE === 'true',
    product: process.env.NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE === 'true',
    // ... all 10 services
  },
};
```

---

## Service Configuration

### File: `src/config/services.ts`

This file is the **central hub** for service configuration. When integrating a new service:

```typescript
// Service names (matches API contract names)
export type ServiceName =
  | 'user'
  | 'product'
  | 'inventory'
  | 'order'
  | 'payment'
  | 'shipping'
  | 'review'
  | 'recommendation'
  | 'notification'
  | 'analytics';

// Service URLs mapping
export const serviceUrls: Record<ServiceName, string> = {
  user: env.services.user,
  product: env.services.product,
  inventory: env.services.inventory,
  order: env.services.order,
  payment: env.services.payment,
  shipping: env.services.shipping,
  review: env.services.review,
  recommendation: env.services.recommendation,
  notification: env.services.notification,
  analytics: env.services.analytics,
};

// Service status (true = use real API, false = use mock)
export const serviceStatus: Record<ServiceName, boolean> = {
  user: env.enableService.user,
  product: env.enableService.product,
  inventory: env.enableService.inventory,
  order: env.enableService.order,
  payment: env.enableService.payment,
  shipping: env.enableService.shipping,
  review: env.enableService.review,
  recommendation: env.enableService.recommendation,
  notification: env.enableService.notification,
  analytics: env.enableService.analytics,
};

// Helper functions
export function getServiceUrl(service: ServiceName): string;
export function isServiceEnabled(service: ServiceName): boolean;
export function shouldUseMock(service: ServiceName): boolean;
```

---

## API Client Layer

### Base Client: `src/lib/api/client.ts`

```typescript
// Creates service-specific Axios instances with:
// - Auth token injection
// - Token refresh on 401
// - Standard error handling
export function createServiceClient(service: ServiceName): AxiosInstance;
```

### Service API Files

Each service has its own API file with typed methods:

| Service | File | Key Methods |
|---------|------|-------------|
| **User** | `src/lib/api/user.ts` | `login`, `register`, `getProfile`, `updateProfile`, `getAddresses` |
| **Product** | `src/lib/api/product.ts` | `getProducts`, `getProduct`, `searchProducts`, `getCategories` |
| **Inventory** | `src/lib/api/inventory.ts` | `getInventory`, `checkAvailability`, `reserveStock` |
| **Order** | `src/lib/api/order.ts` | `getCart`, `addToCart`, `checkout`, `getOrders`, `getOrder` |
| **Payment** | `src/lib/api/payment.ts` | `createPaymentIntent`, `processPayment`, `getPaymentMethods` |
| **Shipping** | `src/lib/api/shipping.ts` | `getShippingRates`, `createShipment`, `trackShipment` |
| **Review** | `src/lib/api/review.ts` | `getProductReviews`, `createReview`, `getRatingSummary` |
| **Recommendation** | `src/lib/api/recommendation.ts` | `getPersonalized`, `getSimilarProducts`, `trackEvent` |
| **Notification** | `src/lib/api/notification.ts` | `getNotifications`, `markAsRead`, `getPreferences` |
| **Analytics** | `src/lib/api/analytics.ts` | `getDashboard`, `getSalesAnalytics`, `trackEvent` |

### API Endpoint Reference

#### User Service (Port 3001)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | User login |
| POST | `/api/v1/auth/register` | User registration |
| POST | `/api/v1/auth/refresh` | Refresh token |
| POST | `/api/v1/auth/logout` | User logout |
| POST | `/api/v1/auth/forgot-password` | Request password reset |
| POST | `/api/v1/auth/reset-password` | Reset password |
| GET | `/api/v1/users/me` | Get current user profile |
| PUT | `/api/v1/users/me` | Update user profile |
| GET | `/api/v1/users/me/addresses` | Get user addresses |
| POST | `/api/v1/users/me/addresses` | Add address |
| PUT | `/api/v1/users/me/addresses/:id` | Update address |
| DELETE | `/api/v1/users/me/addresses/:id` | Delete address |

#### Product Service (Port 3002)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/products` | List products (paginated) |
| GET | `/api/v1/products/:id` | Get product details |
| GET | `/api/v1/products/search` | Search products |
| GET | `/api/v1/categories` | List categories |
| GET | `/api/v1/categories/:id` | Get category |
| POST | `/api/v1/products` | Create product (admin) |
| PUT | `/api/v1/products/:id` | Update product (admin) |
| DELETE | `/api/v1/products/:id` | Delete product (admin) |

#### Inventory Service (Port 3003)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/inventory/:productId` | Get stock level |
| POST | `/api/v1/inventory/check` | Bulk availability check |
| POST | `/api/v1/inventory/reserve` | Reserve stock |
| DELETE | `/api/v1/inventory/reserve/:reservationId` | Release reservation |

#### Order Service (Port 3004)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/cart` | Get cart |
| POST | `/api/v1/cart/items` | Add to cart |
| PUT | `/api/v1/cart/items/:id` | Update cart item |
| DELETE | `/api/v1/cart/items/:id` | Remove from cart |
| DELETE | `/api/v1/cart` | Clear cart |
| POST | `/api/v1/cart/merge` | Merge guest cart |
| POST | `/api/v1/checkout` | Process checkout |
| GET | `/api/v1/orders` | List orders |
| GET | `/api/v1/orders/:id` | Get order details |
| POST | `/api/v1/orders/:id/cancel` | Cancel order |

#### Payment Service (Port 3005)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments/intent` | Create payment intent |
| POST | `/api/v1/payments/process` | Process payment |
| GET | `/api/v1/payments/:id` | Get payment status |
| POST | `/api/v1/payments/:id/refund` | Refund payment |
| GET | `/api/v1/payment-methods` | List saved payment methods |
| POST | `/api/v1/payment-methods` | Save payment method |
| DELETE | `/api/v1/payment-methods/:id` | Remove payment method |

#### Shipping Service (Port 3006)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/shipping/rates` | Get shipping rates |
| POST | `/api/v1/shipping/shipments` | Create shipment |
| GET | `/api/v1/shipping/shipments/:id` | Get shipment |
| GET | `/api/v1/shipping/track/:trackingNumber` | Track shipment |

#### Review Service (Port 3007)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/reviews/product/:productId` | Get product reviews |
| GET | `/api/v1/reviews/product/:productId/summary` | Get rating summary |
| POST | `/api/v1/reviews` | Create review |
| PUT | `/api/v1/reviews/:id` | Update review |
| DELETE | `/api/v1/reviews/:id` | Delete review |
| POST | `/api/v1/reviews/:id/helpful` | Mark as helpful |

#### Recommendation Service (Port 3008)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/recommendations/personalized` | Get personalized recommendations |
| GET | `/api/v1/recommendations/similar/:productId` | Get similar products |
| GET | `/api/v1/recommendations/trending` | Get trending products |
| GET | `/api/v1/recommendations/frequently-bought/:productId` | Frequently bought together |
| POST | `/api/v1/recommendations/track` | Track user event |

#### Notification Service (Port 3009)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/notifications` | List notifications |
| PUT | `/api/v1/notifications/:id/read` | Mark as read |
| PUT | `/api/v1/notifications/read-all` | Mark all as read |
| GET | `/api/v1/notifications/preferences` | Get preferences |
| PUT | `/api/v1/notifications/preferences` | Update preferences |

#### Analytics Service (Port 3010)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/analytics/dashboard` | Get dashboard metrics |
| GET | `/api/v1/analytics/sales` | Get sales analytics |
| GET | `/api/v1/analytics/products` | Get product analytics |
| GET | `/api/v1/analytics/customers` | Get customer analytics |
| POST | `/api/v1/analytics/events` | Track event |

---

## State Management

### Zustand Stores

| Store | File | Purpose |
|-------|------|---------|
| **Auth Store** | `src/stores/auth-store.ts` | User authentication, tokens, profile |
| **Cart Store** | `src/stores/cart-store.ts` | Shopping cart with optimistic updates |
| **UI Store** | `src/stores/ui-store.ts` | Mobile nav, modals, theme |

### Auth Store Interface

```typescript
interface AuthStore {
  user: User | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  updateProfile: (data: UpdateProfileRequest) => Promise<void>;
}
```

### Cart Store Interface

```typescript
interface CartStore {
  cart: Cart | null;
  isLoading: boolean;

  fetchCart: () => Promise<void>;
  addItem: (item: AddToCartRequest) => Promise<void>;
  updateItem: (itemId: string, quantity: number) => Promise<void>;
  removeItem: (itemId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  mergeGuestCart: () => Promise<void>;
}
```

---

## Mock Service Workers (MSW)

### Setup: `src/mocks/browser.ts`

MSW intercepts HTTP requests and returns mock data when services are not enabled.

### Mock Data Files

| File | Contents |
|------|----------|
| `src/mocks/data/users.ts` | Mock users, addresses, auth responses |
| `src/mocks/data/products.ts` | Mock products, categories |
| `src/mocks/data/orders.ts` | Mock cart, orders |
| `src/mocks/data/reviews.ts` | Mock reviews, rating summaries |

### Handler Files

Each handler file exports an array of `http` handlers that match the corresponding API endpoints:

```typescript
// src/mocks/handlers/user.ts
export const userHandlers = [
  http.post('*/api/v1/auth/login', async ({ request }) => { ... }),
  http.get('*/api/v1/users/me', async () => { ... }),
  // ...
];
```

### To Disable Mocks for a Service

1. Set `NEXT_PUBLIC_ENABLE_[SERVICE]_SERVICE=true` in `.env.local`
2. The API client will automatically route to the real service URL

---

## Integration Checklist

When a microservice is ready for integration, follow these steps:

### Step 1: Update Environment Variables

```bash
# .env.local
NEXT_PUBLIC_ENABLE_[SERVICE]_SERVICE=true
NEXT_PUBLIC_[SERVICE]_SERVICE_URL=http://your-service-url
```

### Step 2: Verify API Contract Compatibility

Compare the service's actual API with the types in `src/types/[service].ts`:

1. Check request/response shapes match
2. Verify endpoint paths are correct
3. Update types if API contract changed

### Step 3: Test Integration

1. Disable MSW for the service
2. Run frontend locally
3. Test all related features
4. Check error handling

### Step 4: Update Documentation

If API contract changed, update:
1. `src/types/[service].ts` - Type definitions
2. `src/lib/api/[service].ts` - API client methods
3. `src/mocks/handlers/[service].ts` - Mock handlers (for other devs)
4. This document's endpoint tables

### Service Integration Status

| Service | Port | Enabled | Last Updated |
|---------|------|---------|--------------|
| User | 3001 | ❌ Mock | - |
| Product | 3002 | ❌ Mock | - |
| Inventory | 3003 | ❌ Mock | - |
| Order | 3004 | ❌ Mock | - |
| Payment | 3005 | ❌ Mock | - |
| Shipping | 3006 | ❌ Mock | - |
| Review | 3007 | ❌ Mock | - |
| Recommendation | 3008 | ❌ Mock | - |
| Notification | 3009 | ❌ Mock | - |
| Analytics | 3010 | ❌ Mock | - |

---

## Type Definitions

### API Contract to TypeScript Type Mapping

| API Contract | TypeScript File | Key Types |
|--------------|-----------------|-----------|
| `shared/contracts/user-service.yaml` | `src/types/user.ts` | `User`, `Address`, `AuthResponse` |
| `shared/contracts/product-service.yaml` | `src/types/product.ts` | `Product`, `Category`, `ProductImage` |
| `shared/contracts/inventory-service.yaml` | `src/types/inventory.ts` | `InventoryItem`, `StockReservation` |
| `shared/contracts/order-service.yaml` | `src/types/order.ts`, `src/types/cart.ts` | `Order`, `Cart`, `CartItem` |
| `shared/contracts/payment-service.yaml` | `src/types/payment.ts` | `PaymentIntent`, `PaymentMethod` |
| `shared/contracts/shipping-service.yaml` | `src/types/shipping.ts` | `ShippingRate`, `TrackingInfo` |
| `shared/contracts/review-service.yaml` | `src/types/review.ts` | `Review`, `RatingSummary` |
| `shared/contracts/recommendation-service.yaml` | `src/types/recommendation.ts` | `RecommendationSet`, `TrackingEvent` |
| `shared/contracts/notification-service.yaml` | `src/types/notification.ts` | `Notification`, `NotificationPreferences` |
| `shared/contracts/analytics-service.yaml` | `src/types/analytics.ts` | `DashboardMetrics`, `SalesAnalytics` |

---

## Component Architecture

### Route Groups

| Group | Path | Layout | Purpose |
|-------|------|--------|---------|
| `(auth)` | `/login`, `/register`, etc. | Minimal (logo only) | Authentication flows |
| `(shop)` | `/`, `/products`, `/cart` | Full (header, footer) | Public shopping |
| `(user)` | `/profile`, `/orders` | Full + sidebar | User account |
| `(admin)` | `/admin/*` | Admin layout | Dashboard |

### Key Components

| Component | Location | Purpose |
|-----------|----------|---------|
| `Header` | `src/components/layout/header.tsx` | Main navigation, search, cart |
| `Footer` | `src/components/layout/footer.tsx` | Site footer with links |
| `CartDrawer` | `src/components/cart/cart-drawer.tsx` | Slide-out cart preview |
| `ProductCard` | `src/components/products/product-card.tsx` | Product display card |
| `ProductGrid` | `src/components/products/product-grid.tsx` | Responsive product grid |

---

## Quick Reference

### Enable a Service

```bash
# In .env.local
NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE=true
NEXT_PUBLIC_PRODUCT_SERVICE_URL=http://localhost:3002
```

### Add a New API Endpoint

1. Update type in `src/types/[service].ts`
2. Add method in `src/lib/api/[service].ts`
3. Add mock handler in `src/mocks/handlers/[service].ts`

### Add a New Page

1. Create file in appropriate route group
2. Use existing components from `src/components`
3. Use TanStack Query for data fetching
4. Use Zustand stores for client state

### Debug API Calls

1. Check Network tab in browser DevTools
2. MSW logs intercepted requests to console
3. Check `shouldUseMock(service)` returns expected value

---

## Changelog

| Date | Change | Author |
|------|--------|--------|
| 2026-02-15 | Initial documentation created | AI Agent |

---

## Notes for AI Agents

1. **Always check `src/config/services.ts`** first to understand which services are enabled
2. **Type definitions mirror API contracts** - if backend changes, update types first
3. **MSW handlers serve as documentation** for expected API behavior
4. **Feature flags enable incremental integration** - enable one service at a time
5. **The cart store uses optimistic updates** - understand this before modifying cart logic
6. **Auth tokens are stored in localStorage** via Zustand persist middleware
