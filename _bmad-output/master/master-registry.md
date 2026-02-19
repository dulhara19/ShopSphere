---
documentType: master-registry
projectName: ShopSphere
maintainer: LakshanDulhara (Team Lead)
lastUpdated: 2026-02-14
version: 1.0.0
---

# ShopSphere Master Registry

> **Purpose:** Central tracking document for all microservices development status, integration readiness, and frontend availability.

## Quick Status Dashboard

| # | Service | Owner | MVP Status | Integration | Frontend | Environment |
|---|---------|-------|------------|-------------|----------|-------------|
| 1 | User Service | TM1 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 2 | Product Service | TM2 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 3 | Inventory Service | TM3 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 4 | Order Service | TM4 (Lead) | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 5 | Payment Service | TM5 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 6 | Shipping Service | TM6 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 7 | Review Service | TM7 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 8 | Recommendation Service | TM8 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 9 | Notification Service | TM9 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |
| 10 | Analytics Service | TM10 | `NOT_STARTED` | `PENDING` | `NOT_INTEGRATED` | - |

### Status Legend

**MVP Status:**
- `NOT_STARTED` - No code implemented yet
- `IN_PROGRESS` - Development started, some stories complete
- `MVP_COMPLETE` - All Phase 1 epics done, ready for integration testing
- `PRODUCTION_READY` - Fully tested, documented, deployable

**Integration Status:**
- `PENDING` - Not yet tested with other services
- `TESTING` - Currently in integration testing
- `VERIFIED` - Passed integration tests
- `FAILED` - Integration issues found (see notes)

**Frontend Status:**
- `NOT_INTEGRATED` - API not yet consumed by frontend
- `IN_PROGRESS` - Frontend integration in development
- `INTEGRATED` - API fully integrated and working
- `DISABLED` - Temporarily disabled (feature flag off)

**Environment:**
- `LOCAL` - Running locally only
- `DOCKER` - Running in Docker compose
- `STAGING` - Deployed to staging
- `PRODUCTION` - Live in production

---

## Service Details

### 1. User Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 1 |
| **Port** | 3001 |
| **Branch** | `service/user-service` |
| **Status File** | [service-status/user-service.md](./service-status/user-service.md) |
| **Contract** | [shared/contracts/user-service.yaml](/shared/contracts/user-service.yaml) |
| **Events** | [shared/event-schemas/user-events.json](/shared/event-schemas/user-events.json) |

**Dependencies:** None (foundational service)
**Dependents:** All other services (authentication)

**MVP Epics:**
- [ ] Epic 1.1: User Registration & Login
- [ ] Epic 1.2: JWT Authentication
- [ ] Epic 1.3: Profile Management
- [ ] Epic 1.4: Role-Based Access Control

---

### 2. Product Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 2 |
| **Port** | 3002 |
| **Branch** | `service/product-service` |
| **Status File** | [service-status/product-service.md](./service-status/product-service.md) |
| **Contract** | [shared/contracts/product-service.yaml](/shared/contracts/product-service.yaml) |
| **Events** | [shared/event-schemas/product-events.json](/shared/event-schemas/product-events.json) |

**Dependencies:** None
**Dependents:** Order, Review, Recommendation, Inventory

**MVP Epics:**
- [ ] Epic 1.1: Product CRUD
- [ ] Epic 1.2: Category Management
- [ ] Epic 1.3: Product Search
- [ ] Epic 1.4: Product Variants

---

### 3. Inventory Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 3 |
| **Port** | 3003 |
| **Branch** | `service/inventory-service` |
| **Status File** | [service-status/inventory-service.md](./service-status/inventory-service.md) |
| **Contract** | [shared/contracts/inventory-service.yaml](/shared/contracts/inventory-service.yaml) |
| **Events** | [shared/event-schemas/inventory-events.json](/shared/event-schemas/inventory-events.json) |

**Dependencies:** Product Service
**Dependents:** Order Service

**MVP Epics:**
- [ ] Epic 1.1: Stock Management
- [ ] Epic 1.2: Inventory Reservation
- [ ] Epic 1.3: Stock Alerts
- [ ] Epic 1.4: Multi-Warehouse Support

---

### 4. Order Service (Team Lead)

| Property | Value |
|----------|-------|
| **Owner** | Team Member 4 (LakshanDulhara - Lead) |
| **Port** | 3004 |
| **Branch** | `service/order-service` |
| **Status File** | [service-status/order-service.md](./service-status/order-service.md) |
| **Contract** | [shared/contracts/order-service.yaml](/shared/contracts/order-service.yaml) |
| **Events** | [shared/event-schemas/order-events.json](/shared/event-schemas/order-events.json) |

**Dependencies:** User, Product, Inventory, Payment, Shipping
**Dependents:** Payment, Shipping, Notification, Analytics

**MVP Epics:**
- [ ] Epic 1.1: Shopping Cart Management
- [ ] Epic 1.2: Checkout Process
- [ ] Epic 1.3: Order Management
- [ ] Epic 1.4: Order Status Management
- [ ] Epic 1.5: Internal Service Communication

---

### 5. Payment Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 5 |
| **Port** | 3005 |
| **Branch** | `service/payment-service` |
| **Status File** | [service-status/payment-service.md](./service-status/payment-service.md) |
| **Contract** | [shared/contracts/payment-service.yaml](/shared/contracts/payment-service.yaml) |
| **Events** | [shared/event-schemas/payment-events.json](/shared/event-schemas/payment-events.json) |

