# Notification Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Notification Service, divided into two phases:
- **Phase 1 (MVP)**: Email and in-app notifications
- **Phase 2**: SMS, push notifications, and real-time features

**Owner:** Team Member 9
**Port:** 3009
**Tech Stack:** Spring Boot, Spring WebSocket, Redis, SendGrid, Twilio, Firebase Cloud Messaging

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential email and in-app notification capabilities.

### Epic 1.1: Email Notification System

**Priority:** Critical
**Dependency:** User Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | SendGrid integration | - Configure SendGrid API<br>- Verify sender domain |
| 1.1.2 | Send transactional email | - Accept recipient, subject, body<br>- HTML and plain text support |
| 1.1.3 | Email queue processing | - Queue emails in RabbitMQ<br>- Async processing |
| 1.1.4 | Email delivery tracking | - Track sent, delivered, opened<br>- Handle bounces |
| 1.1.5 | Retry failed emails | - Retry on failure<br>- Max 3 attempts<br>- Dead letter queue |

**API Endpoints:**
```
POST /api/notifications/email
     Body: {
       "to": "user@example.com",
       "templateId": "order-confirmation",
       "data": { "orderNumber": "xxx", ... }
     }

GET  /api/notifications/email/{id}/status
```

---

### Epic 1.2: Email Templates

**Priority:** Critical
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Template storage | - Store templates in database<br>- Version control |
| 1.2.2 | Create/update templates (Admin) | - HTML editor<br>- Variable placeholders |
| 1.2.3 | Template rendering | - Replace variables with data<br>- Support conditionals |
| 1.2.4 | Preview template | - Preview with sample data |
| 1.2.5 | Default templates | - Welcome email<br>- Order confirmation<br>- Password reset<br>- Shipping update |

**API Endpoints:**
```
POST   /api/admin/templates
GET    /api/admin/templates
GET    /api/admin/templates/{id}
PUT    /api/admin/templates/{id}
DELETE /api/admin/templates/{id}
POST   /api/admin/templates/{id}/preview
```

**Default Templates:**
```
- welcome
- email-verification
- password-reset
- order-confirmation
- order-shipped
- order-delivered
- payment-receipt
- payment-failed
- low-stock-alert (seller)
- review-reminder
```

---

### Epic 1.3: In-App Notifications

**Priority:** Critical
**Dependency:** User Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Create notification | - Store in database<br>- Link to user<br>- Type categorization |
| 1.3.2 | Get user notifications | - Paginated results<br>- Filter by read/unread |
| 1.3.3 | Mark as read | - Single notification<br>- Mark all as read |
| 1.3.4 | Delete notification | - Soft delete<br>- Clear all |
| 1.3.5 | Unread count | - Return unread count<br>- For badge display |

**API Endpoints:**
```
GET    /api/notifications?page=0&read=false
GET    /api/notifications/unread-count
PUT    /api/notifications/{id}/read
PUT    /api/notifications/read-all
DELETE /api/notifications/{id}
DELETE /api/notifications/clear-all
```

**Notification Entity:**
```
- id (UUID)
- userId
- type (ORDER, PAYMENT, SHIPPING, PROMO, SYSTEM)
- title
- message
- data (JSON - additional context)
- isRead
- link (optional - click action)
- createdAt
- readAt
```

---

### Epic 1.4: Notification Preferences

**Priority:** High
**Dependency:** User Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Get user preferences | - Return all preference settings |
| 1.4.2 | Update preferences | - Toggle notification types<br>- Toggle channels (email, sms, push) |
| 1.4.3 | Default preferences | - Set on user creation<br>- Sensible defaults |
| 1.4.4 | Unsubscribe link | - One-click unsubscribe in emails<br>- Update preferences |
| 1.4.5 | Preference check | - Check before sending<br>- Respect user settings |

**API Endpoints:**
```
GET /api/notifications/preferences
PUT /api/notifications/preferences
    Body: {
      "email": {
        "orderUpdates": true,
        "promotions": false,
        "newsletter": false
      },
      "inApp": {
        "orderUpdates": true,
        "promotions": true
      }
    }

GET /api/notifications/unsubscribe?token={token}
```

---

### Epic 1.5: Event-Driven Notifications

**Priority:** High
**Dependency:** Epic 1.1, 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Order events listener | - order.created → confirmation email<br>- order.shipped → shipping email |
| 1.5.2 | Payment events listener | - payment.succeeded → receipt<br>- payment.failed → failure notice |
| 1.5.3 | User events listener | - user.registered → welcome email<br>- password reset request |
| 1.5.4 | Inventory events listener | - low stock → seller notification |
| 1.5.5 | Review events listener | - review on seller's product → notify seller |

**Events Consumed:**
```
- user.registered → Welcome email
- user.password-reset-requested → Reset email
- order.created → Order confirmation
- order.confirmed → Payment receipt
- order.shipped → Shipping notification
- order.delivered → Delivery + review prompt
- payment.failed → Payment failure notice
- inventory.low → Low stock alert (seller)
- review.created → New review notification (seller)
```

---

### Epic 1.6: Internal Notification API

**Priority:** High
**Dependency:** Epic 1.1, 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.6.1 | Send notification (internal) | - Other services can trigger notifications<br>- Specify channel(s) |
| 1.6.2 | Bulk notifications | - Send to multiple users<br>- For promotions, announcements |
| 1.6.3 | Scheduled notifications | - Send at specific time<br>- Cancel scheduled |

