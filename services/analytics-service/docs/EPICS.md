# Analytics Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Analytics Service, divided into two phases:
- **Phase 1 (MVP)**: Core analytics and basic reporting
- **Phase 2**: Real-time dashboards, forecasting, and advanced analytics

**Owner:** Team Member 10
**Port:** 3010
**Tech Stack:** Spring Boot, Spring Data JPA, PostgreSQL/ClickHouse, Redis

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential business intelligence and reporting capabilities.

### Epic 1.1: Event Ingestion System

**Priority:** Critical
**Dependency:** All Services

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Event consumer setup | - Listen to events from all services<br>- RabbitMQ/Kafka integration |
| 1.1.2 | Event storage | - Store events in time-series format<br>- Efficient querying |
| 1.1.3 | Event schema registry | - Define event schemas<br>- Version management |
| 1.1.4 | Event validation | - Validate incoming events<br>- Handle malformed events |
| 1.1.5 | Batch event ingestion | - Accept bulk events<br>- For historical data import |

**API Endpoints:**
```
POST /api/analytics/events
     Body: {
       "eventType": "PAGE_VIEW",
       "timestamp": "2024-01-15T10:30:00Z",
       "userId": "xxx",
       "properties": { ... }
     }

POST /api/analytics/events/batch
```

**Event Categories:**
```
- User Events: registration, login, profile_update
- Product Events: view, search, click
- Order Events: created, confirmed, shipped, delivered, cancelled
- Payment Events: succeeded, failed, refunded
- Engagement Events: review, rating, share, follow
```

---

### Epic 1.2: Sales Analytics

**Priority:** Critical
**Dependency:** Order Service, Payment Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Total sales metrics | - Revenue, order count<br>- Daily, weekly, monthly |
| 1.2.2 | Sales by date range | - Custom date range<br>- Compare periods |
| 1.2.3 | Sales by category | - Revenue per category<br>- Top categories |
| 1.2.4 | Sales by product | - Top selling products<br>- Revenue per product |
| 1.2.5 | Average order value | - AOV calculation<br>- Trend over time |
| 1.2.6 | Conversion funnel | - View → Cart → Checkout → Purchase |

**API Endpoints:**
```
GET /api/analytics/sales/summary
    Query: ?from=2024-01-01&to=2024-01-31
    Response: {
      "totalRevenue": 125000,
      "totalOrders": 450,
      "averageOrderValue": 277.78,
      "comparisonPeriod": {
        "revenueChange": "+15%",
        "ordersChange": "+10%"
      }
    }

GET /api/analytics/sales/by-date?from={}&to={}&granularity=day
GET /api/analytics/sales/by-category
GET /api/analytics/sales/by-product?limit=10
GET /api/analytics/sales/funnel
```

---

### Epic 1.3: Product Analytics

**Priority:** High
**Dependency:** Product Service, Recommendation Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Product views | - View count per product<br>- Unique viewers |
| 1.3.2 | Product conversion rate | - Views to purchases<br>- Add-to-cart rate |
| 1.3.3 | Top products | - By views, sales, revenue<br>- Configurable period |
| 1.3.4 | Product performance | - Detailed metrics per product |
| 1.3.5 | Category performance | - Metrics by category |
| 1.3.6 | Search analytics | - Top search terms<br>- Zero result searches |

**API Endpoints:**
```
GET /api/analytics/products/top-viewed?limit=10&period=7d
GET /api/analytics/products/top-selling?limit=10&period=30d
GET /api/analytics/products/{productId}/performance
GET /api/analytics/categories/performance
GET /api/analytics/search/top-terms?limit=20
GET /api/analytics/search/no-results
```

---

### Epic 1.4: User Analytics

**Priority:** High
**Dependency:** User Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | User registrations | - New users over time<br>- Registration source |
| 1.4.2 | Active users | - DAU, WAU, MAU<br>- Activity definition |
| 1.4.3 | User retention | - Cohort analysis<br>- Retention rate |
| 1.4.4 | User segments | - By purchase behavior<br>- By activity level |
| 1.4.5 | User lifetime value | - CLV calculation<br>- Segment by value |
| 1.4.6 | Churn analysis | - Identify churned users<br>- Churn rate |

**API Endpoints:**
```
GET /api/analytics/users/registrations?from={}&to={}
GET /api/analytics/users/active?metric=dau|wau|mau
GET /api/analytics/users/retention?cohort=2024-01
GET /api/analytics/users/segments
GET /api/analytics/users/ltv-distribution
GET /api/analytics/users/churn?period=30d
```

---

### Epic 1.5: Basic Dashboard

**Priority:** High
**Dependency:** Epic 1.2, 1.3, 1.4

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Dashboard summary API | - Key metrics at a glance<br>- Single API call |
| 1.5.2 | Admin dashboard data | - Full platform metrics<br>- Admin only |
| 1.5.3 | Seller dashboard data | - Seller-specific metrics<br>- Their products only |
| 1.5.4 | Period comparison | - Compare to previous period<br>- % change indicators |
| 1.5.5 | Dashboard caching | - Cache dashboard data<br>- Configurable TTL |

**API Endpoints:**
```
GET /api/analytics/dashboard/admin
    Response: {
      "sales": { "today": 5000, "thisWeek": 35000, "thisMonth": 125000 },
      "orders": { "pending": 15, "processing": 8, "shipped": 22 },
      "users": { "new": 45, "active": 1200 },
      "products": { "topSelling": [...], "lowStock": 5 }
    }

GET /api/analytics/dashboard/seller
```

---

### Epic 1.6: Data Export

