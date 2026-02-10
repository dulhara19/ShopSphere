# Inventory Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Inventory Service, divided into two phases:
- **Phase 1 (MVP)**: Core inventory tracking and reservation
- **Phase 2**: Multi-warehouse, analytics, and ML predictions

**Owner:** Team Member 3
**Port:** 3003
**Tech Stack:** Spring Boot, Spring Data JPA, PostgreSQL, Redis

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential inventory tracking and reservation capabilities for checkout flow.

### Epic 1.1: Basic Inventory Management

**Priority:** Critical
**Dependency:** Product Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Initialize inventory for product | - Create inventory record when product created<br>- Default quantity: 0<br>- Link to productId |
| 1.1.2 | Get stock level by product ID | - Return current quantity<br>- Return availability status |
| 1.1.3 | Update stock quantity (Seller) | - Add or set stock quantity<br>- Validate non-negative<br>- Record update timestamp |
| 1.1.4 | Bulk stock update | - Update multiple products at once<br>- CSV import support |
| 1.1.5 | Delete inventory record | - When product is deleted<br>- Handle via event listener |

**API Endpoints:**
```
POST   /api/inventory
GET    /api/inventory/{productId}
PUT    /api/inventory/{productId}
POST   /api/inventory/bulk-update
DELETE /api/inventory/{productId}
```

**Inventory Entity Fields (MVP):**
```
- id (UUID)
- productId (unique)
- quantity
- reservedQuantity
- availableQuantity (computed: quantity - reserved)
- lowStockThreshold
- status (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
- lastUpdated
- createdAt
```

---

### Epic 1.2: Stock Reservation System

**Priority:** Critical
**Dependency:** Epic 1.1, Order Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Reserve stock for checkout | - Decrease available quantity<br>- Increase reserved quantity<br>- Return reservation ID<br>- Fail if insufficient stock |
| 1.2.2 | Confirm reservation (order placed) | - Convert reserved to sold<br>- Decrease total quantity<br>- Clear reservation |
| 1.2.3 | Release reservation (timeout/cancel) | - Return reserved to available<br>- Auto-release after 15 minutes |
| 1.2.4 | Check availability | - Return true/false for quantity<br>- Support batch check |
| 1.2.5 | Reservation expiry job | - Scheduled job every 5 minutes<br>- Release expired reservations |

**API Endpoints:**
```
POST /api/inventory/reserve
     Body: { "productId": "xxx", "quantity": 2 }
     Response: { "reservationId": "xxx", "expiresAt": "..." }

POST /api/inventory/confirm
     Body: { "reservationId": "xxx" }

POST /api/inventory/release
     Body: { "reservationId": "xxx" }

POST /api/inventory/check-availability
     Body: [{ "productId": "xxx", "quantity": 2 }, ...]
```

---

### Epic 1.3: Low Stock Alerts

**Priority:** High
**Dependency:** Epic 1.1, Notification Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Set low stock threshold | - Per product threshold<br>- Default threshold configurable |
| 1.3.2 | Detect low stock | - Trigger when quantity <= threshold<br>- Update status to LOW_STOCK |
| 1.3.3 | Send low stock notification | - Notify seller via Notification Service<br>- Include product details |
| 1.3.4 | Get low stock products | - List all low stock items<br>- Filter by seller |
| 1.3.5 | Out of stock handling | - Update status to OUT_OF_STOCK<br>- Notify Product Service |

**API Endpoints:**
```
PUT  /api/inventory/{productId}/threshold
GET  /api/inventory/low-stock
GET  /api/inventory/out-of-stock
```

---

### Epic 1.4: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.1, 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Get stock for product (internal) | - For Product Service display<br>- Return quantity, status |
| 1.4.2 | Batch get stock levels | - For cart/order display<br>- Return list of stock info |
| 1.4.3 | Stock update events | - Publish stock.updated event<br>- Publish stock.low event |

