# Shipping Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Shipping Service, divided into two phases:
- **Phase 1 (MVP)**: Core shipping rate calculation and tracking
- **Phase 2**: Multi-carrier support and advanced features

**Owner:** Team Member 6
**Port:** 3006
**Tech Stack:** Spring Boot, Spring Data JPA, PostgreSQL, Third-party Carrier APIs

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential shipping rate calculation and order tracking capabilities.

### Epic 1.1: Address Management & Validation

**Priority:** Critical
**Dependency:** User Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Validate shipping address | - Check required fields<br>- Validate postal code format<br>- Validate country code |
| 1.1.2 | Address standardization | - Normalize address format<br>- Correct common typos<br>- Use address verification API |
| 1.1.3 | Get shipping zones | - Define shipping zones by country/region<br>- Zone-based rate lookup |
| 1.1.4 | Restricted addresses | - Block PO boxes if needed<br>- Block unsupported countries |

**API Endpoints:**
```
POST /api/shipping/validate-address
     Body: {
       "line1": "123 Main St",
       "line2": "Apt 4",
       "city": "New York",
       "state": "NY",
       "postalCode": "10001",
       "country": "US"
     }
     Response: { "valid": true, "standardized": {...} }

GET  /api/shipping/zones
GET  /api/shipping/zones/{country}
```

---

### Epic 1.2: Shipping Rate Calculation

**Priority:** Critical
**Dependency:** Epic 1.1, Order Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Calculate shipping cost | - Based on weight, dimensions<br>- Based on destination zone<br>- Return multiple options |
| 1.2.2 | Flat rate shipping | - Configure flat rates per zone<br>- Free shipping threshold |
| 1.2.3 | Weight-based shipping | - Rate per kg/lb<br>- Tiered pricing |
| 1.2.4 | Real-time carrier rates | - Query carrier API for rates<br>- Cache rates temporarily |
| 1.2.5 | Shipping options | - Standard, Express, Overnight<br>- Estimated delivery dates |

**API Endpoints:**
```
POST /api/shipping/calculate-rate
     Body: {
       "origin": { "postalCode": "90210", "country": "US" },
       "destination": { "postalCode": "10001", "country": "US" },
       "package": {
         "weight": 2.5,
         "dimensions": { "length": 10, "width": 8, "height": 4 }
       }
     }
     Response: {
       "options": [
         { "carrier": "FEDEX", "service": "GROUND", "rate": 8.99, "estimatedDays": 5 },
         { "carrier": "FEDEX", "service": "EXPRESS", "rate": 15.99, "estimatedDays": 2 }
       ]
     }

GET /api/shipping/rates/flat
GET /api/shipping/free-shipping-threshold
```

**Shipping Rate Entity:**
```
- id (UUID)
- zone
- minWeight
- maxWeight
- baseRate
- perKgRate
- carrier
- serviceType
- estimatedDays
- isActive
```

---

### Epic 1.3: Shipment Creation

**Priority:** Critical
**Dependency:** Epic 1.2, Order Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Create shipment for order | - Link to order ID<br>- Store selected shipping option<br>- Set status to PENDING |
| 1.3.2 | Generate tracking number | - Unique tracking number<br>- Carrier-specific format |
| 1.3.3 | Get shipment details | - Return shipment info<br>- Include tracking number, status |
| 1.3.4 | Update shipment status | - PENDING → LABEL_CREATED → PICKED_UP → IN_TRANSIT → DELIVERED |
| 1.3.5 | Cancel shipment | - Before pickup only<br>- Void tracking number |

**API Endpoints:**
```
POST /api/shipments
     Body: {
       "orderId": "xxx",
       "carrier": "FEDEX",
       "service": "GROUND",
       "origin": {...},
       "destination": {...},
       "package": {...}
     }

GET    /api/shipments/{id}
GET    /api/shipments/order/{orderId}
PUT    /api/shipments/{id}/status
DELETE /api/shipments/{id}
```

**Shipment Entity:**
```
- id (UUID)
- orderId
- trackingNumber
- carrier
- serviceType
- status (PENDING, LABEL_CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, RETURNED)
- origin (embedded)
- destination (embedded)
- package (weight, dimensions)
- shippingCost
- labelUrl
- estimatedDelivery
- actualDelivery
- createdAt
- updatedAt
```

---

### Epic 1.4: Shipment Tracking

**Priority:** High
**Dependency:** Epic 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Track by tracking number | - Return current status<br>- Return status history |
| 1.4.2 | Status history | - Log all status changes<br>- Include timestamp, location |
| 1.4.3 | Public tracking page | - No auth required<br>- Display friendly status |
| 1.4.4 | Estimated delivery update | - Update based on tracking events<br>- Handle delays |

**API Endpoints:**
```
GET /api/tracking/{trackingNumber}
    Response: {
      "trackingNumber": "xxx",
      "carrier": "FEDEX",
      "status": "IN_TRANSIT",
      "estimatedDelivery": "2024-01-15",
      "history": [
        { "status": "PICKED_UP", "location": "Los Angeles, CA", "timestamp": "..." },
        { "status": "IN_TRANSIT", "location": "Phoenix, AZ", "timestamp": "..." }
      ]
    }

GET /api/tracking/{trackingNumber}/history
```

