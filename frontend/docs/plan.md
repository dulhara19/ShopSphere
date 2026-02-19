# ShopSphere Frontend Development Plan

**Created:** 2026-02-15
**Branch:** dev
**Status:** Implementation

---

## Overview

This plan outlines the complete frontend implementation for ShopSphere e-commerce platform. The frontend is developed **contract-first** against API contracts, enabling parallel development with backend microservices.

---

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Framework | Next.js (App Router) | 14.x |
| Language | TypeScript | 5.x |
| Styling | Tailwind CSS | 3.x |
| UI Components | shadcn/ui | latest |
| State Management | Zustand | 4.x |
| Data Fetching | TanStack Query | 5.x |
| Forms | React Hook Form + Zod | latest |
| Mock APIs | MSW (Mock Service Worker) | 2.x |
| HTTP Client | Axios | 1.x |
| Testing | Vitest + Playwright | latest |

---

## Project Structure

```
frontend/
├── public/
│   ├── images/
│   │   ├── placeholder-product.svg
│   │   └── logo.svg
│   └── icons/
├── src/
│   ├── app/                          # Next.js App Router
│   │   ├── (auth)/                   # Auth routes (no header/footer)
│   │   │   ├── login/page.tsx
│   │   │   ├── register/page.tsx
│   │   │   └── forgot-password/page.tsx
│   │   ├── (shop)/                   # Main shop routes
│   │   │   ├── page.tsx              # Home page
│   │   │   ├── products/
│   │   │   │   ├── page.tsx          # Product listing
│   │   │   │   └── [id]/page.tsx     # Product detail
│   │   │   ├── categories/
│   │   │   │   └── [slug]/page.tsx   # Category page
│   │   │   ├── cart/page.tsx         # Cart page
│   │   │   ├── checkout/page.tsx     # Checkout flow
│   │   │   └── search/page.tsx       # Search results
│   │   ├── (user)/                   # User account routes
│   │   │   ├── profile/page.tsx
│   │   │   ├── orders/
│   │   │   │   ├── page.tsx          # Order list
│   │   │   │   └── [id]/page.tsx     # Order detail
│   │   │   ├── addresses/page.tsx
│   │   │   └── settings/page.tsx
│   │   ├── (seller)/                 # Seller dashboard
│   │   │   ├── dashboard/page.tsx
│   │   │   ├── products/page.tsx
│   │   │   └── orders/page.tsx
│   │   ├── (admin)/                  # Admin dashboard
│   │   │   ├── layout.tsx
│   │   │   ├── page.tsx              # Dashboard home
│   │   │   ├── orders/page.tsx
│   │   │   ├── products/page.tsx
│   │   │   ├── users/page.tsx
│   │   │   └── analytics/page.tsx
│   │   ├── layout.tsx                # Root layout
│   │   ├── globals.css
│   │   └── providers.tsx             # Client providers
│   ├── components/
│   │   ├── ui/                       # shadcn/ui components
│   │   ├── layout/
│   │   │   ├── header.tsx
│   │   │   ├── footer.tsx
│   │   │   ├── sidebar.tsx
│   │   │   ├── mobile-nav.tsx
│   │   │   └── user-menu.tsx
│   │   ├── products/
│   │   │   ├── product-card.tsx
│   │   │   ├── product-grid.tsx
│   │   │   ├── product-filters.tsx
│   │   │   ├── product-images.tsx
│   │   │   └── product-info.tsx
│   │   ├── cart/
│   │   │   ├── cart-item.tsx
│   │   │   ├── cart-summary.tsx
│   │   │   └── cart-drawer.tsx
│   │   ├── checkout/
│   │   │   ├── checkout-form.tsx
│   │   │   ├── address-form.tsx
│   │   │   ├── payment-form.tsx
│   │   │   └── order-review.tsx
│   │   ├── orders/
│   │   │   ├── order-card.tsx
│   │   │   ├── order-status.tsx
│   │   │   └── order-timeline.tsx
│   │   ├── reviews/
│   │   │   ├── review-card.tsx
│   │   │   ├── review-form.tsx
│   │   │   └── rating-stars.tsx
│   │   └── common/
│   │       ├── loading.tsx
│   │       ├── error-boundary.tsx
│   │       ├── pagination.tsx
│   │       └── search-bar.tsx
│   ├── lib/
│   │   ├── api/                      # API clients
│   │   │   ├── client.ts             # Base axios config
│   │   │   ├── user.ts
│   │   │   ├── product.ts
│   │   │   ├── inventory.ts
│   │   │   ├── order.ts
│   │   │   ├── payment.ts
│   │   │   ├── shipping.ts
│   │   │   ├── review.ts
│   │   │   ├── recommendation.ts
│   │   │   ├── notification.ts
│   │   │   └── analytics.ts
│   │   ├── hooks/                    # Custom React hooks
│   │   │   ├── use-auth.ts
│   │   │   ├── use-cart.ts
│   │   │   ├── use-products.ts
│   │   │   └── use-orders.ts
│   │   ├── utils/                    # Helper functions
│   │   │   ├── cn.ts
│   │   │   ├── format.ts
│   │   │   └── storage.ts
│   │   └── validations/              # Zod schemas
│   │       ├── auth.ts
│   │       ├── checkout.ts
│   │       └── product.ts
│   ├── stores/                       # Zustand stores
│   │   ├── auth-store.ts
│   │   ├── cart-store.ts
│   │   └── ui-store.ts
│   ├── types/                        # TypeScript types
│   │   ├── index.ts
│   │   ├── api.ts
│   │   ├── user.ts
│   │   ├── product.ts
│   │   ├── order.ts
│   │   ├── cart.ts
│   │   ├── payment.ts
│   │   ├── shipping.ts
│   │   ├── review.ts
│   │   ├── notification.ts
│   │   └── analytics.ts
│   ├── mocks/                        # MSW mock handlers
│   │   ├── handlers/
│   │   │   ├── index.ts
│   │   │   ├── user.ts
│   │   │   ├── product.ts
│   │   │   ├── inventory.ts
│   │   │   ├── order.ts
│   │   │   ├── payment.ts
│   │   │   ├── shipping.ts
│   │   │   ├── review.ts
│   │   │   ├── recommendation.ts
│   │   │   ├── notification.ts
│   │   │   └── analytics.ts
│   │   ├── data/                     # Mock data fixtures
│   │   │   ├── users.ts
│   │   │   ├── products.ts
│   │   │   ├── categories.ts
│   │   │   ├── orders.ts
│   │   │   └── reviews.ts
│   │   ├── browser.ts
│   │   └── server.ts
│   └── config/
│       ├── env.ts                    # Environment config
│       ├── services.ts               # Service URL mapping
│       └── constants.ts              # App constants
├── docs/
│   ├── FRONTEND-SPEC.md
│   ├── plan.md                       # This file
│   └── FRONTEND-CONFIG.md            # Config map for AI agents
├── .env.local
├── .env.example
├── package.json
├── tsconfig.json
├── tailwind.config.ts
├── next.config.js
├── postcss.config.js
└── components.json                   # shadcn/ui config
```

