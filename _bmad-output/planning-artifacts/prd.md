---
stepsCompleted:
  - step-01-init
  - step-02-discovery
  - step-03-success
  - step-04-journeys
  - step-05-domain
  - step-06-innovation
  - step-07-project-type
  - step-08-scoping
  - step-09-functional
  - step-10-nonfunctional
  - step-11-polish
  - step-12-complete
status: COMPLETE
completedAt: 2026-02-14
inputDocuments:
  - README.md
  - services/order-service/docs/EPICS.md
documentCounts:
  briefs: 0
  research: 0
  brainstorming: 0
  projectDocs: 2
workflowType: 'prd'
projectType: 'brownfield'
classification:
  projectType: api_backend_microservice
  domain: ecommerce
  complexity: medium-high
  projectContext: brownfield
---

# Product Requirements Document - Order Service

**Author:** LakshanDulhara
**Date:** 2026-02-14
**Service:** Order Service (Port 3004)
**Project:** ShopSphere E-commerce Platform

---

## Executive Summary

The Order Service is a critical microservice within the ShopSphere e-commerce platform, responsible for shopping cart management, checkout processing, order lifecycle management, and inter-service coordination. Built with Spring Boot, it integrates with User, Product, Inventory, Payment, Shipping, and Notification services to deliver a seamless purchasing experience.

---

## Success Criteria

### User Success

| Metric | Target | Rationale |
|--------|--------|-----------|
| Add to Cart response | < 500ms | Feels instant, no perceived delay |
| Cart page load | < 1s | Users see their items quickly |
| Checkout completion | < 3 minutes | From cart to order confirmed |
| Order status accuracy | Real-time | Status reflects actual state within 5s |
| Cancel order | < 2 clicks | Easy self-service cancellation |

**User Success Moments:**
- "My item is in the cart" - immediate visual confirmation
- "I know exactly what I'm paying" - clear totals breakdown before payment
- "My order went through" - confirmation page + email within 30s
- "I can track my order" - status visible immediately after checkout

### Business Success

| Metric | MVP Target | Growth Target |
|--------|------------|---------------|
| Cart-to-Order conversion | > 2% | > 4% |
| Checkout abandonment | < 70% | < 50% |
| Order processing errors | < 0.1% | < 0.01% |
| Average checkout time | < 3 min | < 2 min |
| Orders per minute (peak) | 100 | 1,000 |

### Technical Success

| Metric | MVP Target | Measurement |
|--------|------------|-------------|
| Service availability | 99.9% | Monthly uptime |
| API P95 latency (read) | < 200ms | GET /orders, GET /cart |
| API P95 latency (write) | < 500ms | POST checkout, cart mutations |
| Event delivery | At-least-once | No lost order events |
| Test coverage | > 80% | Unit + Integration |
| Zero critical vulnerabilities | Yes | OWASP Top 10 |

### Measurable Outcomes

**MVP Launch Criteria:**
- [ ] All 5 MVP epics (1.1-1.5) completed
- [ ] 80%+ test coverage achieved
- [ ] All APIs documented in OpenAPI
- [ ] Successfully processes 100 orders/minute in load test
- [ ] Integration tested with User, Product, Inventory, Payment services
- [ ] All events publishing correctly to message queue

---

## Product Scope

### MVP - Minimum Viable Product (Phase 1)

**Must Have:**
- Shopping cart CRUD operations
- Guest cart with session persistence
- Cart merge on login
- Stock validation before checkout
- Order creation with inventory reservation
- Payment service integration
- Order status tracking (PENDING -> DELIVERED)
- Customer order cancellation (pre-shipping)
- Internal APIs for Payment/Shipping services
- Events: order.created, order.confirmed, order.cancelled, order.shipped, order.delivered

**Epic Coverage:** 1.1, 1.2, 1.3, 1.4, 1.5 (27 stories)

### Growth Features (Post-MVP / Phase 2)

**Should Have:**
- Order cancellation requests & partial cancellation
- Returns & exchanges flow
- Coupon/discount system
- Admin order management dashboard

