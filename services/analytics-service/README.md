# Analytics Service - Implementation Complete

## Overview

This is a fully functional Analytics Service for the ShopSphere platform implementing all Phase 1 (MVP) features:
- Event Ingestion System
- Sales Analytics
- Product Analytics
- User Analytics
- Basic Dashboard
- Data Export

**Server Port:** 3010  
**Tech Stack:** Spring Boot 3.2, Spring Data JPA, PostgreSQL, Redis, RabbitMQ

---

## 📋 API Endpoints

### Event Ingestion (`/api/analytics/events`)

#### Track Single Event
```bash
POST /api/analytics/events
Content-Type: application/json

{
  "eventType": "PAGE_VIEW",
  "timestamp": "2024-01-15T10:30:00Z",
  "userId": "user123",
  "productId": "prod456",
  "properties": {
    "pageUrl": "/products/1",
    "referrer": "search"
  }
}

Response: 201 Created
{
  "success": true,
  "message": "Event tracked successfully",
  "data": { ... event object ... }
}
```

#### Track Batch Events
```bash
POST /api/analytics/events/batch
Content-Type: application/json

{
  "source": "mobile-app",
  "events": [
    {
      "eventType": "PRODUCT_VIEW",
      "timestamp": "2024-01-15T10:30:00Z",
      "userId": "user123",
      "productId": "prod456"
    },
    {
      "eventType": "ADD_TO_CART",
      "timestamp": "2024-01-15T10:31:00Z",
      "userId": "user123",
      "productId": "prod456"
    }
  ]
}
```

#### Get Event Count by Type
```bash
GET /api/analytics/events/type/PAGE_VIEW?from=2024-01-01T00:00:00&to=2024-01-31T23:59:59

Response: 200 OK
{
  "success": true,
  "data": 12450
}
```

---

### Sales Analytics (`/api/analytics/sales`)

#### Get Sales Summary
```bash
GET /api/analytics/sales/summary?from=2024-01-01&to=2024-01-31

Response: 200 OK
{
  "success": true,
  "data": {
    "totalRevenue": 125000.00,
    "totalOrders": 450,
    "averageOrderValue": 277.78,
    "totalItems": 890,
    "comparisonPeriod": {
      "revenueChange": "+15%",
      "ordersChange": "+10%",
      "previousPeriodRevenue": 108695.65,
      "previousPeriodOrders": 409
    }
  }
}
```

#### Get Sales by Date Range
```bash
GET /api/analytics/sales/by-date?from=2024-01-01&to=2024-01-31&granularity=day

Response: 200 OK
{
  "success": true,
  "data": [
    {
      "metricDate": "2024-01-01",
      "granularity": "day",
      "totalRevenue": 4000.00,
      "totalOrders": 15,
      ...
    }
  ]
}
```

#### Get Sales by Category
```bash
GET /api/analytics/sales/by-category?from=2024-01-01&to=2024-01-31
```

#### Get Top Products
```bash
GET /api/analytics/sales/by-product?from=2024-01-01&to=2024-01-31&limit=10
```

#### Get Top Categories
```bash
GET /api/analytics/sales/top-categories?from=2024-01-01&to=2024-01-31&limit=10
```

#### Get Conversion Funnel
```bash
GET /api/analytics/sales/funnel?from=2024-01-01&to=2024-01-31
```

---

### Product Analytics (`/api/analytics/products`)

#### Get Product Performance
```bash
GET /api/analytics/products/{productId}/performance?from=2024-01-01&to=2024-01-31

Response: 200 OK
{
  "success": true,
  "data": {
    "productId": "prod456",
    "viewCount": 1250,
    "uniqueViewers": 890,
    "addToCartCount": 180,
    "purchaseCount": 45,
    "revenue": 9000.00,
    "unitsSold": 45,
    "conversionRate": 3.6,
    "avgRating": 4.5,
    "reviewCount": 32
  }
}
```

#### Get Top Viewed Products
```bash
GET /api/analytics/products/top-viewed?from=2024-01-01&to=2024-01-31&limit=10
```

#### Get Top Selling Products
```bash
GET /api/analytics/products/top-selling?from=2024-01-01&to=2024-01-31&limit=10
```

#### Get Top Revenue Products
```bash
GET /api/analytics/products/top-revenue?from=2024-01-01&to=2024-01-31&limit=10
```

#### Get Category Performance
```bash
GET /api/analytics/products/categories/{categoryId}/performance?from=2024-01-01&to=2024-01-31
```

---

### User Analytics (`/api/analytics/users`)

#### Get New User Registrations
```bash
GET /api/analytics/users/registrations?from=2024-01-01&to=2024-01-31

Response: 200 OK
{
  "success": true,
  "data": 245
}
```

#### Get Active Users
```bash
GET /api/analytics/users/active?metric=dau&date=2024-01-15
# Metrics: dau (Daily), wau (Weekly), mau (Monthly)
```

#### Get User Retention
```bash
GET /api/analytics/users/retention?cohort=2024-01
```

#### Get User Segments
```bash
GET /api/analytics/users/segments?date=2024-01-15
```

#### Get Lifetime Value Distribution
```bash
GET /api/analytics/users/ltv-distribution?limit=10&date=2024-01-15
```

#### Get Churn Rate
```bash
GET /api/analytics/users/churn?period=30
```

---

### Dashboard (`/api/analytics/dashboard`)