---

## Implementation Steps

### Step 1: Project Foundation

**Files to create:**
- `package.json` - Dependencies and scripts
- `tsconfig.json` - TypeScript configuration
- `next.config.js` - Next.js configuration
- `tailwind.config.ts` - Tailwind configuration
- `postcss.config.js` - PostCSS configuration
- `components.json` - shadcn/ui configuration
- `.env.example` - Environment template

**Key dependencies:**
```json
{
  "dependencies": {
    "next": "^14.2.0",
    "react": "^18.3.0",
    "react-dom": "^18.3.0",
    "@tanstack/react-query": "^5.0.0",
    "zustand": "^4.5.0",
    "axios": "^1.6.0",
    "react-hook-form": "^7.50.0",
    "@hookform/resolvers": "^3.3.0",
    "zod": "^3.22.0",
    "class-variance-authority": "^0.7.0",
    "clsx": "^2.1.0",
    "tailwind-merge": "^2.2.0",
    "lucide-react": "^0.330.0",
    "@radix-ui/react-*": "latest"
  },
  "devDependencies": {
    "typescript": "^5.3.0",
    "tailwindcss": "^3.4.0",
    "postcss": "^8.4.0",
    "autoprefixer": "^10.4.0",
    "msw": "^2.2.0",
    "@types/node": "^20.0.0",
    "@types/react": "^18.2.0"
  }
}
```

