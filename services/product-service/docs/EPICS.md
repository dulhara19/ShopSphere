# Product Service - Epic Breakdown

## Overview

This document outlines the epic breakdown for the Product Service, divided into two phases:
- **Phase 1 (MVP)**: Core product catalog functionality
- **Phase 2**: Advanced search, variations, and AI features

**Owner:** Team Member 2
**Port:** 3002
**Tech Stack:** Spring Boot, Spring Data MongoDB, Elasticsearch, Redis

---

## Phase 1 - MVP (Core Features)

> **Goal:** Deliver essential product catalog and basic search capabilities.

### Epic 1.1: Product CRUD Operations

**Priority:** Critical
**Dependency:** User Service (seller authentication)

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.1.1 | Create product (Seller) | - Validate seller role<br>- Required fields: name, description, price, categoryId<br>- Generate unique SKU<br>- Return created product |
| 1.1.2 | Get product by ID | - Return full product details<br>- Include category info<br>- Handle not found (404) |
| 1.1.3 | Update product (Seller/Admin) | - Only owner or admin can update<br>- Partial updates supported<br>- Return updated product |
| 1.1.4 | Delete product (Seller/Admin) | - Soft delete (mark as inactive)<br>- Only owner or admin can delete |
| 1.1.5 | List seller's products | - Paginated results<br>- Filter by status (active/inactive) |

**API Endpoints:**
```
POST   /api/products
GET    /api/products/{id}
PUT    /api/products/{id}
DELETE /api/products/{id}
GET    /api/products/seller/me
```

**Product Entity Fields (MVP):**
```
- id (UUID)
- sellerId
- name
- description
- price
- compareAtPrice (original price for discounts)
- categoryId
- subcategoryId
- images[] (URLs)
- status (ACTIVE, INACTIVE, OUT_OF_STOCK)
- createdAt
- updatedAt
```

---

### Epic 1.2: Category Management

**Priority:** Critical
**Dependency:** None

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.2.1 | Create category (Admin) | - Unique name validation<br>- Optional parent category (for subcategories)<br>- Support image/icon |
| 1.2.2 | List all categories | - Return hierarchical structure<br>- Include product count per category |
| 1.2.3 | Get category by ID | - Include subcategories<br>- Include parent category info |
| 1.2.4 | Update category (Admin) | - Update name, image, parent |
| 1.2.5 | Delete category (Admin) | - Prevent if products exist<br>- Or reassign products to parent |

**API Endpoints:**
```
POST   /api/categories           (Admin)
GET    /api/categories
GET    /api/categories/{id}
PUT    /api/categories/{id}      (Admin)
DELETE /api/categories/{id}      (Admin)
GET    /api/categories/{id}/subcategories
```

---

### Epic 1.3: Basic Product Search & Listing

**Priority:** High
**Dependency:** Epic 1.1, 1.2

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.3.1 | List products with pagination | - Default 20 items per page<br>- Sort by: createdAt, price, name |
| 1.3.2 | Filter by category | - Filter by categoryId<br>- Include subcategory products |
| 1.3.3 | Filter by price range | - minPrice, maxPrice parameters |
| 1.3.4 | Basic text search | - Search in name and description<br>- Case-insensitive |
| 1.3.5 | Sort products | - Sort by price (asc/desc)<br>- Sort by newest<br>- Sort by name |

**API Endpoints:**
```
GET /api/products?page=0&size=20&sort=price,asc
GET /api/products?category={categoryId}
GET /api/products?minPrice=10&maxPrice=100
GET /api/products?search={keyword}
```

---

### Epic 1.4: Product Images Management

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.4.1 | Upload product images | - Accept multiple images<br>- Max 10 images per product<br>- Validate file type (jpg, png, webp) |
| 1.4.2 | Set primary image | - Mark one image as primary<br>- Used for thumbnails |
| 1.4.3 | Delete product image | - Remove from storage<br>- Update product record |
| 1.4.4 | Image optimization | - Generate thumbnails<br>- Compress for web |

**API Endpoints:**
```
POST   /api/products/{id}/images
PUT    /api/products/{id}/images/{imageId}/primary
DELETE /api/products/{id}/images/{imageId}
```

---

### Epic 1.5: Internal Service Communication

