---
documentType: frontend-integration
projectName: ShopSphere
maintainer: LakshanDulhara (Team Lead / Frontend Owner)
lastUpdated: 2026-02-14
frontendFramework: React 18 + TypeScript
---

# ShopSphere Frontend Integration Tracker

> **Purpose:** Track which backend APIs are integrated into the frontend, feature flags, and integration status.

## Frontend Overview

| Property | Value |
|----------|-------|
| Framework | React 18 + TypeScript |
| Location | `frontend/` |
| State Management | Redux Toolkit / Zustand |
| UI Framework | Tailwind CSS + Headless UI |
| API Client | Axios |
| Owner | Team Lead (LakshanDulhara) |

---

## Integration Status Dashboard

### Overall Progress

| Module | Endpoints | Integrated | Tested | Production |
|--------|-----------|------------|--------|------------|
| Authentication | 0 | 0 | 0 | 0 |
| User Profile | 0 | 0 | 0 | 0 |
| Products | 0 | 0 | 0 | 0 |
| Shopping Cart | 0 | 0 | 0 | 0 |
| Checkout | 0 | 0 | 0 | 0 |
| Orders | 0 | 0 | 0 | 0 |
| Payments | 0 | 0 | 0 | 0 |
| Shipping | 0 | 0 | 0 | 0 |
| Reviews | 0 | 0 | 0 | 0 |
| Recommendations | 0 | 0 | 0 | 0 |
| Notifications | 0 | 0 | 0 | 0 |
| **TOTAL** | **0** | **0** | **0** | **0** |

---

## Feature Flags

> Enable/disable features based on backend service availability

```typescript
// frontend/src/config/features.ts
export const FEATURE_FLAGS = {
  // Authentication Module
  AUTH_LOGIN: false,
  AUTH_REGISTER: false,
  AUTH_OAUTH_GOOGLE: false,
  AUTH_OAUTH_FACEBOOK: false,
  AUTH_PASSWORD_RESET: false,

  // User Module
  USER_PROFILE: false,
  USER_ADDRESSES: false,
  USER_AVATAR: false,

  // Product Module
  PRODUCT_CATALOG: false,
  PRODUCT_SEARCH: false,
  PRODUCT_CATEGORIES: false,
  PRODUCT_VARIANTS: false,

  // Cart Module
  CART_VIEW: false,
  CART_ADD: false,
  CART_UPDATE: false,
  CART_REMOVE: false,
  CART_MERGE: false,

  // Checkout Module
  CHECKOUT_FLOW: false,
  CHECKOUT_VALIDATION: false,
  CHECKOUT_ADDRESS: false,
  CHECKOUT_PAYMENT: false,

  // Order Module
  ORDER_HISTORY: false,
  ORDER_DETAILS: false,
  ORDER_TRACKING: false,
  ORDER_CANCEL: false,

  // Payment Module
  PAYMENT_CARD: false,
  PAYMENT_PAYPAL: false,

  // Shipping Module
  SHIPPING_RATES: false,
  SHIPPING_TRACKING: false,

  // Review Module
  REVIEW_VIEW: false,
  REVIEW_CREATE: false,
  REVIEW_RATINGS: false,

  // Recommendation Module
  RECOMMENDATIONS_FOR_YOU: false,
  RECOMMENDATIONS_SIMILAR: false,
  RECOMMENDATIONS_TRENDING: false,

  // Notification Module
  NOTIFICATIONS_PUSH: false,
  NOTIFICATIONS_REALTIME: false,
};
```

---

## API Integration Details