**Dependencies:** Order Service
**Dependents:** Order Service (callback)

**MVP Epics:**
- [ ] Epic 1.1: Payment Intent Creation
- [ ] Epic 1.2: Stripe Integration
- [ ] Epic 1.3: Payment Confirmation
- [ ] Epic 1.4: Refund Processing

---

### 6. Shipping Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 6 |
| **Port** | 3006 |
| **Branch** | `service/shipping-service` |
| **Status File** | [service-status/shipping-service.md](./service-status/shipping-service.md) |
| **Contract** | [shared/contracts/shipping-service.yaml](/shared/contracts/shipping-service.yaml) |
| **Events** | [shared/event-schemas/shipping-events.json](/shared/event-schemas/shipping-events.json) |

**Dependencies:** Order Service
**Dependents:** Order Service (status updates)

**MVP Epics:**
- [ ] Epic 1.1: Shipping Rate Calculation
- [ ] Epic 1.2: Label Generation
- [ ] Epic 1.3: Tracking Integration
- [ ] Epic 1.4: Carrier Management

---

### 7. Review Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 7 |
| **Port** | 3007 |
| **Branch** | `service/review-service` |
| **Status File** | [service-status/review-service.md](./service-status/review-service.md) |
| **Contract** | [shared/contracts/review-service.yaml](/shared/contracts/review-service.yaml) |
| **Events** | [shared/event-schemas/review-events.json](/shared/event-schemas/review-events.json) |

**Dependencies:** User, Product
**Dependents:** Product (ratings), Recommendation

**MVP Epics:**
- [ ] Epic 1.1: Review CRUD
- [ ] Epic 1.2: Rating System
- [ ] Epic 1.3: Review Moderation
- [ ] Epic 1.4: Helpful Votes

---

### 8. Recommendation Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 8 |
| **Port** | 3008 |
| **Branch** | `service/recommendation-service` |
| **Status File** | [service-status/recommendation-service.md](./service-status/recommendation-service.md) |
| **Contract** | [shared/contracts/recommendation-service.yaml](/shared/contracts/recommendation-service.yaml) |
| **Events** | [shared/event-schemas/recommendation-events.json](/shared/event-schemas/recommendation-events.json) |

**Dependencies:** Product, User, Analytics
**Dependents:** None (provides recommendations)

**MVP Epics:**
- [ ] Epic 1.1: Similar Products
- [ ] Epic 1.2: Personalized Recommendations
- [ ] Epic 1.3: Trending Products
- [ ] Epic 1.4: Recently Viewed

---

### 9. Notification Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 9 |
| **Port** | 3009 |
| **Branch** | `service/notification-service` |
| **Status File** | [service-status/notification-service.md](./service-status/notification-service.md) |
| **Contract** | [shared/contracts/notification-service.yaml](/shared/contracts/notification-service.yaml) |
| **Events** | [shared/event-schemas/notification-events.json](/shared/event-schemas/notification-events.json) |

**Dependencies:** User (contact info)
**Dependents:** None (consumes events from all)

**MVP Epics:**
- [ ] Epic 1.1: Email Notifications
- [ ] Epic 1.2: SMS Notifications
- [ ] Epic 1.3: Push Notifications
- [ ] Epic 1.4: Notification Preferences

---

### 10. Analytics Service

| Property | Value |
|----------|-------|
| **Owner** | Team Member 10 |
| **Port** | 3010 |
| **Branch** | `service/analytics-service` |
| **Status File** | [service-status/analytics-service.md](./service-status/analytics-service.md) |
| **Contract** | [shared/contracts/analytics-service.yaml](/shared/contracts/analytics-service.yaml) |
| **Events** | [shared/event-schemas/analytics-events.json](/shared/event-schemas/analytics-events.json) |

**Dependencies:** None (consumes events)
**Dependents:** Recommendation

**MVP Epics:**
- [ ] Epic 1.1: Event Ingestion
- [ ] Epic 1.2: Sales Dashboard
- [ ] Epic 1.3: User Analytics
- [ ] Epic 1.4: Product Analytics

---

## Integration Priority Order

Based on dependencies, services should be integrated in this order:

```
Phase 1 (Foundational):
  1. User Service      ─┐
  2. Product Service   ─┼── No dependencies, can start immediately
  3. Notification Svc  ─┘

Phase 2 (Core Commerce):
  4. Inventory Service ── Depends on Product
  5. Review Service    ── Depends on User, Product

Phase 3 (Order Flow):
  6. Order Service     ── Depends on User, Product, Inventory
  7. Payment Service   ── Depends on Order
  8. Shipping Service  ── Depends on Order

Phase 4 (Intelligence):
  9. Analytics Service ── Consumes events from all
  10. Recommendation   ── Depends on Product, Analytics
```

---

## Recent Activity Log

| Date | Service | Action | By | Notes |
|------|---------|--------|-----|-------|
| 2026-02-14 | All | Master registry created | Lead | Initial setup |
| | | | | |

---

## Blocked Services

_No services currently blocked._

---

## Notes & Decisions

### 2026-02-14
- Master-Agent Orchestration System initialized
- All service status files created
- PR review workflow ready for use

---

## Related Documents

- [Integration Strategy](./planning-artifacts/integration-strategy.md)
- [Environment Config](./environment-config.md)
- [Frontend Integration](./frontend-integration.md)