**Epic Coverage:** 2.1, 2.2, 2.3

### Vision (Future)

**Nice to Have:**
- Group buying functionality
- Order notes & customer-seller messaging
- Advanced analytics integration
- Multi-currency support
- Split shipments

**Epic Coverage:** 2.4, 2.5 + future enhancements

---

## User Journeys

### Journey 1: Customer - Happy Path (First Purchase)

**Persona:** Sarah, 28, marketing professional

**Situation:** Sarah found a dress she loves on ShopSphere while browsing during lunch break. She wants to buy it before it sells out.

**Opening Scene:**
Sarah is on the product page. She selects size M, clicks "Add to Cart." The button animates, a mini-cart slides in showing "1 item added - $79.00". She feels confident the item is secured.

**Rising Action:**
- Clicks cart icon -> sees her dress with image, size, price
- Notices "Only 3 left in stock" - urgency increases
- Clicks "Proceed to Checkout"
- Already logged in, selects saved home address
- Reviews order: Subtotal $79 + Tax $6.32 + Shipping $5.99 = **$91.31**
- Enters card details, clicks "Place Order"

**Climax:**
3-second spinner... "Order Confirmed! #SHP-20260214-001"
Confetti animation. Email confirmation arrives within 30 seconds.

**Resolution:**
Sarah screenshots her order number, shares the dress link with her friend. Over the next 3 days, she checks "My Orders" twice - sees status go from CONFIRMED -> PROCESSING -> SHIPPED (with tracking link). Dress arrives. She's delighted.

**Capabilities Revealed:**
- Add to cart with immediate feedback
- Real-time stock display
- Saved addresses
- Clear price breakdown
- Order confirmation with email
- Order status tracking
- Tracking number integration

---

### Journey 2: Guest User - Cart Persistence & Merge

**Persona:** Mike, 35, cautious shopper

**Situation:** Mike is browsing ShopSphere on his phone during commute. He's interested but not ready to commit.

**Opening Scene:**
Mike adds a Bluetooth speaker ($49.99) and phone case ($19.99) to cart without logging in. App assigns him a session. He closes the app.

**Rising Action:**
- Next day, opens app on laptop - cart is empty (different device)
- Gets frustrated, almost leaves
- Decides to create account to "save for later"
- During registration, system detects his mobile session (via email match from checkout attempt)
- Prompt: "We found items in your cart from another device. Merge them?"

**Climax:**
Mike clicks "Yes, merge cart." Both items appear. He feels the system "gets" him.

**Resolution:**
Mike completes checkout with confidence, knowing his items won't disappear. Becomes a repeat customer.

**Capabilities Revealed:**
- Guest cart with session persistence (Redis)
- Cross-device cart detection
- Cart merge on login/registration
- Session-to-user cart migration

---

### Journey 3: Customer - Order Cancellation

**Persona:** David, 42, impulse buyer with regrets

**Situation:** David ordered a $299 smartwatch at 11pm. Woke up realizing he can't afford it.

**Opening Scene:**
David opens the app at 7am, heart racing. Order status: "CONFIRMED" (payment processed). He needs to cancel before it ships.

**Rising Action:**
- Goes to My Orders -> finds order #SHP-20260213-089
- Sees status: CONFIRMED, not yet PROCESSING
- Clicks "Cancel Order" button (visible because status allows it)
- Modal: "Are you sure? Reason for cancellation?"
- Selects "Changed my mind" from dropdown
- Clicks "Confirm Cancellation"

**Climax:**
"Order Cancelled. Refund of $299 will be processed within 3-5 business days."
Status changes to CANCELLED. David exhales with relief.

**Resolution:**
David receives cancellation confirmation email. Inventory is released (watch back in stock). Refund appears in 3 days. He appreciates the painless process.

**Capabilities Revealed:**
- Cancel order (pre-shipping only)
- Cancellation reason capture
- Automatic inventory release
- Refund initiation to Payment Service
- Status transition: CONFIRMED -> CANCELLED
- Email notifications

---

### Journey 4: Admin - Order Issue Resolution