**Priority:** Medium
**Dependency:** Epic 1.2, 1.3, 1.4

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.6.1 | Export to CSV | - Sales, orders, products<br>- Custom date range |
| 1.6.2 | Export to Excel | - Formatted Excel file<br>- Multiple sheets |
| 1.6.3 | Scheduled exports | - Automated reports<br>- Email delivery |
| 1.6.4 | Export history | - Track past exports<br>- Re-download |

**API Endpoints:**
```
POST /api/analytics/export
     Body: {
       "type": "SALES",
       "format": "CSV",
       "dateRange": { "from": "...", "to": "..." },
       "filters": {}
     }
     Response: { "exportId": "xxx", "status": "PROCESSING" }

GET  /api/analytics/export/{exportId}
GET  /api/analytics/export/{exportId}/download
GET  /api/analytics/exports   (list past exports)
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add real-time capabilities, forecasting, and advanced analytics.

### Epic 2.1: Real-Time Dashboard

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Real-time sales ticker | - Live sales updates<br>- WebSocket stream |
| 2.1.2 | Active users counter | - Current online users<br>- Real-time updates |
| 2.1.3 | Live order feed | - Stream of new orders<br>- Filterable |
| 2.1.4 | Real-time alerts | - Threshold-based alerts<br>- Anomaly detection |
| 2.1.5 | Live map | - Orders by geography<br>- Real-time visualization |

**WebSocket Endpoints:**
```
WS /api/analytics/stream/sales
WS /api/analytics/stream/orders
WS /api/analytics/stream/alerts
```

---

### Epic 2.2: Revenue Forecasting

**Priority:** Medium
**Dependency:** Epic 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Historical trend analysis | - Identify patterns<br>- Seasonality detection |
| 2.2.2 | Revenue forecast | - Predict future revenue<br>- Confidence intervals |
| 2.2.3 | Sales predictions | - Predict sales volume<br>- By category/product |
| 2.2.4 | Forecast accuracy tracking | - Compare predictions to actual<br>- Improve models |

**API Endpoints:**
```
GET /api/analytics/forecast/revenue?period=30d
GET /api/analytics/forecast/sales?category={}&period=30d
GET /api/analytics/forecast/accuracy
```

---

### Epic 2.3: A/B Testing Analytics

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Create A/B test | - Define variants<br>- Set success metric |
| 2.3.2 | Track test results | - Conversions per variant<br>- Statistical significance |
| 2.3.3 | Test analysis | - Winner determination<br>- Confidence level |
| 2.3.4 | Test history | - Past tests and results |

**API Endpoints:**
```
POST /api/analytics/ab-tests
GET  /api/analytics/ab-tests
GET  /api/analytics/ab-tests/{testId}
GET  /api/analytics/ab-tests/{testId}/results
PUT  /api/analytics/ab-tests/{testId}/conclude
```

---

### Epic 2.4: Custom Reports

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Report builder | - Select metrics<br>- Choose dimensions<br>- Apply filters |
| 2.4.2 | Save custom reports | - Save report configuration<br>- Share with team |
| 2.4.3 | Scheduled reports | - Run on schedule<br>- Email delivery |
| 2.4.4 | Report templates | - Pre-built report templates |

**API Endpoints:**
```
POST /api/analytics/reports/custom
GET  /api/analytics/reports/saved
GET  /api/analytics/reports/{reportId}/run
POST /api/analytics/reports/{reportId}/schedule
```

---

### Epic 2.5: Social & Engagement Analytics

**Priority:** Low
**Dependency:** Review Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Review analytics | - Review volume, sentiment<br>- Rating distribution |
| 2.5.2 | Social engagement | - Posts, likes, shares<br>- Top content |
| 2.5.3 | Influencer performance | - Sales attributed<br>- Engagement metrics |
| 2.5.4 | Viral content tracking | - Track viral products/posts |

**API Endpoints:**
```
GET /api/analytics/reviews/summary
GET /api/analytics/social/engagement
GET /api/analytics/influencers/performance
```

---

### Epic 2.6: Seller Analytics

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.6.1 | Seller performance metrics | - Sales, revenue, ratings<br>- Per seller |
| 2.6.2 | Seller comparison | - Rank sellers<br>- Performance tiers |
| 2.6.3 | Seller dashboard | - Dedicated seller metrics<br>- Recommendations |
| 2.6.4 | Commission reports | - Calculate commissions<br>- Payment reconciliation |

**API Endpoints:**
```
GET /api/analytics/sellers/performance
GET /api/analytics/sellers/{sellerId}/metrics
GET /api/analytics/sellers/ranking
GET /api/analytics/sellers/{sellerId}/commission
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
- [ ] Query performance optimized (<500ms)
- [ ] Deployed to dev environment

---

## Dependencies on Other Services

| Service | Dependency Type | Description |
|---------|----------------|-------------|
| User Service | Inbound | User events, profiles |
| Product Service | Inbound | Product events, data |
| Order Service | Inbound | Order events, data |
| Payment Service | Inbound | Payment events, transactions |
| Inventory Service | Inbound | Stock events |
| Review Service | Inbound | Review events, ratings |
| Recommendation Service | Inbound | Recommendation events |

---

## Events Consumed

| Event | Source | Use |
|-------|--------|-----|
| `user.registered` | User | Registration metrics |
| `user.login` | User | Active user tracking |
| `product.viewed` | Recommendation | Product analytics |
| `order.created` | Order | Sales metrics |
| `order.completed` | Order | Revenue calculation |
| `payment.succeeded` | Payment | Transaction metrics |
| `review.created` | Review | Engagement metrics |

---

## Data Retention Policy

| Data Type | Retention Period |
|-----------|------------------|
| Raw events | 90 days |
| Aggregated daily | 2 years |
| Aggregated monthly | 5 years |
| User profiles | As long as active |