---

### Step 2: Configuration Layer

**Files to create:**
- `src/config/env.ts` - Environment variables
- `src/config/services.ts` - Service URLs and feature flags
- `src/config/constants.ts` - Application constants

**Service Configuration:**
```typescript
// Service ports mapping
const SERVICES = {
  user: 3001,
  product: 3002,
  inventory: 3003,
  order: 3004,
  payment: 3005,
  shipping: 3006,
  review: 3007,
  recommendation: 3008,
  notification: 3009,
  analytics: 3010,
};

// Feature flags for mock vs real
const SERVICE_STATUS = {
  user: false,      // false = mock, true = real
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

### Step 3: TypeScript Types

**Files to create:**
- `src/types/api.ts` - API response wrappers
- `src/types/user.ts` - User, Address, Auth types
- `src/types/product.ts` - Product, Category, Image types
- `src/types/order.ts` - Order, Cart types
- `src/types/cart.ts` - Cart item types
- `src/types/payment.ts` - Payment types
- `src/types/shipping.ts` - Shipping types
- `src/types/review.ts` - Review types
- `src/types/notification.ts` - Notification types
- `src/types/analytics.ts` - Analytics event types
- `src/types/index.ts` - Re-exports

---

### Step 4: API Client Layer

**Files to create:**
- `src/lib/api/client.ts` - Axios instance with interceptors
- Service-specific clients for all 10 services

**API Client Pattern:**
```typescript
// Each service client exports typed functions
export const userApi = {
  login: (data: LoginRequest) => api.post<AuthResponse>('/api/auth/login', data),
  register: (data: RegisterRequest) => api.post<AuthResponse>('/api/auth/register', data),
  getProfile: () => api.get<User>('/api/users/me'),
  // ... more endpoints
};
```

---

### Step 5: State Management (Zustand)

**Files to create:**
- `src/stores/auth-store.ts` - Authentication state
- `src/stores/cart-store.ts` - Shopping cart state
- `src/stores/ui-store.ts` - UI state (modals, drawers)

**Store Features:**
- Persist auth tokens to localStorage
- Sync cart with server
- Handle optimistic updates

---

### Step 6: Mock Service Worker

**Files to create:**
- `src/mocks/browser.ts` - Browser MSW setup
- `src/mocks/server.ts` - SSR MSW setup
- `src/mocks/handlers/` - Handler files for each service
- `src/mocks/data/` - Mock data fixtures

**Mock Strategy:**
- Generate realistic test data
- Simulate delays for loading states
- Handle error scenarios

---

### Step 7: UI Components (shadcn/ui)

**shadcn/ui components to install:**
- button, input, label, textarea
- card, badge, avatar
- dialog, sheet, dropdown-menu
- select, checkbox, radio-group
- tabs, accordion
- table, pagination
- toast, alert
- skeleton, spinner
- form components

---

### Step 8: Layout Components

**Files to create:**
- `src/components/layout/header.tsx` - Main navigation
- `src/components/layout/footer.tsx` - Site footer
- `src/components/layout/sidebar.tsx` - Admin/seller sidebar
- `src/components/layout/mobile-nav.tsx` - Mobile navigation
- `src/components/layout/user-menu.tsx` - User dropdown

---

### Step 9: Feature Components

**Products:**
- ProductCard - Product display card
- ProductGrid - Grid layout with responsive columns
- ProductFilters - Category, price, brand filters
- ProductImages - Image gallery with zoom
- ProductInfo - Name, price, description, add to cart

**Cart:**
- CartItem - Single cart item row
- CartSummary - Subtotal, taxes, shipping, total
- CartDrawer - Slide-out cart preview

**Checkout:**
- CheckoutForm - Multi-step checkout
- AddressForm - Shipping/billing address
- PaymentForm - Payment method selection (Stripe)
- OrderReview - Final review before payment

**Orders:**
- OrderCard - Order summary card
- OrderStatus - Status badge
- OrderTimeline - Status history timeline

**Reviews:**
- ReviewCard - Single review display
- ReviewForm - Write review form
- RatingStars - Star rating component

---

### Step 10: Page Implementation

**Auth Pages:**
- `/login` - Email/password login
- `/register` - New user registration
- `/forgot-password` - Password reset request

**Shop Pages:**
- `/` (home) - Featured products, categories, recommendations
- `/products` - Product listing with filters
- `/products/[id]` - Product detail with reviews
- `/categories/[slug]` - Category page
- `/cart` - Shopping cart
- `/checkout` - Checkout flow
- `/search` - Search results

**User Pages:**
- `/profile` - User profile management
- `/orders` - Order history
- `/orders/[id]` - Order detail with tracking
- `/addresses` - Address book
- `/settings` - Notification preferences

**Admin Pages:**
- `/admin` - Dashboard overview
- `/admin/orders` - Order management
- `/admin/products` - Product management
- `/admin/users` - User management
- `/admin/analytics` - Analytics dashboard

---

## API Integration Points

### User Service (Port 3001)
| Endpoint | Frontend Usage |
|----------|----------------|
| POST /api/auth/login | Login page |
| POST /api/auth/register | Register page |
| POST /api/auth/refresh | Auto-refresh tokens |
| GET /api/users/me | User profile, header |
| PUT /api/users/me | Profile edit |
| GET /api/users/me/addresses | Address book, checkout |

### Product Service (Port 3002)
| Endpoint | Frontend Usage |
|----------|----------------|
| GET /api/products | Home, product listing |
| GET /api/products/{id} | Product detail |
| GET /api/categories | Navigation, filters |
| GET /api/products?search= | Search results |

### Order Service (Port 3004)
| Endpoint | Frontend Usage |
|----------|----------------|
| GET /api/cart | Cart page, cart drawer |
| POST /api/cart/items | Add to cart |
| PUT /api/cart/items/{id} | Update quantity |
| DELETE /api/cart/items/{id} | Remove item |
| POST /api/orders/checkout | Checkout |
| GET /api/orders | Order history |
| GET /api/orders/{id} | Order detail |

### Payment Service (Port 3005)
| Endpoint | Frontend Usage |
|----------|----------------|
| POST /api/payments/create-intent | Checkout payment |
| GET /api/payments/methods | Saved payment methods |

### Review Service (Port 3007)
| Endpoint | Frontend Usage |
|----------|----------------|
| GET /api/reviews/product/{id} | Product detail |
| POST /api/reviews | Write review |

### Recommendation Service (Port 3008)
| Endpoint | Frontend Usage |
|----------|----------------|
| GET /api/recommendations/for-you | Home page |
| GET /api/recommendations/similar/{id} | Product detail |
| GET /api/recommendations/trending | Home page |

### Notification Service (Port 3009)
| Endpoint | Frontend Usage |
|----------|----------------|
| GET /api/notifications | Notification center |
| GET /api/notifications/unread-count | Header badge |

---

## Integration Workflow

When a backend service becomes ready:

1. Update `src/config/services.ts`:
   ```typescript
   serviceStatus.user = true; // Enable real API
   ```

2. Test against real endpoints

3. Update `FRONTEND-CONFIG.md` documentation

4. Remove/comment out corresponding MSW handlers

---

## File Count Summary

| Category | Count |
|----------|-------|
| Config files | 8 |
| Type definitions | 12 |
| API clients | 11 |
| Zustand stores | 3 |
| MSW handlers | 12 |
| Mock data | 5 |
| UI components (shadcn) | ~25 |
| Custom components | ~30 |
| Pages | ~20 |
| **Total files** | **~126** |

---

## Success Criteria

- [ ] All pages render without errors
- [ ] MSW mocks return realistic data
- [ ] Cart state persists across sessions
- [ ] Auth flow works end-to-end (mock)
- [ ] Responsive design on mobile/tablet/desktop
- [ ] TypeScript has no compilation errors
- [ ] Ready to swap any mock for real API

---

## Related Documents

- `FRONTEND-SPEC.md` - Detailed API specifications
- `FRONTEND-CONFIG.md` - Configuration map for AI agents
- `shared/contracts/*.yaml` - OpenAPI contracts