**Persona:** Priya, 30, ShopSphere Customer Support Lead

**Situation:** Customer calls saying they received wrong item. Priya needs to investigate and resolve.

**Opening Scene:**
Priya logs into Admin Dashboard, searches order #SHP-20260210-156. Customer claims they ordered blue headphones but received red.

**Rising Action:**
- Views full order details: items, addresses, payment status
- Sees order history: CONFIRMED -> PROCESSING -> SHIPPED -> DELIVERED
- Checks item: "Wireless Headphones - Blue" was ordered
- Adds internal note: "Customer reports wrong color received. Investigating with warehouse."
- Changes status context: flags for "Dispute - Wrong Item"

**Climax:**
After warehouse confirms error, Priya initiates return label + replacement order. Uses admin override to expedite.

**Resolution:**
Customer gets replacement in 2 days. Priya closes the case. Analytics tracks this fulfillment error for quality metrics.

**Capabilities Revealed:**
- Admin order search and filtering
- Full order detail view
- Order status history
- Internal notes (not visible to customer)
- Admin status management
- Audit trail for all actions

---

### Journey 5: Payment Service - Order Verification (API Consumer)

**Persona:** Payment Service (automated system)

**Situation:** Customer clicked "Place Order." Payment Service needs order details to process charge.

**Journey Flow:**
1. Order Service creates order with status PENDING
2. Order Service calls Payment Service: POST /api/payments/create-intent
3. Payment Service needs to verify order exists and amount matches
4. Payment Service calls: GET /internal/orders/{orderId}
5. Payment Service verifies amount matches, processes payment
6. Payment Service calls: PUT /internal/orders/{orderId}/status
7. Order Service updates status, publishes order.confirmed event

**Capabilities Revealed:**
- Internal API: GET /internal/orders/{id}
- Internal API: PUT /internal/orders/{id}/status
- Service-to-service authentication
- Idempotent status updates
- Event publishing on status change

---

### Journey 6: Shipping Service - Fulfillment Flow (API Consumer)

**Persona:** Shipping Service (automated system)

**Situation:** Order confirmed, needs shipping label and tracking.

**Journey Flow:**
1. Shipping Service listens for order.confirmed event
2. Fetches shipping details: GET /internal/orders/{id}/shipping-details
3. Calculates rates, generates label, gets tracking number
4. Updates order: PUT /internal/orders/{id}/status with trackingNumber
5. Order Service updates status, publishes order.shipped event
6. Later, carrier confirms delivery
7. Shipping Service updates: PUT /internal/orders/{id}/status to DELIVERED

**Capabilities Revealed:**
- Internal API: GET /internal/orders/{id}/shipping-details
- Event consumption (order.confirmed)
- Status updates with metadata (tracking number)
- Event publishing (order.shipped, order.delivered)

---

### Journey Requirements Summary

| Capability | Journeys |
|------------|----------|
| Cart CRUD | J1, J2 |
| Guest cart persistence | J2 |
| Cart merge on login | J2 |
| Checkout flow | J1 |
| Order creation | J1, J5 |
| Order status tracking | J1, J3, J4 |
| Order cancellation | J3 |
| Inventory release on cancel | J3 |
| Admin order management | J4 |
| Internal notes | J4 |
| Internal APIs | J5, J6 |
| Event publishing | J5, J6 |
| Service authentication | J5, J6 |

---

## Domain-Specific Requirements

### Compliance & Regulatory

| Requirement | Details | Implementation |
|-------------|---------|----------------|
| **PCI-DSS Scope** | Order Service is OUT of PCI scope | Never store, process, or transmit card numbers. Payment tokens only. |
| **GDPR Compliance** | Customer PII in orders | Implement data export, deletion on request, consent tracking |
| **Data Retention** | Order records for disputes | Retain completed orders for 7 years minimum |
| **Consumer Protection** | Right to cancel, refund transparency | Clear cancellation flow, refund status visibility |
| **Tax Compliance** | Accurate tax records | Store tax breakdown per jurisdiction, support tax reporting |

### Technical Constraints

