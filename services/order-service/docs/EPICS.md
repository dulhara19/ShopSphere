# Order Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Order Service, divided into two phases:
- **Phase 1 (MVP)**: Core cart and checkout functionality
- **Phase 2**: Returns, group buying, and advanced features

**Owner:** Team Member 4
**Port:** 3004
**Tech Stack:** Spring Boot, Spring Data JPA, PostgreSQL, Redis

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential shopping cart and order management capabilities.

### Epic 1.1: Shopping Cart Management

**Priority:** Critical
**Dependency:** User Service, Product Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Add item to cart | - Validate product exists and in stock<br>- Create cart if not exists<br>- Update quantity if item exists |
| 1.1.2 | Get user's cart | - Return all cart items<br>- Include product details (name, price, image)<br>- Calculate subtotal |
| 1.1.3 | Update cart item quantity | - Validate stock availability<br>- Remove if quantity = 0 |
| 1.1.4 | Remove item from cart | - Delete cart item<br>- Return updated cart |
| 1.1.5 | Clear cart | - Remove all items<br>- After checkout or manual clear |
| 1.1.6 | Cart persistence | - Store in Redis for guests<br>- Merge guest cart on login |

**API Endpoints:**
```
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{itemId}
DELETE /api/cart/items/{itemId}
DELETE /api/cart
POST   /api/cart/merge    (merge guest cart after login)
```

**Cart Entity Fields:**
```
- id (UUID)
- userId (nullable for guests)
- sessionId (for guests)
- items[]
  - productId
  - quantity
  - priceAtAdd
- createdAt
- updatedAt
```

---

### Epic 1.2: Checkout Process

**Priority:** Critical
**Dependency:** Epic 1.1, Inventory Service, Payment Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Validate cart for checkout | - Check all items in stock<br>- Validate prices haven't changed<br>- Return validation result |
| 1.2.2 | Calculate order totals | - Subtotal, tax, shipping<br>- Apply discounts/coupons<br>- Return breakdown |
| 1.2.3 | Reserve inventory | - Call Inventory Service<br>- Handle partial availability<br>- Return reservation IDs |
| 1.2.4 | Create order | - Generate order number<br>- Save order with PENDING status<br>- Store shipping address |
| 1.2.5 | Initiate payment | - Call Payment Service<br>- Handle payment intent creation |
| 1.2.6 | Complete checkout | - Confirm inventory reservation<br>- Update order status to CONFIRMED<br>- Clear cart<br>- Send confirmation event |

**API Endpoints:**
```
POST /api/cart/validate
GET  /api/cart/totals?shippingAddressId={id}
POST /api/orders/checkout
     Body: {
       "shippingAddressId": "xxx",
       "billingAddressId": "xxx",
       "paymentMethod": "card",
       "couponCode": "SAVE10"
     }
```

---

### Epic 1.3: Order Management

**Priority:** Critical
**Dependency:** Epic 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Get order by ID | - Return full order details<br>- Include items, addresses, status history |
| 1.3.2 | List user's orders | - Paginated results<br>- Filter by status<br>- Sort by date |
| 1.3.3 | Get order status | - Current status<br>- Status history with timestamps |
| 1.3.4 | Cancel order (customer) | - Only if status is PENDING or CONFIRMED<br>- Release inventory reservation<br>- Initiate refund if paid |
| 1.3.5 | Admin: List all orders | - Filter by status, date, user<br>- Search by order number |

**API Endpoints:**
```
GET  /api/orders
GET  /api/orders/{id}
GET  /api/orders/{id}/status
POST /api/orders/{id}/cancel
GET  /api/admin/orders        (Admin)
```

**Order Entity Fields:**
```
- id (UUID)
- orderNumber (human-readable)
- userId
- status (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- items[]
  - productId
  - productName
  - quantity
  - unitPrice
  - totalPrice
- shippingAddress
- billingAddress
- subtotal
- taxAmount
- shippingAmount
- discountAmount
- totalAmount
- paymentId
- paymentStatus
- notes
- createdAt
- updatedAt
```

---

### Epic 1.4: Order Status Management

**Priority:** High
**Dependency:** Epic 1.3, Shipping Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Update order status (internal) | - Validate status transition<br>- Record status history<br>- Publish status change event |
| 1.4.2 | Mark as processing | - After payment confirmed<br>- Notify seller |
| 1.4.3 | Mark as shipped | - Receive from Shipping Service<br>- Include tracking number |
| 1.4.4 | Mark as delivered | - Receive from Shipping Service<br>- Update delivery timestamp |
| 1.4.5 | Status transition rules | - PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED<br>- PENDING/CONFIRMED → CANCELLED |

**Internal API Endpoints:**
```
PUT /internal/orders/{id}/status
    Body: { "status": "SHIPPED", "trackingNumber": "xxx" }
```

**Status Transitions:**
```
PENDING → CONFIRMED (payment successful)
CONFIRMED → PROCESSING (seller acknowledged)
PROCESSING → SHIPPED (handed to carrier)
SHIPPED → DELIVERED (delivery confirmed)
PENDING/CONFIRMED → CANCELLED (customer/admin)
```

---