**Internal API Endpoints:**
```
POST /internal/notifications/send
     Body: {
       "userId": "xxx",
       "channels": ["EMAIL", "IN_APP"],
       "templateId": "order-shipped",
       "data": { ... }
     }

POST /internal/notifications/bulk
POST /internal/notifications/schedule
DELETE /internal/notifications/scheduled/{id}
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add SMS, push notifications, real-time updates, and advanced features.

### Epic 2.1: SMS Notifications

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Twilio integration | - Configure Twilio credentials<br>- Verify phone numbers |
| 2.1.2 | Send SMS | - Text message delivery<br>- Handle character limits |
| 2.1.3 | SMS templates | - Short message templates<br>- Variable substitution |
| 2.1.4 | SMS delivery tracking | - Track delivery status<br>- Handle failures |
| 2.1.5 | OTP via SMS | - One-time passwords<br>- For 2FA, verification |

**API Endpoints:**
```
POST /api/notifications/sms
     Body: {
       "to": "+1234567890",
       "templateId": "order-shipped-sms",
       "data": { ... }
     }

POST /api/notifications/sms/otp
     Body: { "phone": "+1234567890" }
```

---

### Epic 2.2: Push Notifications

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Firebase Cloud Messaging setup | - Configure FCM credentials |
| 2.2.2 | Register device token | - Store device tokens per user<br>- Handle multiple devices |
| 2.2.3 | Send push notification | - Title, body, data payload<br>- Deep link support |
| 2.2.4 | Topic-based push | - Subscribe users to topics<br>- Broadcast to topics |
| 2.2.5 | Push delivery tracking | - Track delivery, opens |
| 2.2.6 | Unregister device | - Remove token on logout<br>- Handle invalid tokens |

**API Endpoints:**
```
POST   /api/notifications/push/register
       Body: { "token": "xxx", "platform": "ANDROID" }

DELETE /api/notifications/push/unregister
       Body: { "token": "xxx" }

POST   /api/notifications/push/send
       Body: {
         "userId": "xxx",
         "title": "Order Shipped",
         "body": "Your order is on the way!",
         "data": { "orderId": "xxx" }
       }
```

---

### Epic 2.3: Real-Time WebSocket Notifications

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | WebSocket connection | - Authenticate connection<br>- Per-user channels |
| 2.3.2 | Push real-time notifications | - Instant delivery<br>- No polling required |
| 2.3.3 | Connection management | - Handle reconnection<br>- Heartbeat/ping |
| 2.3.4 | Presence tracking | - Track online users<br>- Show online status |
| 2.3.5 | Typing indicators | - For chat features<br>- Real-time updates |

**WebSocket Endpoints:**
```
WS /api/notifications/stream

Messages:
- NOTIFICATION: New notification
- UNREAD_COUNT: Updated count
- PRESENCE: User online/offline
```

---

### Epic 2.4: Notification Center UI Support

**Priority:** Medium
**Dependency:** Epic 1.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Notification grouping | - Group by type or date<br>- Collapsible groups |
| 2.4.2 | Rich notifications | - Images, buttons<br>- Action support |
| 2.4.3 | Notification actions | - Mark as read<br>- Delete, snooze |
| 2.4.4 | Search notifications | - Search by keyword<br>- Filter by type |

**API Endpoints:**
```
GET /api/notifications/grouped
GET /api/notifications/search?q={query}
POST /api/notifications/{id}/snooze
```

---

### Epic 2.5: Campaign Management

**Priority:** Low
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Create campaign | - Target audience selection<br>- Multi-channel |
| 2.5.2 | Schedule campaign | - Send at optimal time<br>- Time zone handling |
| 2.5.3 | Campaign analytics | - Open rates, click rates<br>- Conversion tracking |
| 2.5.4 | A/B testing | - Test subject lines<br>- Test content variations |

**API Endpoints:**
```
POST /api/admin/campaigns
GET  /api/admin/campaigns
GET  /api/admin/campaigns/{id}
PUT  /api/admin/campaigns/{id}
GET  /api/admin/campaigns/{id}/analytics
```

---

### Epic 2.6: Notification Analytics

**Priority:** Low
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.6.1 | Delivery metrics | - Sent, delivered, failed counts |
| 2.6.2 | Engagement metrics | - Open rates, click rates |
| 2.6.3 | Channel comparison | - Performance by channel |
| 2.6.4 | User engagement | - Most/least engaged users |

**API Endpoints:**
```
GET /api/admin/notifications/analytics
GET /api/admin/notifications/analytics/channels
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
| User Service | Inbound | User data, email, phone |
| Order Service | Inbound | Order events |
| Payment Service | Inbound | Payment events |
| Shipping Service | Inbound | Shipping events |
| Inventory Service | Inbound | Low stock events |
| Review Service | Inbound | Review events |

---

## Events Consumed

| Event | Source | Action |
|-------|--------|--------|
| `user.registered` | User | Welcome email |
| `order.created` | Order | Order confirmation |
| `order.shipped` | Shipping | Shipping notification |
| `order.delivered` | Shipping | Delivery notification |
| `payment.succeeded` | Payment | Receipt email |
| `payment.failed` | Payment | Failure notice |
| `inventory.low` | Inventory | Low stock alert |
| `review.created` | Review | New review notification |