#### Get Admin Dashboard
```bash
GET /api/analytics/dashboard/admin

Response: 200 OK
{
  "success": true,
  "data": {
    "sales": {
      "today": 5000.00,
      "thisWeek": 35000.00,
      "thisMonth": 125000.00,
      "change": "+15%"
    },
    "orders": {
      "pending": 15,
      "processing": 8,
      "shipped": 22,
      "delivered": 450,
      "cancelled": 5
    },
    "users": {
      "newUsers": 45,
      "activeUsers": 1200,
      "totalUsers": 5000
    },
    "products": {
      "topSelling": [
        {
          "productId": "prod456",
          "productName": "Premium Widget",
          "unitsSold": 250,
          "revenue": 7500.00
        }
      ],
      "lowStockCount": 12,
      "outOfStockCount": 3
    }
  }
}
```

#### Get Seller Dashboard
```bash
GET /api/analytics/dashboard/seller?sellerId=seller123
```

#### Invalidate Cache
```bash
POST /api/analytics/dashboard/invalidate-cache

Response: 200 OK
{
  "success": true,
  "data": "Cache invalidated"
}
```

---

### Data Export (`/api/analytics/export`)

#### Request Export
```bash
POST /api/analytics/export
Content-Type: application/json
X-User-Id: user123

{
  "type": "SALES",
  "format": "CSV",
  "dateFrom": "2024-01-01",
  "dateTo": "2024-01-31",
  "filters": {
    "category": "electronics"
  }
}

Response: 202 Accepted
{
  "success": true,
  "data": {
    "exportId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "PENDING",
    "type": "SALES",
    "format": "CSV"
  }
}
```

#### Get Export Status
```bash
GET /api/analytics/export/{exportId}

Response: 200 OK
{
  "success": true,
  "data": {
    "exportId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    "type": "SALES",
    "format": "CSV",
    "filePath": "/exports/550e8400-e29b-41d4-a716-446655440000.csv"
  }
}
```

#### Download Export
```bash
GET /api/analytics/export/{exportId}/download
```

#### Get User Exports
```bash
GET /api/analytics/export?status=COMPLETED
X-User-Id: user123
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL 13+
- Redis 7+
- RabbitMQ 3.12+

### Build & Run

```bash
# Navigate to project directory
cd analytics-service

# Build with Maven
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/analytics-service-1.0.0.jar
```

### Database Setup

The application will automatically create tables on startup (via schema.sql). Ensure PostgreSQL is running and accessible at `localhost:5432`.

### Configuration

Edit `application.yml` to configure:
- Database connection
- Redis host/port
- RabbitMQ credentials
- JWT secret

---

## 📊 Event Types

Supported event types for tracking:
```
User Events:
- USER_REGISTRATION
- USER_LOGIN
- USER_LOGOUT
- PROFILE_UPDATE

Product Events:
- PRODUCT_VIEW
- PRODUCT_SEARCH
- PRODUCT_CLICK

Order Events:
- ORDER_CREATED
- ORDER_CONFIRMED
- ORDER_SHIPPED
- ORDER_DELIVERED
- ORDER_CANCELLED

Payment Events:
- PAYMENT_INITIATED
- PAYMENT_SUCCESS
- PAYMENT_FAILED
- PAYMENT_REFUNDED

Engagement Events:
- PRODUCT_REVIEW
- PRODUCT_RATING
- PRODUCT_SHARE
- USER_FOLLOW
- ADD_TO_CART
- REMOVE_FROM_CART
- CHECKOUT_STARTED
- PURCHASE_COMPLETED
```

---

## 🔄 Architecture

```
Controllers
    ↓
Services (Business Logic)
    ↓
Repositories (Data Access)
    ↓
Database + Redis + RabbitMQ
```

**Key Components:**
- **EventService**: Handles event ingestion and tracking
- **SalesAnalyticsService**: Aggregates and analyzes sales data
- **ProductAnalyticsService**: Tracks product performance metrics
- **UserAnalyticsService**: Manages user behavior and retention
- **DashboardService**: Provides aggregated dashboard data with caching
- **ExportService**: Handles asynchronous CSV/Excel exports
- **EventController**: REST endpoints for event ingestion
- **SalesAnalyticsController**: REST endpoints for sales data
- **ProductAnalyticsController**: REST endpoints for product metrics
- **UserAnalyticsController**: REST endpoints for user analytics
- **DashboardController**: REST endpoints for dashboard data
- **ExportController**: REST endpoints for data exports

---

## 🔐 Security

- JWT-based authentication (configurable)
- CORS enabled for cross-origin requests
- Event ingestion endpoints are publicly accessible (for event collection)
- All analytics endpoints require JWT token in Authorization header

---

## 📈 Caching Strategy

Uses Redis for caching:
- Sales summary: 60 minutes TTL
- Product metrics: 30 minutes TTL
- User metrics: 30 minutes TTL
- Dashboard data: 15 minutes TTL

Manual cache invalidation available via dashboard endpoint.

---

## 🐰 RabbitMQ Event Streaming

Events can be published to RabbitMQ for asynchronous processing:

```
Queue: analytics.events
    - Individual event processing
    
Queue: analytics.batch-events  
    - Batch event processing
    
Exchange: events.topic
```

---

## 📝 Phase 2 (Future Enhancements)

- Real-time WebSocket dashboards
- Revenue forecasting with ML models
- A/B Testing analytics
- Advanced anomaly detection
- Data warehouse integration

---

## 📄 License

ShopSphere © 2024

---

## 🤝 Support

For issues or questions, contact the Analytics Service team.
