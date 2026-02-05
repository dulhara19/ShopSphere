# User Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the User Service, divided into two phases:
- **Phase 1 (MVP)**: Core functionality required for the platform to operate
- **Phase 2**: Enhanced features to improve user experience and security

**Owner:** Team Member 1
**Port:** 3001
**Tech Stack:** Spring Boot, Spring Security, PostgreSQL, Redis, JWT

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential authentication and user management capabilities that other services depend on.

### Epic 1.1: User Registration & Authentication

**Priority:** Critical
**Dependency:** None (foundational)

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | User registration with email/password | - Validate email format and uniqueness<br>- Hash password with BCrypt<br>- Return user ID on success |
| 1.1.2 | User login with JWT token generation | - Validate credentials<br>- Generate access token (15min expiry)<br>- Generate refresh token (7 days expiry) |
| 1.1.3 | Token refresh endpoint | - Validate refresh token<br>- Issue new access token<br>- Rotate refresh token |
| 1.1.4 | Logout functionality | - Invalidate refresh token<br>- Add token to blacklist (Redis) |

**API Endpoints:**
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
```

---

### Epic 1.2: User Profile Management

**Priority:** Critical
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Get user profile by ID | - Return user details (excluding password)<br>- Handle user not found (404) |
| 1.2.2 | Update user profile | - Allow updates to: name, phone, address<br>- Validate input fields<br>- Return updated profile |
| 1.2.3 | Get current user profile | - Extract user from JWT token<br>- Return authenticated user's profile |
| 1.2.4 | Delete user account | - Soft delete (mark as inactive)<br>- Invalidate all tokens |

**API Endpoints:**
```
GET    /api/users/me
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

**User Entity Fields (MVP):**
```
- id (UUID)
- email (unique)
- passwordHash
- firstName
- lastName
- phone
- role
- status (ACTIVE, INACTIVE, SUSPENDED)
- createdAt
- updatedAt
```

---

### Epic 1.3: Role-Based Access Control (RBAC)

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Define user roles | - CUSTOMER: Default role for buyers<br>- SELLER: Can list products<br>- ADMIN: Full system access |
| 1.3.2 | Role assignment during registration | - Default to CUSTOMER role<br>- Allow SELLER registration with verification flag |
| 1.3.3 | Role-based endpoint protection | - Implement @PreAuthorize annotations<br>- Return 403 for unauthorized access |
| 1.3.4 | Admin: Manage user roles | - Admin can change user roles<br>- Admin can suspend/activate users |

**API Endpoints:**
```
PUT  /api/admin/users/{id}/role    (Admin only)
PUT  /api/admin/users/{id}/status  (Admin only)
GET  /api/admin/users              (Admin only - list all users)
```

---

### Epic 1.4: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.1, 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Validate token endpoint (internal) | - Other services can validate JWT<br>- Return user ID and role |
| 1.4.2 | Get user by ID (internal) | - Internal endpoint for other services<br>- No auth required (service-to-service) |
| 1.4.3 | Batch get users by IDs | - Accept list of user IDs<br>- Return list of user summaries |

**Internal API Endpoints:**
```
POST /internal/auth/validate
GET  /internal/users/{id}
POST /internal/users/batch
```

---

## Phase 2 - Enhanced Features

> **Goal:** Improve security, user experience, and add social login capabilities.

### Epic 2.1: OAuth2 Social Login

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Google OAuth2 integration | - Redirect to Google login<br>- Handle callback and create/link account<br>- Generate JWT tokens |
| 2.1.2 | Facebook OAuth2 integration | - Redirect to Facebook login<br>- Handle callback and create/link account |
| 2.1.3 | Account linking | - Link social account to existing email account<br>- Support multiple social providers per user |
| 2.1.4 | Unlink social account | - Remove social provider from account<br>- Require password if last auth method |

**API Endpoints:**
```
GET  /api/auth/oauth2/google
GET  /api/auth/oauth2/google/callback
GET  /api/auth/oauth2/facebook
GET  /api/auth/oauth2/facebook/callback
POST /api/users/me/link-social
DELETE /api/users/me/unlink-social/{provider}
```

---

### Epic 2.2: Email Verification & Password Reset

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Send verification email on registration | - Generate verification token<br>- Send email with verification link<br>- Token expires in 24 hours |
| 2.2.2 | Verify email endpoint | - Validate token<br>- Mark email as verified<br>- Handle expired tokens |
| 2.2.3 | Resend verification email | - Rate limit (1 per 5 min)<br>- Generate new token |
| 2.2.4 | Forgot password - request reset | - Send reset link to email<br>- Token expires in 1 hour |
| 2.2.5 | Reset password with token | - Validate reset token<br>- Update password<br>- Invalidate all sessions |

**API Endpoints:**
```
POST /api/auth/verify-email
POST /api/auth/resend-verification
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

---

### Epic 2.3: Advanced Security & Session Management

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Refresh token rotation | - Issue new refresh token on each refresh<br>- Invalidate old refresh token |
| 2.3.2 | Active sessions management | - List user's active sessions<br>- Show device/browser info<br>- Revoke specific sessions |
| 2.3.3 | Account lockout policy | - Lock after 5 failed attempts<br>- Auto-unlock after 15 minutes<br>- Admin can unlock manually |
| 2.3.4 | Change password | - Require current password<br>- Invalidate all other sessions |
| 2.3.5 | Audit logging | - Log login attempts<br>- Log password changes<br>- Log role changes |

**API Endpoints:**
```
GET    /api/users/me/sessions
DELETE /api/users/me/sessions/{sessionId}
DELETE /api/users/me/sessions/all
PUT    /api/users/me/password
GET    /api/admin/users/{id}/audit-log  (Admin only)
```

---

### Epic 2.4: User Address Management

**Priority:** Low
**Dependency:** Epic 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Add shipping address | - Multiple addresses per user<br>- Mark one as default |
| 2.4.2 | Update/delete address | - Edit address details<br>- Delete address |
| 2.4.3 | Set default address | - Only one default at a time |

**API Endpoints:**
```
GET    /api/users/me/addresses
POST   /api/users/me/addresses
PUT    /api/users/me/addresses/{id}
DELETE /api/users/me/addresses/{id}
PUT    /api/users/me/addresses/{id}/default
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
| Notification Service | Outbound | Send verification/reset emails |
| All Services | Inbound | Token validation, user data lookup |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `user.registered` | New user registration | Notification, Analytics |
| `user.updated` | Profile update | Analytics |
| `user.deleted` | Account deletion | Order, Review, Analytics |
| `user.role.changed` | Role modification | Product (seller status) |
