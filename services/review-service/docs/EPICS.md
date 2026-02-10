# Review Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Review Service, divided into two phases:
- **Phase 1 (MVP)**: Core reviews and ratings functionality
- **Phase 2**: Social features, UGC, and influencer content

**Owner:** Team Member 7
**Port:** 3007
**Tech Stack:** Spring Boot, Spring Data MongoDB, Redis, AWS S3

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential product reviews and ratings to help customers make purchase decisions.

### Epic 1.1: Product Reviews

**Priority:** Critical
**Dependency:** User Service, Product Service, Order Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Create product review | - Rating (1-5 stars) required<br>- Title and body text<br>- Only verified purchasers can review |
| 1.1.2 | Get reviews for product | - Paginated results<br>- Sort by date, rating, helpfulness<br>- Include reviewer info |
| 1.1.3 | Update own review | - Edit within 30 days<br>- Track edit history |
| 1.1.4 | Delete own review | - Soft delete<br>- Admin can hard delete |
| 1.1.5 | Verified purchase badge | - Check order history<br>- Display badge on review |

**API Endpoints:**
```
POST   /api/reviews
       Body: {
         "productId": "xxx",
         "rating": 5,
         "title": "Great product!",
         "body": "Exceeded my expectations..."
       }

GET    /api/reviews/product/{productId}?page=0&sort=newest
GET    /api/reviews/{reviewId}
PUT    /api/reviews/{reviewId}
DELETE /api/reviews/{reviewId}
GET    /api/reviews/user/me
```

**Review Entity Fields:**
```
- id (UUID)
- productId
- userId
- rating (1-5)
- title
- body
- isVerifiedPurchase
- helpfulCount
- status (PENDING, APPROVED, REJECTED, FLAGGED)
- images[]
- createdAt
- updatedAt
- editHistory[]
```

---

### Epic 1.2: Rating Aggregation

**Priority:** Critical
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Calculate average rating | - Weighted average<br>- Update on new review<br>- Cache in Redis |
| 1.2.2 | Rating distribution | - Count per star level<br>- For display (5-star: 45%, etc.) |
| 1.2.3 | Get product rating summary | - Average, count, distribution<br>- Single API call |
| 1.2.4 | Recalculate ratings job | - Scheduled recalculation<br>- Handle deleted reviews |

**API Endpoints:**
```
GET /api/reviews/product/{productId}/summary
    Response: {
      "averageRating": 4.5,
      "totalReviews": 128,
      "distribution": {
        "5": 65,
        "4": 40,
        "3": 15,
        "2": 5,
        "1": 3
      }
    }
```

---

### Epic 1.3: Review Helpfulness

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | Mark review as helpful | - One vote per user per review<br>- Toggle on/off |
| 1.3.2 | Sort by helpfulness | - Most helpful first<br>- Factor in recency |
| 1.3.3 | Helpful count display | - "X people found this helpful" |
| 1.3.4 | Featured reviews | - Auto-select top helpful reviews<br>- Display prominently |

**API Endpoints:**
```
POST /api/reviews/{reviewId}/helpful
DELETE /api/reviews/{reviewId}/helpful
GET  /api/reviews/product/{productId}/featured
```

---

### Epic 1.4: Review Moderation

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Report review | - Reason selection<br>- Additional comments<br>- Track reporter |
| 1.4.2 | Admin review queue | - List pending/reported reviews<br>- Filter by status |
| 1.4.3 | Approve/Reject review | - Update status<br>- Notify user on rejection |
| 1.4.4 | Auto-moderation rules | - Profanity filter<br>- Suspicious pattern detection |
| 1.4.5 | Review guidelines | - Display guidelines to users<br>- Link from review form |

**API Endpoints:**
```
POST /api/reviews/{reviewId}/report
     Body: { "reason": "SPAM", "comments": "..." }

GET  /api/admin/reviews/queue
PUT  /api/admin/reviews/{reviewId}/approve
PUT  /api/admin/reviews/{reviewId}/reject
     Body: { "reason": "Violates guidelines" }
```

---

### Epic 1.5: Comments System

**Priority:** Medium
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Add comment to review | - Text comment<br>- Link to parent review |
| 1.5.2 | Reply to comment | - Nested replies (1 level)<br>- Notify original commenter |
| 1.5.3 | Edit/Delete comment | - Within time limit<br>- Soft delete |
| 1.5.4 | Seller response | - Official seller badge<br>- Highlighted display |

**API Endpoints:**
```
POST   /api/reviews/{reviewId}/comments
GET    /api/reviews/{reviewId}/comments
PUT    /api/comments/{commentId}
DELETE /api/comments/{commentId}
POST   /api/reviews/{reviewId}/seller-response  (Seller only)
```

---

### Epic 1.6: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.1, 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.6.1 | Get rating for product | - For Product Service display<br>- Cached response |
| 1.6.2 | Batch get ratings | - Multiple products at once<br>- For product listings |
| 1.6.3 | Review events | - Publish review.created event<br>- Consumed by Product, Analytics |

