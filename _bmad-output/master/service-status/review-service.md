---
service: review-service
owner: Team Member 7
port: 3007
branch: service/review-service
lastUpdated: 2026-02-14
lastReviewedPR: null
mvpStatus: NOT_STARTED
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# Review Service - Master Status Tracker

> **MASTER copy. Slave must create `services/review-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | Review Service |
| Owner | Team Member 7 |
| Port | 3007 |
| Branch | `service/review-service` |
| Tech Stack | Spring Boot, Spring Data MongoDB, Redis, AWS S3 |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/4 Epics Complete |
| EPICS.md Created | **NO - ACTION REQUIRED** |
| Contract Created | **NO - ACTION REQUIRED** |

---

## Phase 1 - MVP Epics (Expected)

### Epic 1.1: Review CRUD
- [ ] Create review (verified purchase)
- [ ] Get reviews by product
- [ ] Update own review
- [ ] Delete own review
- [ ] Review with photos/videos

### Epic 1.2: Rating System
- [ ] 1-5 star rating
- [ ] Calculate average rating
- [ ] Rating breakdown display
- [ ] Update Product Service with rating

### Epic 1.3: Review Moderation
- [ ] Admin review approval
- [ ] Spam detection
- [ ] Report inappropriate review
- [ ] Review filtering

### Epic 1.4: Helpful Votes
- [ ] Mark review helpful
- [ ] Sort by helpfulness
- [ ] Verified purchase badge

---

## Expected Events

**Publish:**
- `review.created`
- `review.updated`
- `review.deleted`

**Consume:**
- `order.delivered` → Prompt for review

---

## Action Items for Team Member 7

1. [ ] Create `services/review-service/docs/EPICS.md`
2. [ ] Create `shared/contracts/review-service.yaml`
3. [ ] Create `shared/event-schemas/review-events.json`
4. [ ] Get approval from Team Lead

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
