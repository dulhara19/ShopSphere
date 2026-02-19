---
service: shipping-service
owner: Team Member 6
port: 3006
branch: service/shipping-service
lastUpdated: 2026-02-14
lastReviewedPR: null
mvpStatus: NOT_STARTED
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
| MVP Progress | 0/4 Epics Complete |
| EPICS.md Created | **NO - ACTION REQUIRED** |
| Contract Created | **NO - ACTION REQUIRED** |

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

1. [ ] Create `services/shipping-service/docs/EPICS.md`
2. [ ] Create `shared/contracts/shipping-service.yaml`
3. [ ] Create `shared/event-schemas/shipping-events.json`
4. [ ] Get approval from Team Lead

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