### 1. User Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/auth/register` | POST | `pages/Register.tsx` | NOT_STARTED | |
| `/api/auth/login` | POST | `pages/Login.tsx` | NOT_STARTED | |
| `/api/auth/refresh` | POST | `api/authClient.ts` | NOT_STARTED | |
| `/api/auth/logout` | POST | `components/Header.tsx` | NOT_STARTED | |
| `/api/users/me` | GET | `pages/Profile.tsx` | NOT_STARTED | |
| `/api/users/me` | PUT | `pages/Profile.tsx` | NOT_STARTED | |
| `/api/users/me/addresses` | GET | `pages/Addresses.tsx` | NOT_STARTED | |
| `/api/users/me/addresses` | POST | `pages/Addresses.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `AUTH_*`, `USER_*`

---

### 2. Product Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/products` | GET | `pages/ProductList.tsx` | NOT_STARTED | |
| `/api/products/{id}` | GET | `pages/ProductDetail.tsx` | NOT_STARTED | |
| `/api/products/search` | GET | `components/SearchBar.tsx` | NOT_STARTED | |
| `/api/categories` | GET | `components/CategoryNav.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `PRODUCT_*`

---

### 3. Order Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/cart` | GET | `pages/Cart.tsx` | NOT_STARTED | |
| `/api/cart/items` | POST | `components/AddToCart.tsx` | NOT_STARTED | |
| `/api/cart/items/{id}` | PUT | `pages/Cart.tsx` | NOT_STARTED | |
| `/api/cart/items/{id}` | DELETE | `pages/Cart.tsx` | NOT_STARTED | |
| `/api/cart/validate` | POST | `pages/Checkout.tsx` | NOT_STARTED | |
| `/api/cart/totals` | GET | `pages/Checkout.tsx` | NOT_STARTED | |
| `/api/orders/checkout` | POST | `pages/Checkout.tsx` | NOT_STARTED | |
| `/api/orders` | GET | `pages/OrderHistory.tsx` | NOT_STARTED | |
| `/api/orders/{id}` | GET | `pages/OrderDetail.tsx` | NOT_STARTED | |
| `/api/orders/{id}/status` | GET | `pages/OrderTracking.tsx` | NOT_STARTED | |
| `/api/orders/{id}/cancel` | POST | `pages/OrderDetail.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `CART_*`, `CHECKOUT_*`, `ORDER_*`

---

### 4. Payment Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/payments/create-intent` | POST | `pages/Checkout.tsx` | NOT_STARTED | |
| `/api/payments/confirm` | POST | `pages/Checkout.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `PAYMENT_*`

---

### 5. Shipping Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/shipping/calculate-rate` | POST | `pages/Checkout.tsx` | NOT_STARTED | |
| `/api/shipping/{tracking}` | GET | `pages/OrderTracking.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `SHIPPING_*`

---

### 6. Review Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/reviews/product/{id}` | GET | `pages/ProductDetail.tsx` | NOT_STARTED | |
| `/api/reviews` | POST | `components/ReviewForm.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `REVIEW_*`

---

### 7. Recommendation Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| `/api/recommendations/for-you` | GET | `pages/Home.tsx` | NOT_STARTED | |
| `/api/recommendations/similar/{id}` | GET | `pages/ProductDetail.tsx` | NOT_STARTED | |
| `/api/recommendations/trending` | GET | `pages/Home.tsx` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `RECOMMENDATIONS_*`

---

### 8. Notification Service Integration

| Endpoint | Method | Frontend Location | Status | Notes |
|----------|--------|-------------------|--------|-------|
| WebSocket `/ws/notifications` | WS | `hooks/useNotifications.ts` | NOT_STARTED | |

**Backend Ready:** NO
**Feature Flag:** `NOTIFICATIONS_*`

---

## Integration Workflow

### When Backend Service Becomes Ready:

1. **Team Member** completes MVP and raises PR
2. **Master Agent** reviews and approves PR
3. **Master Agent** updates service status file → `mvpStatus: MVP_COMPLETE`
4. **Team Lead** receives notification
5. **Team Lead** integrates endpoints into frontend:
   - Create/update API client in `frontend/src/api/`
   - Create/update components
   - Enable feature flag
   - Test integration
6. **Team Lead** updates this tracker
7. **Team Lead** updates `environment-config.md` service status

---

## Frontend File Structure

```
frontend/
├── src/
│   ├── api/
│   │   ├── client.ts           # Base Axios instance
│   │   ├── userApi.ts          # User Service client
│   │   ├── productApi.ts       # Product Service client
│   │   ├── orderApi.ts         # Order Service client
│   │   ├── cartApi.ts          # Cart endpoints
│   │   ├── paymentApi.ts       # Payment Service client
│   │   ├── shippingApi.ts      # Shipping Service client
│   │   ├── reviewApi.ts        # Review Service client
│   │   └── recommendationApi.ts # Recommendation client
│   ├── config/
│   │   ├── api.config.ts       # Environment URLs
│   │   └── features.ts         # Feature flags
│   ├── components/
│   │   ├── common/
│   │   ├── auth/
│   │   ├── products/
│   │   ├── cart/
│   │   ├── checkout/
│   │   └── orders/
│   ├── pages/
│   │   ├── Home.tsx
│   │   ├── Login.tsx
│   │   ├── Register.tsx
│   │   ├── ProductList.tsx
│   │   ├── ProductDetail.tsx
│   │   ├── Cart.tsx
│   │   ├── Checkout.tsx
│   │   ├── OrderHistory.tsx
│   │   ├── OrderDetail.tsx
│   │   └── Profile.tsx
│   ├── store/
│   │   ├── authSlice.ts
│   │   ├── cartSlice.ts
│   │   └── orderSlice.ts
│   └── hooks/
│       ├── useAuth.ts
│       ├── useCart.ts
│       └── useNotifications.ts
└── package.json
```

---

## API Client Template

```typescript
// frontend/src/api/orderApi.ts
import { apiClient } from './client';
import { Cart, Order, CheckoutRequest } from '../types';

