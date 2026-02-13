---
documentType: integration-strategy
projectName: ShopSphere
author: LakshanDulhara
role: Team Lead
dateCreated: 2026-02-14
version: 1.0.0
status: active
relatedDocuments:
  - services/order-service/docs/EPICS.md
  - README.md
---

# ShopSphere Master Integration Strategy

## Executive Summary

This document establishes the **mandatory integration standards** for all 10 microservices in the ShopSphere e-commerce platform. All team members MUST follow these guidelines to ensure seamless integration.

**Key Decisions:**
- **Frontend Owner:** Team Lead (LakshanDulhara)
- **Integration Approach:** API-First with Contract Testing
- **Communication:** REST (sync) + RabbitMQ/Kafka (async)
- **Shared Standards:** Enforced via `shared/` folder artifacts

---

## Table of Contents

1. [Service Overview & Dependencies](#1-service-overview--dependencies)
2. [API Contract Standards](#2-api-contract-standards)
3. [Event Schema Standards](#3-event-schema-standards)
4. [Shared Library Standards](#4-shared-library-standards)
5. [Integration Milestones](#5-integration-milestones)
6. [Frontend Architecture](#6-frontend-architecture)
7. [Testing Strategy](#7-testing-strategy)
8. [Branch & Merge Strategy](#8-branch--merge-strategy)
9. [Definition of Done](#9-definition-of-done)
10. [Communication Protocol](#10-communication-protocol)

---

## 1. Service Overview & Dependencies

### 1.1 Service Matrix

| # | Service | Port | Owner | Primary Responsibility | Database |
|---|---------|------|-------|------------------------|----------|
| 1 | User Service | 3001 | Team Member 1 | Authentication, Profiles, JWT | PostgreSQL |
| 2 | Product Service | 3002 | Team Member 2 | Catalog, Search, Categories | MongoDB |
| 3 | Inventory Service | 3003 | Team Member 3 | Stock, Reservations, Warehouses | PostgreSQL |
| 4 | Order Service | 3004 | Team Member 4 (Lead) | Cart, Checkout, Orders | PostgreSQL |
| 5 | Payment Service | 3005 | Team Member 5 | Payments, Stripe, Refunds | PostgreSQL |
| 6 | Shipping Service | 3006 | Team Member 6 | Rates, Tracking, Labels | PostgreSQL |
| 7 | Review Service | 3007 | Team Member 7 | Reviews, Ratings, Social | MongoDB |
| 8 | Recommendation Service | 3008 | Team Member 8 | AI Recommendations | MongoDB |
| 9 | Notification Service | 3009 | Team Member 9 | Email, SMS, Push | PostgreSQL |
| 10 | Analytics Service | 3010 | Team Member 10 | Dashboards, Reports | ClickHouse |

### 1.2 Service Dependency Map

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │  (Kong/NGINX)   │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│ User Service  │◄───│ Order Service │───►│Product Service│
│    (3001)     │    │    (3004)     │    │    (3002)     │
└───────────────┘    └───────┬───────┘    └───────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│Inventory Svc  │    │ Payment Svc   │    │ Shipping Svc  │
│    (3003)     │    │    (3005)     │    │    (3006)     │
└───────────────┘    └───────────────┘    └───────────────┘
                             │
                             ▼
                    ┌───────────────┐
                    │Notification   │◄──── All Services
                    │    (3009)     │      Publish Events
                    └───────────────┘
```

### 1.3 Dependency Matrix (Who Calls Whom)

| Service | Depends On (Sync REST) | Publishes Events To |
|---------|------------------------|---------------------|
| User | - | Notification |
| Product | - | Analytics, Recommendation |
| Inventory | - | Notification, Analytics |
| **Order** | User, Product, Inventory, Payment, Shipping | Notification, Analytics, Inventory |
| Payment | Order (callback) | Notification, Order, Analytics |
| Shipping | Order | Notification, Order |
| Review | User, Product | Notification, Analytics |
| Recommendation | Product, User, Analytics | - |
| Notification | - | Analytics |
| Analytics | All (consumes events) | - |

---

## 2. API Contract Standards

### 2.1 Contract Location

All OpenAPI specifications MUST be stored in:
```
shared/contracts/
├── user-service.yaml
├── product-service.yaml
├── inventory-service.yaml
├── order-service.yaml
├── payment-service.yaml
├── shipping-service.yaml
├── review-service.yaml
├── recommendation-service.yaml
├── notification-service.yaml
└── analytics-service.yaml
```

### 2.2 OpenAPI Template

Every service MUST provide an OpenAPI 3.0+ specification following this structure:

```yaml
openapi: 3.0.3
info:
  title: {Service Name} API
  description: ShopSphere {Service Name} - {Brief Description}
  version: 1.0.0
  contact:
    name: {Owner Name}

servers:
  - url: http://localhost:{port}
    description: Local development
  - url: https://api.shopsphere.com/{service}
    description: Production

tags:
  - name: public
    description: Public APIs (requires user auth)
  - name: internal
    description: Internal APIs (service-to-service only)
  - name: admin
    description: Admin APIs (requires admin role)

paths:
  # Define all endpoints here

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
  schemas:
    # Define all DTOs here
    Error:
      type: object
      properties:
        code:
          type: string
        message:
          type: string
        timestamp:
          type: string
          format: date-time
```

### 2.3 API Design Standards

#### URL Conventions
```
Public APIs:     /api/{resource}
Internal APIs:   /internal/{resource}
Admin APIs:      /api/admin/{resource}
Webhooks:        /webhooks/{provider}
Health Check:    /actuator/health
```

#### HTTP Methods
| Method | Purpose | Idempotent |
|--------|---------|------------|
| GET | Retrieve resource(s) | Yes |
| POST | Create resource | No |
| PUT | Full update | Yes |
| PATCH | Partial update | Yes |
| DELETE | Remove resource | Yes |

#### Response Format (Mandatory)
```json
// Success Response
{
  "success": true,
  "data": { ... },
  "meta": {
    "page": 1,
    "limit": 20,
    "total": 100
  }
}

// Error Response
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Order not found with ID: xxx",
    "timestamp": "2026-02-14T10:30:00Z",
    "path": "/api/orders/xxx"
  }
}
```

#### Pagination (Mandatory for List Endpoints)
```
GET /api/orders?page=1&limit=20&sort=createdAt&order=desc
```

#### Standard Error Codes
| HTTP Status | Error Code | Usage |
|-------------|------------|-------|
| 400 | VALIDATION_ERROR | Invalid input |
| 401 | UNAUTHORIZED | Missing/invalid token |
| 403 | FORBIDDEN | Insufficient permissions |
| 404 | RESOURCE_NOT_FOUND | Entity not found |
| 409 | CONFLICT | Duplicate/state conflict |
| 422 | BUSINESS_RULE_VIOLATION | Business logic failure |
| 500 | INTERNAL_ERROR | Server error |
| 503 | SERVICE_UNAVAILABLE | Dependency down |

### 2.4 Internal API Authentication

Service-to-service calls MUST include:
```
X-Service-Name: order-service
X-Service-Secret: {shared-secret}
X-Correlation-Id: {uuid}
```

---

## 3. Event Schema Standards

### 3.1 Event Location

All event schemas MUST be stored in:
```
shared/event-schemas/
├── user-events.json
├── product-events.json
├── inventory-events.json
├── order-events.json
├── payment-events.json
├── shipping-events.json
├── review-events.json
└── notification-events.json
```

### 3.2 Event Envelope (Mandatory)

Every event MUST follow this envelope structure:

```json
{
  "eventId": "uuid",
  "eventType": "order.created",
  "eventVersion": "1.0",
  "source": "order-service",
  "timestamp": "2026-02-14T10:30:00Z",
  "correlationId": "uuid",
  "data": {
    // Event-specific payload
  },
  "metadata": {
    "userId": "uuid",
    "traceId": "uuid"
  }
}
```

### 3.3 Event Naming Convention

```
{domain}.{action}

Examples:
- order.created
- order.confirmed
- order.shipped
- order.delivered
- order.cancelled
- payment.completed
- payment.failed
- inventory.reserved
- inventory.released
- user.registered
- user.updated
```

### 3.4 Required Events per Service

| Service | Events to Publish |
|---------|-------------------|
| User | user.registered, user.updated, user.deleted |
| Product | product.created, product.updated, product.deleted, product.viewed |
| Inventory | inventory.updated, inventory.reserved, inventory.released, inventory.low_stock |
| Order | order.created, order.confirmed, order.processing, order.shipped, order.delivered, order.cancelled, cart.updated |
| Payment | payment.initiated, payment.completed, payment.failed, payment.refunded |
| Shipping | shipping.label_created, shipping.picked_up, shipping.in_transit, shipping.delivered |
| Review | review.created, review.updated, review.deleted |
| Notification | notification.sent, notification.failed |

### 3.5 Message Queue Configuration

**Exchange/Topic Naming:**
```
shopsphere.{service}.events

Examples:
- shopsphere.order.events
- shopsphere.payment.events
- shopsphere.inventory.events
```

**Queue Naming:**
```
{consumer-service}.{event-type}.queue

Examples:
- notification.order.created.queue
- analytics.order.created.queue
- inventory.order.cancelled.queue
```

---

## 4. Shared Library Standards

### 4.1 Common Library Structure

```
shared/common-lib/
├── pom.xml
└── src/main/java/com/shopsphere/common/
    ├── dto/
    │   ├── ApiResponse.java
    │   ├── PagedResponse.java
    │   ├── ErrorResponse.java
    │   └── BaseDto.java
    ├── event/
    │   ├── EventEnvelope.java
    │   ├── EventPublisher.java
    │   └── EventListener.java
    ├── exception/
    │   ├── BaseException.java
    │   ├── ResourceNotFoundException.java
    │   ├── ValidationException.java
    │   ├── BusinessRuleException.java
    │   └── GlobalExceptionHandler.java
    ├── security/
    │   ├── JwtTokenProvider.java
    │   ├── JwtAuthenticationFilter.java
    │   └── ServiceAuthFilter.java
    └── util/
        ├── DateUtils.java
        ├── IdGenerator.java
        └── ValidationUtils.java
```

### 4.2 Using Common Library

Add to each service's `pom.xml`:
```xml
<dependency>
    <groupId>com.shopsphere</groupId>
    <artifactId>common-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 4.3 Mandatory Classes to Use

| Class | Purpose | Required |
|-------|---------|----------|
| ApiResponse<T> | Wrap all REST responses | YES |
| EventEnvelope | Wrap all published events | YES |
| GlobalExceptionHandler | Consistent error handling | YES |
| JwtAuthenticationFilter | JWT validation | YES |
| IdGenerator | UUID generation | YES |

---

## 5. Integration Milestones

### 5.1 Phase 1: Foundation (Week 1-2)

**Goal:** Establish shared infrastructure

| Task | Owner | Deliverable |
|------|-------|-------------|
| Create common-lib with base classes | Team Lead | `shared/common-lib/` populated |
| Define all OpenAPI contracts | All Members | `shared/contracts/*.yaml` |
| Define all event schemas | All Members | `shared/event-schemas/*.json` |
| Setup API Gateway (Kong) | Team Lead | `gateway/kong/` configured |
| Setup RabbitMQ/Kafka | Team Lead | Message queue running |

**Checkpoint:** All contracts reviewed and approved by Team Lead

### 5.2 Phase 2: MVP Development (Week 3-6)

**Goal:** Each service implements MVP functionality

| Service | MVP Features | Dependencies |
|---------|-------------|--------------|
| User | Register, Login, JWT, Profile | None |
| Product | CRUD, Search, Categories | None |
| Inventory | Stock check, Reserve, Release | None |
| Order | Cart, Checkout, Order CRUD | User, Product, Inventory |
| Payment | Create intent, Confirm, Refund | Order |
| Shipping | Calculate rate, Create label | Order |
| Review | Create, List, Rate | User, Product |
| Recommendation | Basic recommendations | Product |
| Notification | Email sending | None |
| Analytics | Event ingestion | None |

**Checkpoint:** Each service passes contract tests

### 5.3 Phase 3: Integration Testing (Week 7-8)

**Goal:** Services communicate correctly

| Integration Test | Services Involved |
|------------------|-------------------|
| User Registration Flow | User → Notification |
| Add to Cart Flow | Order → Product → Inventory |
| Checkout Flow | Order → Inventory → Payment → Shipping → Notification |
| Order Tracking Flow | Order → Shipping → Notification |
| Review Flow | Review → User → Product → Notification |

**Checkpoint:** All integration tests passing

### 5.4 Phase 4: Frontend Integration (Week 9-10)

**Goal:** Unified frontend connects all services

| Frontend Module | Services Consumed |
|-----------------|-------------------|
| Auth Module | User Service |
| Product Catalog | Product, Recommendation |
| Shopping Cart | Order, Product, Inventory |
| Checkout | Order, Payment, Shipping |
| Order History | Order, Shipping |
| Reviews | Review, User |
| User Profile | User, Order |
| Notifications | Notification (WebSocket) |

**Checkpoint:** Full E2E user flows working

### 5.5 Phase 5: Production Readiness (Week 11-12)

- Performance testing
- Security audit
- Documentation completion
- Deployment to staging
- Final integration testing
- Production deployment

---

## 6. Frontend Architecture

### 6.1 Ownership

**Frontend Owner:** Team Lead (LakshanDulhara)

The unified frontend will be developed in `frontend/` and will consume all 10 microservices.

### 6.2 Tech Stack

| Layer | Technology |
|-------|------------|
| Framework | React 18+ with TypeScript |
| State Management | Redux Toolkit / Zustand |
| API Client | Axios with interceptors |
| UI Components | Tailwind CSS + Headless UI |
| Forms | React Hook Form + Zod |
| Routing | React Router v6 |
| Real-time | Socket.io Client |
| Testing | Jest + React Testing Library |

### 6.3 Frontend Structure

```
frontend/
├── src/
│   ├── api/
│   │   ├── client.ts           # Axios instance
│   │   ├── userApi.ts
│   │   ├── productApi.ts
│   │   ├── orderApi.ts
│   │   ├── paymentApi.ts
│   │   └── ...
│   ├── components/
│   │   ├── common/
│   │   ├── auth/
│   │   ├── products/
│   │   ├── cart/
│   │   ├── checkout/
│   │   ├── orders/
│   │   └── ...
│   ├── pages/
│   ├── store/
│   ├── hooks/
│   ├── utils/
│   └── types/
├── public/
└── package.json
```

### 6.4 API Client Pattern

Each service will have a dedicated API client:

```typescript
// api/client.ts
const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_GATEWAY_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - add JWT
apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor - handle errors
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => handleApiError(error)
);
```

### 6.5 Service Integration via API Gateway

All frontend requests go through API Gateway:

```
Frontend → API Gateway → Microservice

Routes:
/api/auth/*     → User Service (3001)
/api/users/*    → User Service (3001)
/api/products/* → Product Service (3002)
/api/inventory/*→ Inventory Service (3003)
/api/cart/*     → Order Service (3004)
/api/orders/*   → Order Service (3004)
/api/payments/* → Payment Service (3005)
/api/shipping/* → Shipping Service (3006)
/api/reviews/*  → Review Service (3007)
/api/recommendations/* → Recommendation Service (3008)
```

---

## 7. Testing Strategy

### 7.1 Testing Pyramid

```
         ┌─────────┐
         │   E2E   │  ← Frontend + All Services (Team Lead)
         ├─────────┤
       ┌─┴─────────┴─┐
       │ Integration │  ← Service + Dependencies (Each Owner)
       ├─────────────┤
    ┌──┴─────────────┴──┐
    │    Unit Tests     │  ← Individual Components (Each Owner)
    └───────────────────┘
```

### 7.2 Test Requirements per Service

| Test Type | Coverage | Owner |
|-----------|----------|-------|
| Unit Tests | 80% minimum | Service Owner |
| Integration Tests | All API endpoints | Service Owner |
| Contract Tests | All published APIs | Service Owner |
| E2E Tests | Critical flows | Team Lead |

### 7.3 Contract Testing with Pact

Each service MUST provide Pact contracts:

**Provider Side (Service Owner):**
```java
@Provider("order-service")
@PactBroker
class OrderServiceContractTest {
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
```

**Consumer Side (Frontend/Other Services):**
```java
@ExtendWith(PactConsumerTestExt.class)
class OrderServiceConsumerTest {
    @Pact(consumer = "frontend")
    V4Pact createOrder(PactDslWithProvider builder) {
        return builder
            .given("cart exists")
            .uponReceiving("create order request")
            .path("/api/orders/checkout")
            .method("POST")
            .willRespondWith()
            .status(201)
            .toPact(V4Pact.class);
    }
}
```

### 7.4 Integration Test Environment

```yaml
# docker-compose.test.yml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: shopsphere_test

  mongodb:
    image: mongo:6

  redis:
    image: redis:7

  rabbitmq:
    image: rabbitmq:3-management
```

---

## 8. Branch & Merge Strategy

### 8.1 Branch Structure

```
main                    ← Production-ready code
├── develop             ← Integration branch
│   ├── service/user-service
│   ├── service/product-service
│   ├── service/inventory-service
│   ├── service/order-service
│   ├── service/payment-service
│   ├── service/shipping-service
│   ├── service/review-service
│   ├── service/recommendation-service
│   ├── service/notification-service
│   ├── service/analytics-service
│   └── frontend
└── feature/{service}/{feature-name}
```

### 8.2 Workflow

1. **Development:** Work on `service/{service-name}` branch
2. **Feature:** Create `feature/{service}/{feature}` from service branch
3. **PR to Service Branch:** Requires service owner approval
4. **PR to Develop:** Requires Team Lead approval + CI passing
5. **PR to Main:** Requires Team Lead approval + all tests passing

### 8.3 Merge Rules

| Target Branch | Required Approvals | Required Checks |
|---------------|-------------------|-----------------|
| feature → service | 1 (self or peer) | Unit tests |
| service → develop | Team Lead | Unit + Integration + Contract tests |
| develop → main | Team Lead + 1 | All tests + E2E |

### 8.4 Commit Convention

```
{type}({service}): {description}

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- test: Tests
- refactor: Code refactoring
- chore: Maintenance

Examples:
- feat(order): add cart validation endpoint
- fix(payment): handle Stripe webhook retry
- docs(user): update API documentation
- test(inventory): add reservation unit tests
```

---

## 9. Definition of Done

### 9.1 Story Level DoD

A story is DONE when:
- [ ] Code implemented following coding standards
- [ ] Unit tests written (80% coverage minimum)
- [ ] Integration tests for API endpoints
- [ ] OpenAPI spec updated in `shared/contracts/`
- [ ] Event schemas updated (if publishing events)
- [ ] Code reviewed and approved
- [ ] No critical/high security vulnerabilities
- [ ] Deployed to dev environment
- [ ] Documented in service README

### 9.2 Service MVP DoD

A service MVP is DONE when:
- [ ] All Phase 1 epic stories completed
- [ ] All API endpoints match OpenAPI contract
- [ ] All events match event schemas
- [ ] Contract tests passing
- [ ] Integration tests passing with mock dependencies
- [ ] Swagger/OpenAPI documentation accessible
- [ ] Health check endpoint working
- [ ] Docker image builds successfully
- [ ] Can start with docker-compose
- [ ] README updated with setup instructions

### 9.3 Integration Ready DoD

A service is INTEGRATION READY when:
- [ ] MVP DoD met
- [ ] All dependencies can be called (not mocked)
- [ ] All events are being published correctly
- [ ] All events are being consumed correctly
- [ ] Works end-to-end in integration environment
- [ ] Performance acceptable (response < 500ms)
- [ ] Error handling works correctly

---

## 10. Communication Protocol

### 10.1 Required Meetings

| Meeting | Frequency | Attendees | Purpose |
|---------|-----------|-----------|---------|
| Daily Standup | Daily | All | Progress updates |
| Integration Sync | Weekly | All | Integration issues |
| Contract Review | As needed | Affected parties | API/Event changes |
| Demo | Bi-weekly | All | Show progress |

### 10.2 Communication Channels

| Channel | Purpose |
|---------|---------|
| Slack #shopsphere-dev | General development |
| Slack #shopsphere-integration | Integration issues |
| GitHub Issues | Bug tracking |
| GitHub PRs | Code reviews |
| Confluence/Notion | Documentation |

### 10.3 Escalation Path

```
Issue Detected
     │
     ▼
Try to resolve with affected service owner
     │
     ▼ (if unresolved)
Escalate to Team Lead
     │
     ▼ (if blocking)
Emergency sync meeting
```

### 10.4 Contract Change Protocol

**Before changing any API or Event:**

1. Create GitHub Issue describing the change
2. Tag all affected service owners
3. Get approval from Team Lead
4. Update contract in `shared/contracts/` or `shared/event-schemas/`
5. All affected services update their code
6. Contract tests updated and passing
7. PR merged

**Breaking changes require 1-week notice minimum.**

---

## Appendix A: Service Owner Checklist

Use this checklist before marking your service as "Integration Ready":

```markdown
## Pre-Integration Checklist for {Service Name}

### Code Quality
- [ ] Code follows Google Java Style Guide
- [ ] No TODO comments left in production code
- [ ] No hardcoded secrets or credentials
- [ ] Logging implemented (INFO, WARN, ERROR levels)

### API Contract
- [ ] OpenAPI spec exists in shared/contracts/{service}.yaml
- [ ] All endpoints documented
- [ ] Request/Response examples provided
- [ ] Error responses documented

### Events
- [ ] Event schemas exist in shared/event-schemas/{service}-events.json
- [ ] All events follow envelope structure
- [ ] Events tested with message queue

### Testing
- [ ] Unit tests: 80%+ coverage
- [ ] Integration tests: all endpoints covered
- [ ] Contract tests: passing
- [ ] Manual testing: all flows verified

### Documentation
- [ ] README.md updated
- [ ] Setup instructions work
- [ ] API documentation accessible at /swagger-ui.html
- [ ] Environment variables documented

### Infrastructure
- [ ] Dockerfile works
- [ ] docker-compose.yml works
- [ ] Health check endpoint responds
- [ ] Graceful shutdown implemented

### Security
- [ ] JWT validation working
- [ ] Role-based access implemented
- [ ] No SQL injection vulnerabilities
- [ ] Input validation on all endpoints
```

---

## Appendix B: Quick Reference

### Port Reference
```
User Service:           3001
Product Service:        3002
Inventory Service:      3003
Order Service:          3004
Payment Service:        3005
Shipping Service:       3006
Review Service:         3007
Recommendation Service: 3008
Notification Service:   3009
Analytics Service:      3010
API Gateway:            8000
RabbitMQ Management:    15672
```

### Environment Variables (All Services)
```
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://localhost:5432/shopsphere
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
JWT_SECRET={shared-secret}
SERVICE_SECRET={service-to-service-secret}
```

---

**Document Version History:**

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0.0 | 2026-02-14 | LakshanDulhara | Initial version |
