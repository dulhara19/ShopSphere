---
service: order-service
owner: Team Member 4 (LakshanDulhara)
port: 3004
branch: service/order-service
lastUpdated: 2026-02-20
lastReviewedPR: service/order-service-2026-02-15
mvpStatus: PHASE_1_COMPLETE
integrationStatus: READY_FOR_TESTING
frontendStatus: READY_FOR_INTEGRATION
environment: dev
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
| Tech Stack | Spring Boot 3, Spring Data JPA, PostgreSQL, Redis, RabbitMQ |
| Database | PostgreSQL (orders), Redis (guest carts) |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | **5/5 Epics Complete** |
| Stories Complete | **25/25** |
| Test Coverage | ~70-80% (target: 80%) |
| Contract Compliance | **100%** |
| Event Schema | **Complete** |
| PR Status | **APPROVED** |

---

## Phase 1 - MVP Epics (Master Copy)

### Epic 1.1: Shopping Cart Management - **COMPLETE**

**Status:** `DONE`
**Priority:** Critical
**Dependencies:** User Service, Product Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.1.1 | Add item to cart | `DONE` | service/order-service | POST /api/cart/items |
| 1.1.2 | Get user's cart | `DONE` | service/order-service | GET /api/cart |
| 1.1.3 | Update cart item quantity | `DONE` | service/order-service | PUT /api/cart/items/{id} |
| 1.1.4 | Remove item from cart | `DONE` | service/order-service | DELETE /api/cart/items/{id} |
| 1.1.5 | Clear cart | `DONE` | service/order-service | DELETE /api/cart |
| 1.1.6 | Cart persistence (Redis/merge) | `DONE` | service/order-service | POST /api/cart/merge |

**API Endpoints Implemented:**
- [x] `GET /api/cart` - Get user's cart
- [x] `POST /api/cart/items` - Add item to cart
- [x] `PUT /api/cart/items/{itemId}` - Update cart item
- [x] `DELETE /api/cart/items/{itemId}` - Remove item
- [x] `DELETE /api/cart` - Clear cart
- [x] `POST /api/cart/merge` - Merge guest cart
- [x] `POST /api/cart/coupon` - Apply coupon (bonus)
- [x] `DELETE /api/cart/coupon` - Remove coupon (bonus)

**Acceptance Criteria:**
- [x] Validates product exists and in stock before adding
- [x] Creates cart if not exists
- [x] Updates quantity if item already exists
- [x] Returns product details (name, price, image) with cart
- [x] Calculates subtotal correctly
- [x] Guest cart stored in Redis with session ID
- [x] Authenticated user cart stored in database
- [x] Guest cart merges with user cart on login

---

### Epic 1.2: Checkout Process - **COMPLETE**

**Status:** `DONE`
**Priority:** Critical
**Dependencies:** Epic 1.1, Inventory Service, Payment Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.2.1 | Validate cart for checkout | `DONE` | service/order-service | POST /api/cart/validate |
| 1.2.2 | Calculate order totals | `DONE` | service/order-service | GET /api/cart/totals |
| 1.2.3 | Reserve inventory | `DONE` | service/order-service | Via InventoryService |
| 1.2.4 | Create order | `DONE` | service/order-service | POST /api/orders/checkout |
| 1.2.5 | Initiate payment | `DONE` | service/order-service | Stubbed for Payment Service |
| 1.2.6 | Complete checkout | `DONE` | service/order-service | Full flow |

**API Endpoints Implemented:**
- [x] `POST /api/cart/validate` - Validate cart
- [x] `GET /api/cart/totals` - Calculate totals
- [x] `POST /api/orders/checkout` - Create order

**Acceptance Criteria:**
- [x] Validates all items in stock
- [x] Validates prices haven't changed
- [x] Calculates subtotal, tax, shipping, discount
- [x] Reserves inventory via Inventory Service (stubbed)
- [x] Handles partial availability
- [x] Generates unique order number (ORD-YYYY-NNNNNN)
- [x] Creates order with PENDING status
- [x] Stores shipping and billing addresses
- [x] Initiates payment via Payment Service (stubbed)
- [x] Clears cart after successful checkout
- [x] Publishes `order.created` event

---

### Epic 1.3: Order Management - **COMPLETE**