**Internal API Endpoints:**
```
GET  /internal/inventory/{productId}
POST /internal/inventory/batch
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add multi-warehouse support, detailed tracking, and predictive features.

### Epic 2.1: Multi-Warehouse Support

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Create warehouse | - Name, location, address<br>- Unique warehouse code |
| 2.1.2 | Assign inventory to warehouse | - Track stock per warehouse<br>- Same product in multiple warehouses |
| 2.1.3 | Warehouse-level operations | - Update stock per warehouse<br>- Transfer between warehouses |
| 2.1.4 | Nearest warehouse selection | - Based on shipping address<br>- For fulfillment optimization |
| 2.1.5 | Warehouse stock aggregation | - Total stock across warehouses<br>- Per-warehouse breakdown |

**API Endpoints:**
```
POST   /api/warehouses
GET    /api/warehouses
GET    /api/warehouses/{id}
PUT    /api/warehouses/{id}
POST   /api/inventory/{productId}/warehouses/{warehouseId}
GET    /api/inventory/{productId}/warehouses
POST   /api/inventory/transfer
```

---

### Epic 2.2: Stock History & Audit Trail

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Log all stock changes | - Record: timestamp, quantity change, reason, userId |
| 2.2.2 | View stock history | - Filter by date range<br>- Filter by change type |
| 2.2.3 | Stock adjustment reasons | - SALE, RETURN, RESTOCK, DAMAGE, ADJUSTMENT |
| 2.2.4 | Audit report generation | - Export stock history<br>- CSV/PDF format |
| 2.2.5 | Stock reconciliation | - Compare expected vs actual<br>- Flag discrepancies |

**API Endpoints:**
```
GET  /api/inventory/{productId}/history
GET  /api/inventory/{productId}/history?from={date}&to={date}
POST /api/inventory/{productId}/adjustment
GET  /api/inventory/audit-report
```

---

### Epic 2.3: Inventory Analytics

**Priority:** Medium
**Dependency:** Epic 2.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Stock turnover rate | - Calculate inventory turnover<br>- Per product, per category |
| 2.3.2 | Days of inventory | - Estimate days until stockout<br>- Based on sales velocity |
| 2.3.3 | Dead stock identification | - Products with no movement<br>- Configurable threshold |
| 2.3.4 | Inventory valuation | - Total inventory value<br>- By warehouse, category |

**API Endpoints:**
```
GET /api/inventory/analytics/turnover
GET /api/inventory/analytics/days-remaining
GET /api/inventory/analytics/dead-stock
GET /api/inventory/analytics/valuation
```

---

### Epic 2.4: Restock Predictions (ML)

**Priority:** Low
**Dependency:** Epic 2.2, 2.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Sales velocity calculation | - Average daily/weekly sales<br>- Trend analysis |
| 2.4.2 | Restock recommendations | - Suggest reorder quantity<br>- Suggest reorder date |
| 2.4.3 | Demand forecasting | - Predict future demand<br>- Consider seasonality |
| 2.4.4 | Auto-restock alerts | - Notify when restock needed<br>- Based on lead time |

**API Endpoints:**
```
GET /api/inventory/{productId}/restock-recommendation
GET /api/inventory/forecasts
```

---

### Epic 2.5: Inventory Sync & Integration

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Real-time stock sync | - WebSocket for live updates<br>- Push stock changes |
| 2.5.2 | External system integration | - API for third-party inventory systems<br>- Webhook support |
| 2.5.3 | Batch import/export | - Import from CSV/Excel<br>- Export current inventory |

**API Endpoints:**
```
WS   /api/inventory/stream
POST /api/inventory/import
GET  /api/inventory/export
POST /api/inventory/webhooks
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
| Product Service | Inbound | Product creation triggers inventory init |
| Order Service | Inbound | Reserve/confirm/release stock |
| Notification Service | Outbound | Low stock alerts |
| Shipping Service | Outbound | Warehouse selection for fulfillment |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `inventory.updated` | Stock level changed | Product, Analytics |
| `inventory.low` | Stock below threshold | Notification, Analytics |
| `inventory.out` | Stock reached zero | Product, Notification |
| `inventory.reserved` | Stock reserved | Order |
| `inventory.released` | Reservation released | Order |
