# Recommendation Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Recommendation Service, divided into two phases:
- **Phase 1 (MVP)**: Basic recommendations and trending products
- **Phase 2**: AI/ML-powered personalization and visual search

**Owner:** Team Member 8
**Port:** 3008
**Tech Stack:** Spring Boot, Spring AI, DJL (Deep Java Library), MongoDB, Redis

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver basic product recommendations to improve product discovery and sales.

### Epic 1.1: User Behavior Tracking

**Priority:** Critical
**Dependency:** User Service, Product Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Track product views | - Log productId, userId, timestamp<br>- Anonymous tracking with sessionId |
| 1.1.2 | Track search queries | - Log search terms<br>- Track result clicks |
| 1.1.3 | Track cart additions | - Log add-to-cart events<br>- Track quantity |
| 1.1.4 | Track purchases | - Log completed orders<br>- Link to products purchased |
| 1.1.5 | Event ingestion API | - Real-time event collection<br>- Batch event support |

**API Endpoints:**
```
POST /api/events/track
     Body: {
       "eventType": "PRODUCT_VIEW",
       "productId": "xxx",
       "userId": "xxx",        // optional
       "sessionId": "xxx",
       "metadata": {}
     }

POST /api/events/batch
     Body: [{ event1 }, { event2 }]
```

**Event Types:**
```
- PRODUCT_VIEW
- PRODUCT_CLICK
- SEARCH_QUERY
- SEARCH_CLICK
- ADD_TO_CART
- REMOVE_FROM_CART
- PURCHASE
- WISHLIST_ADD
- REVIEW_VIEW
```

---

### Epic 1.2: Recently Viewed Products

**Priority:** Critical
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Store recently viewed | - Per user (logged in)<br>- Per session (anonymous)<br>- Max 50 items |
| 1.2.2 | Get recently viewed | - Return last N products<br>- Exclude current product |
| 1.2.3 | Clear history | - User can clear history<br>- Auto-expire after 30 days |
| 1.2.4 | Merge on login | - Combine session history with user history |

**API Endpoints:**
```
GET    /api/recommendations/recently-viewed?limit=10
DELETE /api/recommendations/recently-viewed
```

---

### Epic 1.3: Trending Products

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Calculate trending score | - Based on views, sales, velocity<br>- Time-weighted (recent = higher) |
| 1.3.2 | Global trending | - Top products across platform |
| 1.3.3 | Category trending | - Top products per category |
| 1.3.4 | Update trending job | - Scheduled recalculation<br>- Every hour |
| 1.3.5 | Trending cache | - Cache in Redis<br>- Refresh on schedule |

**API Endpoints:**
```
GET /api/recommendations/trending?limit=20
GET /api/recommendations/trending/category/{categoryId}
```

**Trending Score Formula:**
```
score = (views * 1) + (cart_adds * 3) + (purchases * 5)
        × time_decay_factor
```

---

### Epic 1.4: Similar Products

**Priority:** High
**Dependency:** Product Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Same category products | - Products in same category<br>- Similar price range |
| 1.4.2 | Attribute matching | - Match by brand, color, etc.<br>- Configurable attributes |
| 1.4.3 | Get similar products | - For product detail page<br>- Exclude out of stock |
| 1.4.4 | Similarity scoring | - Rank by similarity score |

**API Endpoints:**
```
GET /api/recommendations/similar/{productId}?limit=8
```

---

### Epic 1.5: "Customers Also Bought"

**Priority:** High
**Dependency:** Epic 1.1, Order Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Build co-purchase matrix | - Products bought together<br>- From order history |
| 1.5.2 | Frequently bought together | - Top co-purchased products<br>- For product page |
| 1.5.3 | Bundle suggestions | - Suggest product bundles<br>- Based on purchase patterns |
| 1.5.4 | Update matrix job | - Daily recalculation<br>- Incremental updates |

**API Endpoints:**
```
GET /api/recommendations/also-bought/{productId}?limit=5
GET /api/recommendations/bundle/{productId}
```

---

### Epic 1.6: Basic Personalization

**Priority:** Medium
**Dependency:** Epic 1.1, 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.6.1 | Category affinity | - Track preferred categories<br>- Based on views/purchases |
| 1.6.2 | Price range preference | - Track typical price range |
| 1.6.3 | Homepage recommendations | - Personalized for logged-in users<br>- Popular for anonymous |
| 1.6.4 | "For You" section | - Based on browsing history<br>- Mix of categories |

