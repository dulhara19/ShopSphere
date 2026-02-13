---
service: inventory-service
owner: Team Member 3
port: 3003
branch: service/inventory-service
lastUpdated: 2026-02-14
lastReviewedPR: null
mvpStatus: NOT_STARTED
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# Inventory Service - Master Status Tracker

> **MASTER copy. Slave must create `services/inventory-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | Inventory Service |
| Owner | Team Member 3 |
| Port | 3003 |
| Branch | `service/inventory-service` |
| Tech Stack | Spring Boot, Spring Data JPA, PostgreSQL, Redis |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/4 Epics Complete |
| EPICS.md Created | **NO - ACTION REQUIRED** |
| Contract Created | **NO - ACTION REQUIRED** |

---

## Phase 1 - MVP Epics (Expected)

### Epic 1.1: Stock Management
- [ ] Get stock by product ID
- [ ] Update stock levels
- [ ] Bulk stock updates
- [ ] Stock history/audit

### Epic 1.2: Inventory Reservation
- [ ] Reserve stock (checkout)
- [ ] Release reservation (cancel/timeout)
- [ ] Confirm reservation (payment success)
- [ ] Handle partial availability

### Epic 1.3: Stock Alerts
- [ ] Low stock threshold
- [ ] Low stock notifications
- [ ] Out of stock handling

### Epic 1.4: Multi-Warehouse Support
- [ ] Warehouse management
- [ ] Stock per warehouse
- [ ] Nearest warehouse selection

---

## Expected Events

**Publish:**
- `inventory.updated`
- `inventory.reserved`
- `inventory.released`
- `inventory.low_stock`

**Consume:**
- `order.cancelled` → Release reserved stock

---

## Action Items for Team Member 3

1. [ ] Create `services/inventory-service/docs/EPICS.md`
2. [ ] Create `shared/contracts/inventory-service.yaml`
3. [ ] Create `shared/event-schemas/inventory-events.json`
4. [ ] Get approval from Team Lead

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