| Constraint | Requirement | Rationale |
|------------|-------------|-----------|
| **PII Encryption** | Encrypt addresses at rest | GDPR, data breach protection |
| **Audit Logging** | Log all status changes with actor | Dispute resolution, accountability |
| **Soft Deletes** | Never hard-delete orders | Legal retention requirements |
| **Data Masking** | Mask PII in logs | Prevent accidental exposure |
| **Access Control** | Role-based order access | Customers see own orders only |

### Integration Requirements

| Integration | Data Flow | Security |
|-------------|-----------|----------|
| **Payment Service** | Order totals, payment status | Service-to-service auth, no card data |
| **Inventory Service** | Stock reservation/release | Idempotent operations, compensating transactions |
| **Shipping Service** | Addresses, item details | PII transmitted securely |
| **Notification Service** | Order events, customer contact | No sensitive data in notifications |
| **User Service** | User validation, addresses | JWT validation, address ownership check |

### Risk Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| **Double charging** | Customer trust, refunds | Idempotent checkout, payment intent pattern |
| **Overselling** | Fulfillment failure | Inventory reservation before payment |
| **Data breach** | Legal liability, reputation | Encryption, access controls, audit logs |
| **Order tampering** | Fraud, disputes | Immutable order history, checksums |
| **Service outage** | Lost sales | Circuit breakers, graceful degradation, retry logic |

### Data Handling Rules

```
Order Data Classification:
- PUBLIC: Order status, order number
- INTERNAL: Item details, totals, timestamps
- CONFIDENTIAL: Customer name, email, phone
- RESTRICTED: Full address, payment reference

Retention Policy:
- Active orders: Full access
- Completed (< 2 years): Full access
- Completed (2-7 years): Archived, limited access
- Completed (> 7 years): Anonymized or deleted on request
```

---

## API Backend Specific Requirements

### Project-Type Overview

The Order Service is a RESTful microservice built with Spring Boot, exposing both public APIs (for frontend/customers) and internal APIs (for service-to-service communication). It follows the ShopSphere API conventions and integrates with the central API Gateway.