export const orderApi = {
  // Cart endpoints
  getCart: () =>
    apiClient.get<Cart>('/api/cart'),

  addToCart: (productId: string, quantity: number) =>
    apiClient.post<Cart>('/api/cart/items', { productId, quantity }),

  updateCartItem: (itemId: string, quantity: number) =>
    apiClient.put<Cart>(`/api/cart/items/${itemId}`, { quantity }),

  removeCartItem: (itemId: string) =>
    apiClient.delete<Cart>(`/api/cart/items/${itemId}`),

  validateCart: () =>
    apiClient.post('/api/cart/validate'),

  getCartTotals: (shippingAddressId?: string) =>
    apiClient.get('/api/cart/totals', { params: { shippingAddressId } }),

  // Order endpoints
  checkout: (data: CheckoutRequest) =>
    apiClient.post<Order>('/api/orders/checkout', data),

  getOrders: (page = 1, limit = 20) =>
    apiClient.get<Order[]>('/api/orders', { params: { page, limit } }),

  getOrder: (orderId: string) =>
    apiClient.get<Order>(`/api/orders/${orderId}`),

  getOrderStatus: (orderId: string) =>
    apiClient.get(`/api/orders/${orderId}/status`),

  cancelOrder: (orderId: string, reason: string) =>
    apiClient.post(`/api/orders/${orderId}/cancel`, { reason }),
};
```

---

## Integration Checklist (Per Service)

Use this checklist when integrating a new service:

```markdown
## Integration Checklist: {Service Name}

### Pre-Integration
- [ ] Service MVP complete and approved
- [ ] API contract in `shared/contracts/`
- [ ] Service running and healthy
- [ ] Service status updated in master registry

### API Client
- [ ] Create `frontend/src/api/{service}Api.ts`
- [ ] Types defined in `frontend/src/types/`
- [ ] Error handling implemented
- [ ] Loading states handled

### Components
- [ ] Create/update necessary components
- [ ] Connect to API client
- [ ] Handle loading/error states
- [ ] Implement feature flag checks

### Testing
- [ ] Unit tests for API client
- [ ] Component tests
- [ ] Integration tests (E2E)
- [ ] Manual testing complete

### Documentation
- [ ] Update this tracker
- [ ] Enable feature flags
- [ ] Update environment config
```

---

## Change Log

| Date | Service | Change | By |
|------|---------|--------|-----|
| 2026-02-14 | - | Frontend integration tracker created | Lead |