**Status:** `DONE`
**Priority:** Critical
**Dependencies:** Epic 1.2

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.3.1 | Get order by ID | `DONE` | service/order-service | GET /api/orders/{id} |
| 1.3.2 | List user's orders | `DONE` | service/order-service | GET /api/orders |
| 1.3.3 | Get order status | `DONE` | service/order-service | GET /api/orders/{id}/status |
| 1.3.4 | Cancel order (customer) | `DONE` | service/order-service | POST /api/orders/{id}/cancel |
| 1.3.5 | Admin: List all orders | `DONE` | service/order-service | GET /api/admin/orders |

**API Endpoints Implemented:**
- [x] `GET /api/orders` - List user orders (paginated)
- [x] `GET /api/orders/{id}` - Get order details
- [x] `GET /api/orders/{id}/status` - Get status history
- [x] `POST /api/orders/{id}/cancel` - Cancel order
- [x] `GET /api/admin/orders` - Admin list orders

**Acceptance Criteria:**
- [x] Returns full order details including items
- [x] Includes shipping/billing addresses
- [x] Includes status history with timestamps
- [x] Pagination for list endpoints
- [x] Filter by status, date
- [x] Sort by date, amount
- [x] Cancel only allowed for PENDING/CONFIRMED
- [x] Releases inventory on cancel (stubbed)
- [x] Initiates refund if paid (stubbed)
- [x] Admin can filter by user, order number

---

### Epic 1.4: Order Status Management - **COMPLETE**

**Status:** `DONE`
**Priority:** High
**Dependencies:** Epic 1.3, Shipping Service

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.4.1 | Update order status (internal) | `DONE` | service/order-service | PUT /internal/orders/{id}/status |
| 1.4.2 | Mark as processing | `DONE` | service/order-service | Status transition |
| 1.4.3 | Mark as shipped | `DONE` | service/order-service | With tracking number |
| 1.4.4 | Mark as delivered | `DONE` | service/order-service | With timestamp |
| 1.4.5 | Status transition rules | `DONE` | service/order-service | State machine |

**API Endpoints Implemented:**
- [x] `PUT /internal/orders/{id}/status` - Internal status update

**Status Transitions:**
```
PENDING → CONFIRMED (payment successful)
CONFIRMED → PROCESSING (seller acknowledged)
PROCESSING → SHIPPED (handed to carrier)
SHIPPED → DELIVERED (delivery confirmed)
PENDING/CONFIRMED → CANCELLED (customer/admin)
```

**Acceptance Criteria:**
- [x] Validates status transitions
- [x] Records status history with timestamp and updatedBy
- [x] Publishes appropriate event on each transition
- [x] Stores tracking number when shipped
- [x] Updates delivery timestamp when delivered

---

### Epic 1.5: Internal Service Communication - **COMPLETE**

**Status:** `DONE`
**Priority:** High
**Dependencies:** Epic 1.3

| Story | Description | Status | PR | Notes |
|-------|-------------|--------|-----|-------|
| 1.5.1 | Get order for payment | `DONE` | service/order-service | GET /internal/orders/{id} |
| 1.5.2 | Get order for shipping | `DONE` | service/order-service | GET /internal/orders/{id}/shipping-details |
| 1.5.3 | Order events publishing | `DONE` | service/order-service | RabbitMQ |

**API Endpoints Implemented:**
- [x] `GET /internal/orders/{id}` - Internal get order
- [x] `GET /internal/orders/{id}/shipping-details` - Shipping details

**Events Publishing:**
- [x] `order.created`
- [x] `order.confirmed`
- [x] `order.shipped`
- [x] `order.delivered`
- [x] `order.cancelled`
- [x] `cart.updated`

**Events to Consume (Phase 2):**
- [ ] `payment.completed` → Update to CONFIRMED
- [ ] `payment.failed` → Release inventory
- [ ] `shipping.shipped` → Update to SHIPPED
- [ ] `shipping.delivered` → Update to DELIVERED

---

## Contract Compliance

### API Contract: `shared/contracts/order-service.yaml` - **100% Compliant**