**Internal API Endpoints:**
```
GET  /internal/reviews/product/{productId}/rating
POST /internal/reviews/ratings/batch
     Body: { "productIds": ["xxx", "yyy"] }
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add social features, user-generated content, and influencer capabilities.

### Epic 2.1: Review Images & Videos

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Upload images with review | - Max 5 images per review<br>- Image validation (size, type) |
| 2.1.2 | Upload video with review | - Max 1 video per review<br>- Max 60 seconds |
| 2.1.3 | Image gallery view | - View all images for product<br>- Lightbox display |
| 2.1.4 | Content moderation | - Auto-scan for inappropriate content<br>- Manual review queue |
| 2.1.5 | CDN integration | - Serve via CloudFront/CDN<br>- Image optimization |

**API Endpoints:**
```
POST /api/reviews/{reviewId}/images
GET  /api/reviews/{reviewId}/images
DELETE /api/reviews/{reviewId}/images/{imageId}
POST /api/reviews/{reviewId}/video
GET  /api/reviews/product/{productId}/gallery
```

---

### Epic 2.2: Social Feed

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Create social post | - Text, images, product tags<br>- Style inspiration posts |
| 2.2.2 | User feed | - Posts from followed users<br>- Personalized content |
| 2.2.3 | Explore/discover feed | - Trending posts<br>- Algorithm-based |
| 2.2.4 | Like posts | - Toggle like<br>- Like count |
| 2.2.5 | Share posts | - Share to external platforms<br>- Copy link |

**API Endpoints:**
```
POST /api/social/posts
GET  /api/social/feed
GET  /api/social/explore
POST /api/social/posts/{postId}/like
DELETE /api/social/posts/{postId}/like
GET  /api/social/posts/{postId}/share-link
```

---

### Epic 2.3: Follow System

**Priority:** Medium
**Dependency:** Epic 2.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Follow user | - Add to following list<br>- Notify followed user |
| 2.3.2 | Unfollow user | - Remove from list |
| 2.3.3 | Followers/Following lists | - Paginated results<br>- Include user summary |
| 2.3.4 | Follow suggestions | - Based on interests<br>- Popular users |
| 2.3.5 | Block user | - Hide content from blocked user<br>- Prevent follows |

**API Endpoints:**
```
POST   /api/users/{userId}/follow
DELETE /api/users/{userId}/follow
GET    /api/users/{userId}/followers
GET    /api/users/{userId}/following
GET    /api/users/suggestions
POST   /api/users/{userId}/block
```

---

### Epic 2.4: Influencer Features

**Priority:** Low
**Dependency:** Epic 2.2, 2.3

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Influencer verification | - Apply for verification<br>- Admin approval process |
| 2.4.2 | Influencer profile | - Verified badge<br>- Custom bio, links |
| 2.4.3 | Curated collections | - Create product collections<br>- Public/private |
| 2.4.4 | Affiliate links | - Track referrals<br>- Commission tracking |
| 2.4.5 | Influencer analytics | - View engagement stats<br>- Top performing content |

**API Endpoints:**
```
POST /api/influencer/apply
GET  /api/admin/influencer/applications
PUT  /api/admin/influencer/{userId}/verify
POST /api/collections
GET  /api/users/{userId}/collections
GET  /api/influencer/analytics
```

---

### Epic 2.5: Stories Feature

**Priority:** Low
**Dependency:** Epic 2.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Create story | - Image/video + optional text<br>- 24-hour expiry |
| 2.5.2 | View stories | - Carousel view<br>- Track views |
| 2.5.3 | Story reactions | - Quick emoji reactions |
| 2.5.4 | Story highlights | - Save stories permanently<br>- Organize in collections |

**API Endpoints:**
```
POST /api/stories
GET  /api/stories/feed
GET  /api/users/{userId}/stories
POST /api/stories/{storyId}/reaction
POST /api/stories/highlights
```

---

### Epic 2.6: Advanced Spam Detection

**Priority:** Medium
**Dependency:** Epic 1.4

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.6.1 | ML-based spam detection | - Train on flagged reviews<br>- Auto-flag suspicious |
| 2.6.2 | Fake review detection | - Identify patterns<br>- Velocity analysis |
| 2.6.3 | Review bombing prevention | - Detect coordinated attacks<br>- Rate limiting |
| 2.6.4 | Sentiment analysis | - Analyze review sentiment<br>- Flag inconsistent rating/text |

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
| User Service | Inbound | User authentication, profiles |
| Product Service | Inbound/Outbound | Product info, rating updates |
| Order Service | Inbound | Verify purchase for reviews |
| Notification Service | Outbound | Review notifications |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `review.created` | New review submitted | Product, Notification, Analytics |
| `review.updated` | Review edited | Product |
| `review.deleted` | Review removed | Product |
| `review.approved` | Review passes moderation | Product, Notification |
| `post.created` | New social post | Analytics, Recommendation |
| `user.followed` | User followed | Notification |