---

### Epic 1.5: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Notify order of shipment | - Send tracking number to Order Service<br>- Update order status to SHIPPED |
| 1.5.2 | Delivery confirmation | - Notify Order Service on delivery<br>- Trigger delivery event |
| 1.5.3 | Get rates for checkout | - Internal endpoint for Order Service<br>- Return available options |

**Internal API Endpoints:**
```
POST /internal/shipping/rates
GET  /internal/shipments/order/{orderId}
POST /internal/shipments/{id}/delivered
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add multi-carrier support, label generation, and advanced tracking.

### Epic 2.1: Multi-Carrier Integration

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | FedEx API integration | - Rate quotes<br>- Shipment creation<br>- Tracking |
| 2.1.2 | UPS API integration | - Rate quotes<br>- Shipment creation<br>- Tracking |
| 2.1.3 | DHL API integration | - International shipping<br>- Rate quotes<br>- Tracking |
| 2.1.4 | Carrier selection logic | - Best rate selection<br>- Fastest option selection<br>- Preferred carrier setting |
| 2.1.5 | Carrier account management | - Store merchant carrier accounts<br>- Negotiated rates |

**API Endpoints:**
```
GET    /api/carriers
POST   /api/carriers/accounts
GET    /api/carriers/accounts
DELETE /api/carriers/accounts/{id}
```

---

### Epic 2.2: Shipping Label Generation

**Priority:** High
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Generate shipping label | - Create label via carrier API<br>- Return PDF/PNG |
| 2.2.2 | Batch label generation | - Multiple labels at once<br>- Combined PDF |
| 2.2.3 | Label reprint | - Regenerate existing label |
| 2.2.4 | Return label generation | - Pre-paid return labels<br>- Include in package |
| 2.2.5 | Label customization | - Add logo<br>- Custom messages |

**API Endpoints:**
```
POST /api/shipments/{id}/label
GET  /api/shipments/{id}/label
POST /api/shipments/batch-labels
POST /api/shipments/{id}/return-label
```

---

### Epic 2.3: Real-Time Tracking Integration

**Priority:** Medium
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Carrier webhook setup | - Receive real-time updates from carriers |
| 2.3.2 | Tracking sync job | - Poll carriers for updates<br>- Handle carriers without webhooks |
| 2.3.3 | Delivery notifications | - Push notification on status change<br>- SMS/Email alerts |
| 2.3.4 | Delivery photo/signature | - Capture proof of delivery<br>- Store and display |

**API Endpoints:**
```
POST /api/webhooks/fedex
POST /api/webhooks/ups
POST /api/webhooks/dhl
GET  /api/shipments/{id}/proof-of-delivery
```

---

### Epic 2.4: Pickup Scheduling

**Priority:** Medium
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Schedule carrier pickup | - Request pickup from carrier<br>- Specify date/time window |
| 2.4.2 | Pickup confirmation | - Receive confirmation number<br>- Track pickup status |
| 2.4.3 | Cancel pickup | - Cancel before pickup time |
| 2.4.4 | Recurring pickups | - Daily/weekly scheduled pickups |

**API Endpoints:**
```
POST   /api/pickups
GET    /api/pickups/{id}
DELETE /api/pickups/{id}
GET    /api/pickups/scheduled
```

---

### Epic 2.5: Returns Management

**Priority:** Medium
**Dependency:** Epic 2.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Create return shipment | - Generate return label<br>- Link to original shipment |
| 2.5.2 | Track return | - Monitor return in transit<br>- Notify on delivery |
| 2.5.3 | Return to warehouse | - Route to appropriate warehouse<br>- Update inventory on receipt |
| 2.5.4 | Return analytics | - Track return rates<br>- Common return reasons |

**API Endpoints:**
```
POST /api/shipments/{id}/return
GET  /api/returns
GET  /api/returns/{returnId}
```

---

### Epic 2.6: International Shipping

**Priority:** Low
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.6.1 | Customs documentation | - Generate customs forms<br>- HS codes lookup |
| 2.6.2 | Duties & taxes calculation | - Calculate import duties<br>- DDP vs DDU options |
| 2.6.3 | Restricted items check | - Validate items can be shipped<br>- Country-specific restrictions |
| 2.6.4 | International tracking | - Track across borders<br>- Handle handoffs |

**API Endpoints:**
```
POST /api/shipping/customs/calculate
GET  /api/shipping/restrictions/{country}
POST /api/shipments/{id}/customs-docs
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
| Order Service | Inbound/Outbound | Order details, shipping updates |
| User Service | Inbound | Shipping addresses |
| Inventory Service | Outbound | Warehouse selection |
| Notification Service | Outbound | Shipping notifications |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `shipment.created` | New shipment | Order, Analytics |
| `shipment.shipped` | Package picked up | Order, Notification |
| `shipment.in_transit` | Status update | Notification |
| `shipment.delivered` | Package delivered | Order, Notification, Review |
| `shipment.returned` | Return received | Order, Inventory |