| Endpoint | Contract Defined | Implemented | Tested | Notes |
|----------|------------------|-------------|--------|-------|
| GET /api/cart | Yes | **Yes** | **Yes** | |
| POST /api/cart/items | Yes | **Yes** | **Yes** | |
| PUT /api/cart/items/{id} | Yes | **Yes** | **Yes** | |
| DELETE /api/cart/items/{id} | Yes | **Yes** | **Yes** | |
| DELETE /api/cart | Yes | **Yes** | **Yes** | |
| POST /api/cart/merge | Yes | **Yes** | **Yes** | |
| POST /api/cart/validate | Yes | **Yes** | **Yes** | |
| GET /api/cart/totals | Yes | **Yes** | **Yes** | |
| POST /api/orders/checkout | Yes | **Yes** | **Yes** | |
| GET /api/orders | Yes | **Yes** | **Yes** | |
| GET /api/orders/{id} | Yes | **Yes** | **Yes** | |
| GET /api/orders/{id}/status | Yes | **Yes** | **Yes** | |
| POST /api/orders/{id}/cancel | Yes | **Yes** | **Yes** | |
| GET /api/admin/orders | Yes | **Yes** | **Yes** | |
| GET /internal/orders/{id} | Yes | **Yes** | Partial | |
| PUT /internal/orders/{id}/status | Yes | **Yes** | Partial | |
| GET /internal/orders/{id}/shipping-details | Yes | **Yes** | Partial | |

### Event Contract: `shared/event-schemas/order-events.json` - **Complete**

| Event | Schema Defined | Publishing | Tested |
|-------|----------------|------------|--------|
| order.created | **Yes** | **Yes** | Partial |
| order.confirmed | **Yes** | **Yes** | Partial |
| order.shipped | **Yes** | **Yes** | Partial |
| order.delivered | **Yes** | **Yes** | Partial |
| order.cancelled | **Yes** | **Yes** | Partial |
| cart.updated | **Yes** | **Yes** | Partial |

---

## PR Review History

| PR # | Date | Stories | Decision | Reviewer Notes |
|------|------|---------|----------|----------------|
| service/order-service | 2026-02-15 | 25/25 | **APPROVED** | Exemplary implementation |

---

## Code Quality

### Test Coverage
- Unit Tests: CartServiceTest, CheckoutServiceTest, OrderServiceTest
- Integration Tests: CartController, CheckoutController, OrderController
- Estimated Coverage: ~70-80%

### Best Practices Applied
- [x] Environment variables for secrets
- [x] OpenAPI/Swagger documentation
- [x] Proper exception handling
- [x] Input validation
- [x] Security (JWT + RBAC)
- [x] Clean architecture

---

## Integration Testing

### Dependencies Health

| Service | Status | Last Checked | Notes |
|---------|--------|--------------|-------|
| User Service | Pending | - | Needs auth token |
| Product Service | Pending | - | Needs product data |
| Inventory Service | Pending | - | Stock reservation |
| Payment Service | Pending | - | Payment initiation |
| Shipping Service | Pending | - | Status updates |

### Integration Test Scenarios

| Test Scenario | Status | Last Run | Notes |
|---------------|--------|----------|-------|
| Add to cart flow | Ready | - | Unit tested |
| Checkout flow | Ready | - | Unit tested |
| Order cancellation | Ready | - | Unit tested |
| Status updates | Ready | - | Unit tested |

---

## Frontend Integration

### Endpoints Available for Integration

| Endpoint | Frontend Component | Status | Notes |
|----------|-------------------|--------|-------|
| GET /api/cart | CartPage | Ready | |
| POST /api/cart/items | ProductPage | Ready | |
| PUT /api/cart/items/{id} | CartPage | Ready | |
| DELETE /api/cart/items/{id} | CartPage | Ready | |
| POST /api/cart/validate | CheckoutPage | Ready | |
| GET /api/cart/totals | CheckoutPage | Ready | |
| POST /api/orders/checkout | CheckoutPage | Ready | |
| GET /api/orders | OrdersPage | Ready | |
| GET /api/orders/{id} | OrderDetailsPage | Ready | |

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
| 2026-02-15 | Phase 1 MVP implementation PR submitted | LakshanDulhara |
| 2026-02-15 | PR Reviewed and APPROVED | Master Agent |
| 2026-02-15 | Status updated to PHASE_1_COMPLETE | Master Agent |
| 2026-02-17 | Master sync executed - slave EPICS aligned | Master Agent |
| 2026-02-20 | Master sync executed - no changes detected | Master Agent |