**API Endpoints:**
```
GET /api/recommendations/for-you?limit=20
GET /api/recommendations/homepage
```

---

## Phase 2 - Enhanced Features

> **Goal:** Implement AI/ML-powered recommendations and advanced personalization.

### Epic 2.1: Collaborative Filtering

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | User-based collaborative filtering | - Find similar users<br>- Recommend what they liked |
| 2.1.2 | Item-based collaborative filtering | - Find similar items<br>- Based on user interactions |
| 2.1.3 | Hybrid approach | - Combine user and item based<br>- Weighted scoring |
| 2.1.4 | Model training pipeline | - Train models on historical data<br>- Scheduled retraining |
| 2.1.5 | A/B testing framework | - Test algorithm variations |

**API Endpoints:**
```
GET /api/recommendations/cf/users-like-you
GET /api/recommendations/cf/because-you-bought/{productId}
```

---

### Epic 2.2: Content-Based Recommendations

**Priority:** Medium
**Dependency:** Product Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Product embeddings | - Generate vector representations<br>- Based on attributes, description |
| 2.2.2 | Text similarity | - Similar product descriptions<br>- NLP processing |
| 2.2.3 | Attribute-based matching | - Deep attribute comparison<br>- Weighted attributes |
| 2.2.4 | Hybrid content + collaborative | - Combine approaches<br>- Cold start handling |

---

### Epic 2.3: AI Visual Search

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Image feature extraction | - Use pre-trained CNN<br>- Generate image embeddings |
| 2.3.2 | Search by image upload | - Accept image file<br>- Find visually similar products |
| 2.3.3 | Search by image URL | - Accept image URL |
| 2.3.4 | Visual similarity index | - Index all product images<br>- Fast similarity search |
| 2.3.5 | Crop and search | - Select region of image<br>- Search based on selection |

**API Endpoints:**
```
POST /api/recommendations/visual-search
     Body: multipart/form-data with image

POST /api/recommendations/visual-search/url
     Body: { "imageUrl": "https://..." }
```

---

### Epic 2.4: Real-Time Personalization

**Priority:** Medium
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Session-based recommendations | - Adapt to current session behavior |
| 2.4.2 | Context-aware recommendations | - Time of day, device, location |
| 2.4.3 | Real-time model serving | - Low latency predictions<br>- Model caching |
| 2.4.4 | Recommendation explanation | - "Because you viewed X"<br>- Transparency |

**API Endpoints:**
```
GET /api/recommendations/real-time?context={...}
GET /api/recommendations/{id}/explanation
```

---

### Epic 2.5: Search Recommendations

**Priority:** Medium
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Search autocomplete | - Suggest search terms<br>- Based on popular searches |
| 2.5.2 | "Did you mean" suggestions | - Spell correction<br>- Alternative queries |
| 2.5.3 | Search personalization | - Personalize search results<br>- Boost preferred categories |
| 2.5.4 | Zero results recommendations | - Suggest alternatives<br>- Similar searches |

**API Endpoints:**
```
GET /api/recommendations/search/autocomplete?q={partial}
GET /api/recommendations/search/alternatives?q={query}
```

---

### Epic 2.6: Recommendation Analytics

**Priority:** Low
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.6.1 | Click-through rate tracking | - Track recommendation clicks<br>- Calculate CTR |
| 2.6.2 | Conversion tracking | - Recommendations → purchases<br>- Attribution |
| 2.6.3 | A/B test results | - Compare algorithm performance |
| 2.6.4 | Dashboard | - Recommendation performance metrics<br>- Visualizations |

**API Endpoints:**
```
GET /api/admin/recommendations/analytics
GET /api/admin/recommendations/ab-tests
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
- [ ] Performance benchmarks met (<100ms response)
- [ ] Deployed to dev environment

---

## Dependencies on Other Services

| Service | Dependency Type | Description |
|---------|----------------|-------------|
| Product Service | Inbound | Product data, attributes |
| User Service | Inbound | User profiles, preferences |
| Order Service | Inbound | Purchase history |
| Analytics Service | Outbound | Recommendation metrics |

---

## Events Consumed

| Event | Source | Action |
|-------|--------|--------|
| `product.created` | Product | Index new product |
| `product.updated` | Product | Update product data |
| `order.completed` | Order | Update purchase data |
| `review.created` | Review | Update product scores |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `recommendation.clicked` | User clicks recommendation | Analytics |
| `recommendation.converted` | Recommendation → purchase | Analytics |
