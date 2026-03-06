---
service: shipping-service
owner: Team Member 6
port: 3006
branch: service/shipping-service
lastUpdated: 2026-02-20
lastReviewedPR: service/shipping-service-2026-02-20
mvpStatus: EPICS_DEFINED
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# Shipping Service - Master Status Tracker

> **MASTER copy. Slave must create `services/shipping-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | Shipping Service |
| Owner | Team Member 6 |
| Port | 3006 |
| Branch | `service/shipping-service` |
| Tech Stack | Spring Boot, Spring Data JPA, PostgreSQL, Carrier APIs |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/2 Epics Complete |
| EPICS.md Created | **YES** |
| Contract Created | **YES** |
| Event Schema | **NO - ACTION REQUIRED** |
| PR Status | **APPROVED** (EPICS.md) |

---

## Phase 1 - MVP Epics (Expected)

### Epic 1.1: Shipping Rate Calculation
- [ ] Get rates from carriers
- [ ] Rate comparison
- [ ] Address validation
- [ ] Delivery time estimates

### Epic 1.2: Label Generation
- [ ] Create shipping label
- [ ] Store label PDF/image
- [ ] Generate tracking number

### Epic 1.3: Tracking Integration
- [ ] Track shipment status
- [ ] Webhook from carriers
- [ ] Update Order Service

### Epic 1.4: Carrier Management
- [ ] FedEx integration
- [ ] UPS integration
- [ ] DHL integration
- [ ] Carrier selection rules

---

## Expected Events

**Publish:**
- `shipping.label_created`
- `shipping.picked_up`
- `shipping.in_transit`
- `shipping.delivered`

**Consume:**
- `order.confirmed` → Prepare for shipping

---

## Action Items for Team Member 6

1. [x] Create `services/shipping-service/docs/EPICS.md` - **DONE**
2. [x] Create `shared/contracts/shipping-service.yaml` - **DONE**
3. [ ] Create `shared/event-schemas/shipping-events.json`
4. [ ] Begin Epic 1.1 (Address Management & Validation) implementation
5. [ ] Include unit tests (80% coverage target)

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
| 2026-02-20 | EPICS.md PR Review - APPROVED | Master Agent |