### Epic 1.5: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Get order for payment | - Return order total, items<br>- For Payment Service |
| 1.5.2 | Get order for shipping | - Return items, addresses<br>- For Shipping Service |
| 1.5.3 | Order events publishing | - order.created, order.confirmed<br>- order.shipped, order.delivered |

**Internal API Endpoints:**
```
GET /internal/orders/{id}
GET /internal/orders/{id}/shipping-details
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add returns, refunds, group buying, and advanced order features.

### Epic 2.1: Order Cancellation & Refunds

**Priority:** High
**Dependency:** Phase 1, Payment Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Request cancellation | - Customer can request before shipping<br>- Reason required |
| 2.1.2 | Process cancellation | - Release inventory<br>- Initiate refund<br>- Update status |
| 2.1.3 | Partial cancellation | - Cancel specific items<br>- Recalculate totals<br>- Partial refund |
| 2.1.4 | Admin force cancel | - Override restrictions<br>- Full audit trail |

**API Endpoints:**
```
POST /api/orders/{id}/cancel-request
POST /api/orders/{id}/items/{itemId}/cancel
POST /api/admin/orders/{id}/force-cancel
```

---

### Epic 2.2: Returns & Exchanges

**Priority:** High
**Dependency:** Phase 1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Request return | - Within return window (e.g., 30 days)<br>- Select items and reason<br>- Generate return label |
| 2.2.2 | Return status tracking | - REQUESTED, APPROVED, SHIPPED, RECEIVED, REFUNDED |
| 2.2.3 | Process return | - Inspect returned items<br>- Approve/reject refund<br>- Update inventory |
| 2.2.4 | Exchange request | - Return for different variant<br>- Handle price differences |
| 2.2.5 | Return analytics | - Return rate by product<br>- Common return reasons |

**API Endpoints:**
```
POST /api/orders/{id}/returns
GET  /api/orders/{id}/returns
GET  /api/returns/{returnId}
PUT  /api/returns/{returnId}/status   (Admin)
POST /api/orders/{id}/exchanges
```

---

### Epic 2.3: Discount & Coupon System

**Priority:** Medium
**Dependency:** Epic 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Create coupon (Admin) | - Percentage or fixed amount<br>- Min order amount<br>- Expiry date |
| 2.3.2 | Apply coupon to cart | - Validate coupon<br>- Check eligibility<br>- Calculate discount |
| 2.3.3 | Remove coupon | - Recalculate totals |
| 2.3.4 | Coupon usage tracking | - Max uses total<br>- Max uses per user |
| 2.3.5 | Auto-apply promotions | - Site-wide sales<br>- Category discounts |

**API Endpoints:**
```
POST   /api/admin/coupons
GET    /api/admin/coupons
PUT    /api/admin/coupons/{id}
DELETE /api/admin/coupons/{id}
POST   /api/cart/apply-coupon
DELETE /api/cart/coupon
```

---

### Epic 2.4: Group Buying

**Priority:** Low
**Dependency:** Phase 1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Create group buy | - Set target participants<br>- Set discount tiers<br>- Set time limit |
| 2.4.2 | Join group buy | - Link to existing group<br>- Track participant count |
| 2.4.3 | Group buy success | - Reached target → apply discount<br>- Process all orders |
| 2.4.4 | Group buy failure | - Didn't reach target<br>- Cancel/refund all orders |
| 2.4.5 | Share group buy | - Generate shareable link<br>- Track referrals |

**API Endpoints:**
```
POST /api/group-buy
GET  /api/group-buy/{id}
POST /api/group-buy/{id}/join
GET  /api/group-buy/active
```

---

### Epic 2.5: Order Notes & Communication

**Priority:** Low
**Dependency:** Phase 1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Add order notes (customer) | - Gift message<br>- Delivery instructions |
| 2.5.2 | Internal notes (seller/admin) | - Not visible to customer<br>- Audit trail |
| 2.5.3 | Order messaging | - Customer-seller communication<br>- Tied to order |

**API Endpoints:**
```
POST /api/orders/{id}/notes
GET  /api/orders/{id}/notes
POST /api/orders/{id}/messages
GET  /api/orders/{id}/messages
```

---

## Definition of Done (DoD)

Each story is considered done when:
- [ ] Code implemented and follows coding standards
- [ ] Unit tests written (minimum 80% coverage)
- [ ] Integration tests for API endpoints
- [ ] API documented in OpenAPI/Swagger
- [ ] Code reviewed and approved
- [ ] No critical/high security vulnerabilities
- [ ] Deployed to dev environment

---

## Dependencies on Other Services

| Service | Dependency Type | Description |
|---------|----------------|-------------|
| User Service | Inbound | User authentication, addresses |
| Product Service | Inbound | Product details, pricing |
| Inventory Service | Outbound | Stock reservation |
| Payment Service | Outbound | Payment processing |
| Shipping Service | Outbound | Shipping rates, tracking |
| Notification Service | Outbound | Order confirmations, updates |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `order.created` | New order placed | Notification, Analytics, Inventory |
| `order.confirmed` | Payment successful | Notification, Seller Dashboard |
| `order.cancelled` | Order cancelled | Inventory, Payment, Notification |
| `order.shipped` | Order shipped | Notification |
| `order.delivered` | Order delivered | Notification, Review (prompt) |
| `cart.updated` | Cart modified | Analytics, Recommendation |
