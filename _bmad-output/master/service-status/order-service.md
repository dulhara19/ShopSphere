---
service: order-service
owner: Team Member 4 (LakshanDulhara)
port: 3004
branch: service/order-service
lastUpdated: 2026-02-14
lastReviewedPR: null
mvpStatus: NOT_STARTED
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# Order Service - Master Status Tracker

> **This is the MASTER copy maintained by Team Lead. Slave copy is at `services/order-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | Order Service |
| Owner | LakshanDulhara (Team Lead) |
| Port | 3004 |
| Branch | `service/order-service` |
| Tech Stack | Spring Boot, Spring Data JPA, PostgreSQL, Redis |
| Database | PostgreSQL (orders), Redis (cart) |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/5 Epics Complete |
| Stories Complete | 0/27 |
| Test Coverage | 0% |
| Contract Compliance | Not Verified |
| Integration Tests | Not Run |

---

## Phase 1 - MVP Epics (Master Copy)

### Epic 1.1: Shopping Cart Management

**Status:** `NOT_STARTED`
**Priority:** Critical
**Dependencies:** User Service, Product Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.1.1 | Add item to cart | `TODO` | - | |
| 1.1.2 | Get user's cart | `TODO` | - | |
| 1.1.3 | Update cart item quantity | `TODO` | - | |
| 1.1.4 | Remove item from cart | `TODO` | - | |
| 1.1.5 | Clear cart | `TODO` | - | |
| 1.1.6 | Cart persistence (Redis/merge) | `TODO` | - | |

**API Endpoints to Implement:**
- [ ] `GET /api/cart` - Get user's cart
- [ ] `POST /api/cart/items` - Add item to cart
- [ ] `PUT /api/cart/items/{itemId}` - Update cart item
- [ ] `DELETE /api/cart/items/{itemId}` - Remove item
- [ ] `DELETE /api/cart` - Clear cart
- [ ] `POST /api/cart/merge` - Merge guest cart

**Acceptance Criteria:**
- [ ] Validates product exists and in stock before adding
- [ ] Creates cart if not exists
- [ ] Updates quantity if item already exists
- [ ] Returns product details (name, price, image) with cart
- [ ] Calculates subtotal correctly
- [ ] Guest cart stored in Redis with session ID
- [ ] Authenticated user cart stored in database
- [ ] Guest cart merges with user cart on login

---

### Epic 1.2: Checkout Process

**Status:** `NOT_STARTED`
**Priority:** Critical
**Dependencies:** Epic 1.1, Inventory Service, Payment Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.2.1 | Validate cart for checkout | `TODO` | - | |
| 1.2.2 | Calculate order totals | `TODO` | - | |
| 1.2.3 | Reserve inventory | `TODO` | - | |
| 1.2.4 | Create order | `TODO` | - | |
| 1.2.5 | Initiate payment | `TODO` | - | |
| 1.2.6 | Complete checkout | `TODO` | - | |

**API Endpoints to Implement:**
- [ ] `POST /api/cart/validate` - Validate cart
- [ ] `GET /api/cart/totals` - Calculate totals
- [ ] `POST /api/orders/checkout` - Create order

**Acceptance Criteria:**
- [ ] Validates all items in stock
- [ ] Validates prices haven't changed
- [ ] Calculates subtotal, tax, shipping, discount
- [ ] Reserves inventory via Inventory Service
- [ ] Handles partial availability
- [ ] Generates unique order number (ORD-YYYY-NNNNNN)
- [ ] Creates order with PENDING status
- [ ] Stores shipping and billing addresses
- [ ] Initiates payment via Payment Service
- [ ] Clears cart after successful checkout
- [ ] Publishes `order.created` event

---

### Epic 1.3: Order Management

**Status:** `NOT_STARTED`
**Priority:** Critical
**Dependencies:** Epic 1.2

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.3.1 | Get order by ID | `TODO` | - | |
| 1.3.2 | List user's orders | `TODO` | - | |
| 1.3.3 | Get order status | `TODO` | - | |
| 1.3.4 | Cancel order (customer) | `TODO` | - | |
| 1.3.5 | Admin: List all orders | `TODO` | - | |

**API Endpoints to Implement:**
- [ ] `GET /api/orders` - List user orders
- [ ] `GET /api/orders/{id}` - Get order details
- [ ] `GET /api/orders/{id}/status` - Get status history
- [ ] `POST /api/orders/{id}/cancel` - Cancel order
- [ ] `GET /api/admin/orders` - Admin list orders

**Acceptance Criteria:**
- [ ] Returns full order details including items
- [ ] Includes shipping/billing addresses
- [ ] Includes status history with timestamps
- [ ] Pagination for list endpoints
- [ ] Filter by status, date
- [ ] Sort by date, amount
- [ ] Cancel only allowed for PENDING/CONFIRMED
- [ ] Releases inventory on cancel
- [ ] Initiates refund if paid
- [ ] Admin can filter by user, order number

---

### Epic 1.4: Order Status Management

**Status:** `NOT_STARTED`
**Priority:** High
**Dependencies:** Epic 1.3, Shipping Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.4.1 | Update order status (internal) | `TODO` | - | |
| 1.4.2 | Mark as processing | `TODO` | - | |
| 1.4.3 | Mark as shipped | `TODO` | - | |
| 1.4.4 | Mark as delivered | `TODO` | - | |
| 1.4.5 | Status transition rules | `TODO` | - | |

**API Endpoints to Implement:**
- [ ] `PUT /internal/orders/{id}/status` - Internal status update

**Status Transitions:**
```
PENDING → CONFIRMED (payment successful)
CONFIRMED → PROCESSING (seller acknowledged)
PROCESSING → SHIPPED (handed to carrier)
SHIPPED → DELIVERED (delivery confirmed)
PENDING/CONFIRMED → CANCELLED (customer/admin)
```

**Acceptance Criteria:**
- [ ] Validates status transitions
- [ ] Records status history with timestamp and updatedBy
- [ ] Publishes appropriate event on each transition
- [ ] Stores tracking number when shipped
- [ ] Updates delivery timestamp when delivered

---

### Epic 1.5: Internal Service Communication

**Status:** `NOT_STARTED`
**Priority:** High
**Dependencies:** Epic 1.3

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.5.1 | Get order for payment | `TODO` | - | |
| 1.5.2 | Get order for shipping | `TODO` | - | |
| 1.5.3 | Order events publishing | `TODO` | - | |

**API Endpoints to Implement:**
- [ ] `GET /internal/orders/{id}` - Internal get order
- [ ] `GET /internal/orders/{id}/shipping-details` - Shipping details

**Events to Publish:**
- [ ] `order.created`
- [ ] `order.confirmed`
- [ ] `order.processing`
- [ ] `order.shipped`
- [ ] `order.delivered`
- [ ] `order.cancelled`
- [ ] `cart.updated`

**Events to Consume:**
- [ ] `payment.completed` → Update to CONFIRMED
- [ ] `payment.failed` → Release inventory
- [ ] `shipping.shipped` → Update to SHIPPED
- [ ] `shipping.delivered` → Update to DELIVERED

---

## Contract Compliance

### API Contract: `shared/contracts/order-service.yaml`

| Endpoint | Contract Defined | Implemented | Tested | Notes |
|----------|------------------|-------------|--------|-------|
| GET /api/cart | Yes | No | No | |
| POST /api/cart/items | Yes | No | No | |
| PUT /api/cart/items/{id} | Yes | No | No | |
| DELETE /api/cart/items/{id} | Yes | No | No | |
| DELETE /api/cart | Yes | No | No | |
| POST /api/cart/merge | Yes | No | No | |
| POST /api/cart/validate | Yes | No | No | |
| GET /api/cart/totals | Yes | No | No | |
| POST /api/orders/checkout | Yes | No | No | |
| GET /api/orders | Yes | No | No | |
| GET /api/orders/{id} | Yes | No | No | |
| GET /api/orders/{id}/status | Yes | No | No | |
| POST /api/orders/{id}/cancel | Yes | No | No | |
| GET /api/admin/orders | Yes | No | No | |
| GET /internal/orders/{id} | Yes | No | No | |
| PUT /internal/orders/{id}/status | Yes | No | No | |
| GET /internal/orders/{id}/shipping-details | Yes | No | No | |

### Event Contract: `shared/event-schemas/order-events.json`

| Event | Schema Defined | Publishing | Consuming | Tested |
|-------|----------------|------------|-----------|--------|
| order.created | Yes | No | N/A | No |
| order.confirmed | Yes | No | N/A | No |
| order.processing | Yes | No | N/A | No |
| order.shipped | Yes | No | N/A | No |
| order.delivered | Yes | No | N/A | No |
| order.cancelled | Yes | No | N/A | No |
| cart.updated | Yes | No | N/A | No |
| payment.completed | Yes | N/A | No | No |
| payment.failed | Yes | N/A | No | No |
| shipping.shipped | Yes | N/A | No | No |
| shipping.delivered | Yes | N/A | No | No |

---

## PR Review History

| PR # | Date | Stories | Decision | Reviewer Notes |
|------|------|---------|----------|----------------|
| - | - | - | - | No PRs reviewed yet |

---

## Issues & Improvements

### Critical Issues
_None yet_

### Improvements Needed
_None yet_

### Technical Debt
_None yet_

---

## Integration Testing

### Dependencies Health

| Service | Status | Last Checked | Notes |
|---------|--------|--------------|-------|
| User Service | Unknown | - | Not tested |
| Product Service | Unknown | - | Not tested |
| Inventory Service | Unknown | - | Not tested |
| Payment Service | Unknown | - | Not tested |
| Shipping Service | Unknown | - | Not tested |

### Integration Test Results

| Test Scenario | Status | Last Run | Notes |
|---------------|--------|----------|-------|
| Add to cart flow | Not Run | - | |
| Checkout flow | Not Run | - | |
| Order cancellation | Not Run | - | |
| Status updates | Not Run | - | |

---

## Frontend Integration

### Endpoints Integrated

| Endpoint | Frontend Component | Status | Notes |
|----------|-------------------|--------|-------|
| - | - | - | Not started |

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