**Priority:** High
**Dependency:** Epic 1.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 1.5.1 | Get product by ID (internal) | - For Order, Cart services<br>- Return price, name, stock status |
| 1.5.2 | Batch get products | - Accept list of product IDs<br>- Return list of product summaries |
| 1.5.3 | Validate products exist | - Check if products are active<br>- Return validation result |

**Internal API Endpoints:**
```
GET  /internal/products/{id}
POST /internal/products/batch
POST /internal/products/validate
```

---

## Phase 2 - Enhanced Features

> **Goal:** Add advanced search, product variations, and AI-powered features.

### Epic 2.1: Elasticsearch Integration

**Priority:** High
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.1.1 | Index products in Elasticsearch | - Sync products to ES on create/update<br>- Handle bulk indexing |
| 2.1.2 | Full-text search | - Search across all text fields<br>- Relevance scoring |
| 2.1.3 | Autocomplete/suggestions | - Suggest products as user types<br>- Based on product names |
| 2.1.4 | Faceted search | - Return filter counts<br>- Category, price range, brand facets |
| 2.1.5 | Search analytics | - Track search queries<br>- Track zero-result searches |

**API Endpoints:**
```
GET /api/products/search?q={query}
GET /api/products/suggest?q={partial}
GET /api/products/search/facets
```

---

### Epic 2.2: Product Variations

**Priority:** Medium
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.2.1 | Define variation attributes | - Size, Color, Material, etc.<br>- Admin can create new attributes |
| 2.2.2 | Create product variants | - Each variant has own SKU, price, images<br>- Link to parent product |
| 2.2.3 | Variant inventory tracking | - Each variant has own stock<br>- Integrate with Inventory Service |
| 2.2.4 | Variant selection UI support | - Return available combinations<br>- Handle out-of-stock variants |

**API Endpoints:**
```
POST   /api/products/{id}/variants
GET    /api/products/{id}/variants
PUT    /api/products/{id}/variants/{variantId}
DELETE /api/products/{id}/variants/{variantId}
GET    /api/attributes
POST   /api/attributes         (Admin)
```

---

### Epic 2.3: Advanced Filtering

**Priority:** Medium
**Dependency:** Epic 2.1

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.3.1 | Filter by brand | - Brand as product attribute<br>- Multi-select support |
| 2.3.2 | Filter by rating | - Integrate with Review Service<br>- Filter by min rating |
| 2.3.3 | Filter by attributes | - Dynamic filters based on category<br>- Size, color, material, etc. |
| 2.3.4 | Filter by availability | - In stock only<br>- Include out of stock |
| 2.3.5 | Combined filters | - Apply multiple filters<br>- AND logic between filter types |

**API Endpoints:**
```
GET /api/products?brand={brand}&rating=4&color=red&inStock=true
```

---

### Epic 2.4: AI Visual Search

**Priority:** Low
**Dependency:** Phase 1 complete

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.4.1 | Image upload for search | - Accept image file<br>- Extract visual features |
| 2.4.2 | Find similar products | - Match against product images<br>- Return ranked results |
| 2.4.3 | Visual search optimization | - Cache feature vectors<br>- Handle large catalogs |

**API Endpoints:**
```
POST /api/products/visual-search
```

---

### Epic 2.5: Product Reviews Integration

**Priority:** Medium
**Dependency:** Review Service

| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| 2.5.1 | Display average rating | - Aggregate from Review Service<br>- Cache rating data |
| 2.5.2 | Review count on product | - Show total review count<br>- Update on new reviews |
| 2.5.3 | Rating breakdown | - Show rating distribution (5-star, 4-star, etc.) |

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
| User Service | Inbound | Validate seller authentication |
| Inventory Service | Outbound | Check stock availability |
| Review Service | Inbound | Get ratings and reviews |
| Recommendation Service | Outbound | Provide product data |

---

## Events Published

| Event | Trigger | Consumers |
|-------|---------|-----------|
| `product.created` | New product added | Inventory, Recommendation, Analytics |
| `product.updated` | Product details changed | Recommendation, Analytics |
| `product.deleted` | Product removed | Inventory, Order, Analytics |
| `product.price.changed` | Price update | Order (cart), Analytics |