### API Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      API GATEWAY                            │
│            (Kong/NGINX - Authentication, Rate Limiting)     │
└─────────────────────┬───────────────────────────────────────┘
                      │
        ┌─────────────┼─────────────┐
        │             │             │
        ▼             ▼             ▼
   ┌─────────┐  ┌─────────┐  ┌─────────┐
   │ /api/*  │  │/admin/* │  │/internal│
   │ Public  │  │  Admin  │  │  S2S    │
   └─────────┘  └─────────┘  └─────────┘
```

### Authentication Model

| Endpoint Type | Auth Method | Token/Header |
|---------------|-------------|--------------|
| **Public** (`/api/*`) | JWT Bearer | `Authorization: Bearer <token>` |
| **Admin** (`/api/admin/*`) | JWT + ADMIN role | `Authorization: Bearer <token>` |
| **Internal** (`/internal/*`) | Service Secret | `X-Service-Name` + `X-Service-Secret` |

**JWT Claims Expected:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "roles": ["CUSTOMER", "SELLER", "ADMIN"],
  "iat": 1707900000,
  "exp": 1707903600
}
```

### Endpoint Specification

#### Public Cart APIs
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/cart` | Bearer | Get current user's cart |
| POST | `/api/cart/items` | Bearer | Add item to cart |
| PUT | `/api/cart/items/{itemId}` | Bearer | Update item quantity |
| DELETE | `/api/cart/items/{itemId}` | Bearer | Remove item |
| DELETE | `/api/cart` | Bearer | Clear cart |
| POST | `/api/cart/merge` | Bearer | Merge guest cart |
| POST | `/api/cart/validate` | Bearer | Validate for checkout |
| GET | `/api/cart/totals` | Bearer | Calculate totals |
| POST | `/api/cart/apply-coupon` | Bearer | Apply coupon code |
| DELETE | `/api/cart/coupon` | Bearer | Remove coupon |

#### Public Order APIs
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/orders/checkout` | Bearer | Create order from cart |
| GET | `/api/orders` | Bearer | List user's orders |
| GET | `/api/orders/{id}` | Bearer | Get order details |
| GET | `/api/orders/{id}/status` | Bearer | Get status history |
| POST | `/api/orders/{id}/cancel` | Bearer | Cancel order |

#### Admin APIs
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/admin/orders` | Admin | List all orders |
| GET | `/api/admin/orders/{id}` | Admin | Get any order details |
| PUT | `/api/admin/orders/{id}/status` | Admin | Update order status |
| POST | `/api/admin/orders/{id}/notes` | Admin | Add internal note |

#### Internal APIs (Service-to-Service)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/internal/orders/{id}` | Service | Get order for payment verification |
| GET | `/internal/orders/{id}/shipping-details` | Service | Get shipping info |
| PUT | `/internal/orders/{id}/status` | Service | Update status |

### Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `CART_NOT_FOUND` | 404 | Cart does not exist |
| `CART_EMPTY` | 400 | Cannot checkout empty cart |
| `ITEM_NOT_IN_CART` | 404 | Cart item not found |
| `PRODUCT_NOT_FOUND` | 404 | Product does not exist |
| `INSUFFICIENT_STOCK` | 409 | Not enough inventory |
| `PRICE_CHANGED` | 409 | Product price changed since added |
| `ORDER_NOT_FOUND` | 404 | Order does not exist |
| `ORDER_NOT_CANCELLABLE` | 400 | Order status doesn't allow cancellation |
| `INVALID_STATUS_TRANSITION` | 400 | Invalid status change |
| `INVALID_COUPON` | 400 | Coupon invalid or expired |

### Rate Limits

| Endpoint Group | Limit | Window | Scope |
|----------------|-------|--------|-------|
| Public Cart APIs | 100 req | 1 min | Per user |
| Checkout | 10 req | 1 min | Per user |
| Order List/Detail | 60 req | 1 min | Per user |
| Admin APIs | 200 req | 1 min | Per admin |
| Internal APIs | 1000 req | 1 min | Per service |

### API Versioning

| Strategy | Implementation |
|----------|----------------|
| **Method** | URL path versioning |
| **Current** | `/api/v1/*` (implicit, no prefix for v1) |
| **Future** | `/api/v2/*` when breaking changes needed |

### Implementation Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.x |
| Database | PostgreSQL (orders, items) |
| Cache | Redis (cart, sessions) |
| Messaging | RabbitMQ (events) |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Validation | Jakarta Bean Validation |

---

## Project Scoping & Phased Development

### MVP Strategy & Philosophy

**MVP Approach:** Problem-Solving MVP
- Focus on core e-commerce transaction flow (cart -> checkout -> order)
- Deliver working integration with dependent services
- Prove the microservice architecture works end-to-end

**Resource Requirements:**
| Role | Count | Focus |
|------|-------|-------|
| Backend Developer | 1 | Spring Boot, PostgreSQL, Redis |
| DevOps (shared) | 0.5 | CI/CD, Docker, deployment |
| QA (shared) | 0.5 | API testing, integration testing |

### MVP Feature Set (Phase 1)

**Core User Journeys Supported:**
| Journey | MVP Support |
|---------|-------------|
| J1: Customer Happy Path | Full |
| J2: Guest Cart & Merge | Full |
| J3: Order Cancellation | Full |
| J4: Admin Management | Basic |
| J5: Payment Integration | Full |
| J6: Shipping Integration | Full |

**Must-Have Capabilities (Epics 1.1-1.5):**
| Epic | Stories | MVP Scope |
|------|---------|-----------|
| 1.1 Shopping Cart | 6 | All - core functionality |
| 1.2 Checkout Process | 6 | All - revenue critical |
| 1.3 Order Management | 5 | All - customer visibility |
| 1.4 Status Management | 5 | All - fulfillment flow |
| 1.5 Internal APIs | 3 | All - service integration |

**Total MVP Stories:** 25 stories

### Post-MVP Features

**Phase 2 - Growth (Epics 2.1-2.3):**
| Feature | Priority | Rationale |
|---------|----------|-----------|
| Cancellation requests | High | Customer service efficiency |
| Returns flow | High | Post-purchase experience |
| Coupon system (full) | Medium | Marketing enablement |
| Admin dashboard (full) | Medium | Ops efficiency |

**Phase 3 - Expansion (Epics 2.4-2.5):**
| Feature | Priority | Rationale |
|---------|----------|-----------|
| Group buying | Low | Social commerce feature |
| Order notes | Low | Enhanced communication |
| Multi-currency | Future | International expansion |

### Dependency Analysis

**MVP Blockers:**
| Service | Required APIs |
|---------|---------------|
| User Service | Auth validation, address retrieval |
| Product Service | Product details, pricing |
| Inventory Service | Stock check, reservation |
| Payment Service | Payment intent, confirmation |

**MVP Non-Blockers (Can mock):**
| Service | Workaround |
|---------|------------|
| Shipping Service | Hardcode rates initially |
| Notification Service | Log events instead |

### Risk Mitigation Strategy

| Risk | Mitigation |
|------|------------|
| Service integration failures | Circuit breakers, fallback responses |
| Database performance | Index optimization, query monitoring |
| Event delivery failures | Retry with backoff, DLQ |
| Delayed dependencies | Mock services, contract-first dev |

### MVP Definition of Done

- [ ] All 25 MVP stories implemented
- [ ] 80%+ test coverage
- [ ] APIs documented in Swagger
- [ ] Integration test with dependent services
- [ ] Load test: 100 orders/minute
- [ ] Events publishing to RabbitMQ
- [ ] Deployed to staging
- [ ] No critical security vulnerabilities

---

## Functional Requirements

### Shopping Cart Management

- **FR1:** Customer can add a product to their shopping cart
- **FR2:** Customer can view all items in their shopping cart
- **FR3:** Customer can update the quantity of a cart item
- **FR4:** Customer can remove an item from their cart
- **FR5:** Customer can clear all items from their cart
- **FR6:** Guest user can add items to cart without authentication
- **FR7:** Guest user's cart persists across browser sessions
- **FR8:** Customer can merge guest cart with account cart upon login
- **FR9:** System displays real-time stock availability for cart items
- **FR10:** System preserves price-at-add for cart items

### Checkout & Order Creation

- **FR11:** Customer can validate cart contents before checkout
- **FR12:** Customer can view calculated order totals (subtotal, tax, shipping, discounts)
- **FR13:** Customer can select a shipping address for the order
- **FR14:** Customer can select a billing address for the order
- **FR15:** Customer can apply a coupon code to their order
- **FR16:** Customer can remove an applied coupon
- **FR17:** Customer can initiate checkout to create an order
- **FR18:** System reserves inventory during checkout process
- **FR19:** System generates a unique human-readable order number
- **FR20:** System clears cart upon successful order creation

### Order Lifecycle Management

- **FR21:** Customer can view a list of their orders
- **FR22:** Customer can filter their orders by status
- **FR23:** Customer can view detailed information for a specific order
- **FR24:** Customer can view the current status of an order
- **FR25:** Customer can view the complete status history of an order
- **FR26:** Customer can view tracking information when available
- **FR27:** System updates order status based on fulfillment events

### Order Cancellation

- **FR28:** Customer can cancel an order before it ships
- **FR29:** Customer must provide a cancellation reason
- **FR30:** System releases reserved inventory upon cancellation
- **FR31:** System initiates refund process upon cancellation
- **FR32:** System prevents cancellation of shipped/delivered orders

### Admin Order Management

- **FR33:** Admin can view all orders in the system
- **FR34:** Admin can search orders by order number, customer, or date
- **FR35:** Admin can filter orders by status
- **FR36:** Admin can view any order's full details
- **FR37:** Admin can update an order's status
- **FR38:** Admin can add internal notes to an order
- **FR39:** Admin can view the audit trail of order changes

### Service Integration

- **FR40:** Payment Service can retrieve order details for verification
- **FR41:** Payment Service can update order status upon payment
- **FR42:** Shipping Service can retrieve order shipping details
- **FR43:** Shipping Service can update order status with tracking
- **FR44:** System validates user authentication via User Service
- **FR45:** System retrieves product details from Product Service
- **FR46:** System checks and reserves stock via Inventory Service

### Event Publishing

- **FR47:** System publishes event when order is created
- **FR48:** System publishes event when order is confirmed
- **FR49:** System publishes event when order is cancelled
- **FR50:** System publishes event when order is shipped
- **FR51:** System publishes event when order is delivered
- **FR52:** System publishes event when cart is updated

### Data & Compliance

- **FR53:** System retains order records for required retention period
- **FR54:** System logs all order status changes with actor and timestamp
- **FR55:** System encrypts customer PII at rest
- **FR56:** Customer can only access their own orders

---

## Non-Functional Requirements

### Performance

| Metric | Requirement | Measurement |
|--------|-------------|-------------|
| **NFR-P1** | Cart API responses < 200ms P95 | GET /cart, cart mutations |
| **NFR-P2** | Checkout completion < 500ms P95 | POST /orders/checkout |
| **NFR-P3** | Order list/detail < 200ms P95 | GET /orders, GET /orders/{id} |
| **NFR-P4** | Internal APIs < 100ms P95 | Service-to-service calls |
| **NFR-P5** | Database queries < 50ms P95 | All PostgreSQL queries |
| **NFR-P6** | Cache hit rate > 90% | Redis cart cache |

**Load Requirements:**
- Support 100 concurrent checkouts
- Handle 100 orders/minute sustained
- Handle 500 orders/minute peak (5-minute burst)

### Security

| Requirement | Details |
|-------------|---------|
| **NFR-S1** | All API endpoints require authentication |
| **NFR-S2** | JWT tokens validated on every request |
| **NFR-S3** | Internal APIs require service-to-service auth |
| **NFR-S4** | Customer PII encrypted at rest (AES-256) |
| **NFR-S5** | All data in transit encrypted (TLS 1.3) |
| **NFR-S6** | SQL injection prevention via parameterized queries |
| **NFR-S7** | Input validation on all request payloads |
| **NFR-S8** | Rate limiting enforced per user/service |
| **NFR-S9** | No sensitive data in logs (PII masking) |
| **NFR-S10** | OWASP Top 10 vulnerabilities addressed |

### Scalability

| Requirement | Details |
|-------------|---------|
| **NFR-SC1** | Stateless API design (horizontal scaling) |
| **NFR-SC2** | Database connection pooling (max 20/instance) |
| **NFR-SC3** | Redis cluster support for cart caching |
| **NFR-SC4** | Support 10x user growth without architecture changes |
| **NFR-SC5** | Auto-scaling triggered at 70% CPU utilization |

### Reliability

| Requirement | Details |
|-------------|---------|
| **NFR-R1** | Service availability 99.9% |
| **NFR-R2** | Zero data loss for confirmed orders |
| **NFR-R3** | Graceful degradation when dependencies unavailable |
| **NFR-R4** | Circuit breakers for all external service calls |
| **NFR-R5** | Retry with exponential backoff for transient failures |
| **NFR-R6** | Dead letter queue for failed event publishing |
| **NFR-R7** | Health check endpoint for load balancer |

### Integration

| Requirement | Details |
|-------------|---------|
| **NFR-I1** | REST API following OpenAPI 3.0 specification |
| **NFR-I2** | JSON request/response format |
| **NFR-I3** | Idempotent POST operations |
| **NFR-I4** | Event publishing via RabbitMQ (AMQP) |
| **NFR-I5** | At-least-once event delivery guarantee |
| **NFR-I6** | Correlation ID propagation across services |
| **NFR-I7** | Request tracing via distributed tracing |

### Observability

| Requirement | Details |
|-------------|---------|
| **NFR-O1** | Structured JSON logging |
| **NFR-O2** | Metrics exported to Prometheus |
| **NFR-O3** | Custom business metrics (orders/min, conversions) |
| **NFR-O4** | Alerting on error rate > 1% |
| **NFR-O5** | Alerting on P95 latency > 2x target |

