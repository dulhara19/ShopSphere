---
service: user-service
owner: Team Member 1
port: 3001
branch: service/user-service
lastUpdated: 2026-02-17
lastReviewedPR: e683082-phase1
mvpStatus: FOUNDATION_ONLY
integrationStatus: PENDING
frontendStatus: NOT_INTEGRATED
environment: null
---

# User Service - Master Status Tracker

> **MASTER copy. Slave must create `services/user-service/docs/EPICS.md`**

## Service Overview

| Property | Value |
|----------|-------|
| Service | User Service |
| Owner | Team Member 1 |
| Port | 3001 |
| Branch | `service/user-service` |
| Tech Stack | Spring Boot, Spring Security, PostgreSQL, Redis, JWT |

## Current Status Summary

| Metric | Value |
|--------|-------|
| MVP Progress | 0/4 Epics Complete |
| Stories Complete | 0/? |
| Test Coverage | 0% |
| Contract Compliance | Not Verified |
| EPICS.md Created | **YES** |

---

## Phase 1 - MVP Epics (Expected)

> **Note:** Team Member 1 must create `services/user-service/docs/EPICS.md` with detailed stories

### Epic 1.1: User Registration & Login

**Status:** `NOT_STARTED`
**Priority:** Critical

**Expected Stories:**
- [ ] User registration with email validation
- [ ] User login with credentials
- [ ] Password hashing (bcrypt)
- [ ] Email verification flow
- [ ] Password reset flow

**Expected API Endpoints:**
- [ ] `POST /api/auth/register`
- [ ] `POST /api/auth/login`
- [ ] `POST /api/auth/verify-email`
- [ ] `POST /api/auth/forgot-password`
- [ ] `POST /api/auth/reset-password`

---

### Epic 1.2: JWT Authentication

**Status:** `NOT_STARTED`
**Priority:** Critical

**Expected Stories:**
- [ ] JWT token generation
- [ ] JWT token validation
- [ ] Refresh token mechanism
- [ ] Token blacklisting (logout)

**Expected API Endpoints:**
- [ ] `POST /api/auth/refresh`
- [ ] `POST /api/auth/logout`

---

### Epic 1.3: Profile Management

**Status:** `NOT_STARTED`
**Priority:** High

**Expected Stories:**
- [ ] Get user profile
- [ ] Update user profile
- [ ] Upload profile picture
- [ ] Manage addresses
- [ ] Change password

**Expected API Endpoints:**
- [ ] `GET /api/users/me`
- [ ] `PUT /api/users/me`
- [ ] `POST /api/users/me/avatar`
- [ ] `GET /api/users/me/addresses`
- [ ] `POST /api/users/me/addresses`
- [ ] `PUT /api/users/me/addresses/{id}`
- [ ] `DELETE /api/users/me/addresses/{id}`
- [ ] `PUT /api/users/me/password`

---

### Epic 1.4: Role-Based Access Control

**Status:** `NOT_STARTED`
**Priority:** High

**Expected Stories:**
- [ ] Role definitions (CUSTOMER, SELLER, ADMIN)
- [ ] Permission management
- [ ] Admin user management
- [ ] OAuth2 social login (Google, Facebook)

**Expected API Endpoints:**
- [ ] `GET /api/auth/oauth2/{provider}`
- [ ] `GET /api/admin/users`
- [ ] `PUT /api/admin/users/{id}/role`

---

## Contract Compliance

### API Contract: `shared/contracts/user-service.yaml`
**Status:** NOT CREATED - Team Member 1 must create using template

### Event Contract: `shared/event-schemas/user-events.json`
**Status:** NOT CREATED - Team Member 1 must create using template

**Expected Events to Publish:**
- `user.registered`
- `user.updated`
- `user.deleted`
- `user.password_reset`

---

## PR Review History

| PR # | Date | Stories | Decision | Notes |
|------|------|---------|----------|-------|
| e683082 | 2026-02-17 | 1/15 (foundation) | CHANGES REQUESTED | Models & JWT only, no endpoints |

---

## Issues & Improvements

_No reviews conducted yet_

---

## Action Items for Team Member 1

1. [ ] Create `services/user-service/docs/EPICS.md` with detailed stories
2. [ ] Create `shared/contracts/user-service.yaml` using template
3. [ ] Create `shared/event-schemas/user-events.json` using template
4. [ ] Get contracts approved by Team Lead before implementation

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Status file created | Lead |
| 2026-02-17 | Phase 1 PR reviewed - CHANGES REQUESTED | Master Agent |
