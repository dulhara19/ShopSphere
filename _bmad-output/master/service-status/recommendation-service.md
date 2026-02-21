---
service: recommendation-service
owner: Team Member 8
port: 3008
branch: service/recommendation-service
lastUpdated: 2026-02-20
lastReviewedPR: service/recommendation-service-2026-02-20
mvpStatus: EPICS_DEFINED
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# Recommendation Service - Master Status Tracker

> **MASTER copy. Slave must create `services/recommendation-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | Recommendation Service |
| Owner | Team Member 8 |
| Port | 3008 |
| Branch | `service/recommendation-service` |
| Tech Stack | Spring Boot, Spring AI, DJL, MongoDB, Redis |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/3 Epics Complete |
| EPICS.md Created | **YES** |
| Contract Created | **YES** |
| Event Schema | **NO - ACTION REQUIRED** |
| PR Status | **APPROVED** (EPICS.md) |

---

## Phase 1 - MVP Epics (Expected)

### Epic 1.1: Similar Products
- [ ] Find similar products
- [ ] Based on category/attributes
- [ ] Product page suggestions

### Epic 1.2: Personalized Recommendations
- [ ] User browsing history
- [ ] Purchase history
- [ ] "For You" section
- [ ] Collaborative filtering

### Epic 1.3: Trending Products
- [ ] Popular products
- [ ] Trending by category
- [ ] Time-based trends

### Epic 1.4: Recently Viewed
- [ ] Track viewed products
- [ ] Recently viewed list
- [ ] Continue shopping

---

## Expected Events

**Consume:**
- `product.viewed` → Track for recommendations
- `order.created` → Track purchases
- `cart.updated` → Track cart activity

---

## Action Items for Team Member 8

1. [x] Create `services/recommendation-service/docs/EPICS.md` - **DONE**
2. [x] Create `shared/contracts/recommendation-service.yaml` - **DONE**
3. [ ] Create `shared/event-schemas/recommendation-events.json`
4. [ ] Begin Epic 1.1 (User Behavior Tracking) implementation
5. [ ] Include unit tests (80% coverage target)

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
| 2026-02-20 | EPICS.md PR Review - APPROVED | Master Agent |
