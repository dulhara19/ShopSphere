---
documentType: environment-config
projectName: ShopSphere
maintainer: LakshanDulhara (Team Lead)
lastUpdated: 2026-02-14
activeEnvironment: LOCAL
---

# ShopSphere Environment Configuration

> **Purpose:** Central configuration for all service endpoints across environments. Frontend and integration tests use this as single source of truth.

## Active Environment

**Current:** `LOCAL`

| Environment | Status | Description |
|-------------|--------|-------------|
| `LOCAL` | **ACTIVE** | Local development (localhost) |
| `DOCKER` | Inactive | Docker Compose deployment |
| `STAGING` | Inactive | AWS staging environment |
| `PRODUCTION` | Inactive | AWS production environment |

---

## Environment Definitions

### LOCAL Environment

> Individual services running on localhost with different ports

```yaml
environment: LOCAL
api_gateway:
  enabled: false
  base_url: null

services:
  user-service:
    base_url: "http://localhost:3001"
    health: "http://localhost:3001/actuator/health"
    status: OFFLINE

  product-service:
    base_url: "http://localhost:3002"
    health: "http://localhost:3002/actuator/health"
    status: OFFLINE

  inventory-service:
    base_url: "http://localhost:3003"
    health: "http://localhost:3003/actuator/health"
    status: OFFLINE

  order-service:
    base_url: "http://localhost:3004"
    health: "http://localhost:3004/actuator/health"
    status: OFFLINE

  payment-service:
    base_url: "http://localhost:3005"
    health: "http://localhost:3005/actuator/health"
    status: OFFLINE

  shipping-service:
    base_url: "http://localhost:3006"
    health: "http://localhost:3006/actuator/health"
    status: OFFLINE

  review-service:
    base_url: "http://localhost:3007"
    health: "http://localhost:3007/actuator/health"
    status: OFFLINE

  recommendation-service:
    base_url: "http://localhost:3008"
    health: "http://localhost:3008/actuator/health"
    status: OFFLINE

  notification-service:
    base_url: "http://localhost:3009"
    health: "http://localhost:3009/actuator/health"
    status: OFFLINE

  analytics-service:
    base_url: "http://localhost:3010"
    health: "http://localhost:3010/actuator/health"
    status: OFFLINE

infrastructure:
  postgresql:
    host: localhost
    port: 5432
    database: shopsphere

  mongodb:
    host: localhost
    port: 27017
    database: shopsphere

  redis:
    host: localhost
    port: 6379

  rabbitmq:
    host: localhost
    port: 5672
    management: 15672
```

---

### DOCKER Environment

> All services running in Docker Compose with internal networking

```yaml
environment: DOCKER
api_gateway:
  enabled: true
  base_url: "http://localhost:8000"

services:
  user-service:
    base_url: "http://localhost:8000/api/users"
    internal_url: "http://user-service:3001"
    status: OFFLINE

  product-service:
    base_url: "http://localhost:8000/api/products"
    internal_url: "http://product-service:3002"
    status: OFFLINE

  inventory-service:
    base_url: "http://localhost:8000/api/inventory"
    internal_url: "http://inventory-service:3003"
    status: OFFLINE

  order-service:
    base_url: "http://localhost:8000/api/orders"
    internal_url: "http://order-service:3004"
    status: OFFLINE

  payment-service:
    base_url: "http://localhost:8000/api/payments"
    internal_url: "http://payment-service:3005"
    status: OFFLINE

  shipping-service:
    base_url: "http://localhost:8000/api/shipping"
    internal_url: "http://shipping-service:3006"
    status: OFFLINE

  review-service:
    base_url: "http://localhost:8000/api/reviews"
    internal_url: "http://review-service:3007"
    status: OFFLINE

  recommendation-service:
    base_url: "http://localhost:8000/api/recommendations"
    internal_url: "http://recommendation-service:3008"
    status: OFFLINE

  notification-service:
    base_url: "http://localhost:8000/api/notifications"
    internal_url: "http://notification-service:3009"
    status: OFFLINE

  analytics-service:
    base_url: "http://localhost:8000/api/analytics"
    internal_url: "http://analytics-service:3010"
    status: OFFLINE

infrastructure:
  postgresql:
    host: postgres
    port: 5432

  mongodb:
    host: mongodb
    port: 27017

  redis:
    host: redis
    port: 6379

  rabbitmq:
    host: rabbitmq
    port: 5672
```

---

### STAGING Environment

> AWS Staging deployment

```yaml
environment: STAGING
api_gateway:
  enabled: true
  base_url: "https://staging-api.shopsphere.com"

services:
  user-service:
    base_url: "https://staging-api.shopsphere.com/api/users"
    status: OFFLINE

  product-service:
    base_url: "https://staging-api.shopsphere.com/api/products"
    status: OFFLINE

  # ... (all services follow same pattern)

infrastructure:
  postgresql:
    host: "shopsphere-staging.xxxxx.us-east-1.rds.amazonaws.com"
    port: 5432

  redis:
    host: "shopsphere-staging.xxxxx.cache.amazonaws.com"
    port: 6379

  rabbitmq:
    host: "b-xxxxx.mq.us-east-1.amazonaws.com"
    port: 5671
```

---

### PRODUCTION Environment

> AWS Production deployment

```yaml
environment: PRODUCTION
api_gateway:
  enabled: true
  base_url: "https://api.shopsphere.com"

services:
  user-service:
    base_url: "https://api.shopsphere.com/api/users"
    status: OFFLINE

  # ... (all services follow same pattern)
```

---

## Frontend Configuration Generator

### TypeScript Config (for React Frontend)

```typescript
// frontend/src/config/api.config.ts
// AUTO-GENERATED - DO NOT EDIT MANUALLY
// Last updated: 2026-02-14
// Active environment: LOCAL

export type Environment = 'LOCAL' | 'DOCKER' | 'STAGING' | 'PRODUCTION';

export const CURRENT_ENV: Environment = 'LOCAL';

export const API_CONFIG = {
  LOCAL: {
    USER_SERVICE: 'http://localhost:3001',
    PRODUCT_SERVICE: 'http://localhost:3002',
    INVENTORY_SERVICE: 'http://localhost:3003',
    ORDER_SERVICE: 'http://localhost:3004',
    PAYMENT_SERVICE: 'http://localhost:3005',
    SHIPPING_SERVICE: 'http://localhost:3006',
    REVIEW_SERVICE: 'http://localhost:3007',
    RECOMMENDATION_SERVICE: 'http://localhost:3008',
    NOTIFICATION_SERVICE: 'http://localhost:3009',
    ANALYTICS_SERVICE: 'http://localhost:3010',
  },
  DOCKER: {
    // All services behind API Gateway
    API_GATEWAY: 'http://localhost:8000',
    USER_SERVICE: 'http://localhost:8000/api/users',
    PRODUCT_SERVICE: 'http://localhost:8000/api/products',
    INVENTORY_SERVICE: 'http://localhost:8000/api/inventory',
    ORDER_SERVICE: 'http://localhost:8000/api/orders',
    PAYMENT_SERVICE: 'http://localhost:8000/api/payments',
    SHIPPING_SERVICE: 'http://localhost:8000/api/shipping',
    REVIEW_SERVICE: 'http://localhost:8000/api/reviews',
    RECOMMENDATION_SERVICE: 'http://localhost:8000/api/recommendations',
    NOTIFICATION_SERVICE: 'http://localhost:8000/api/notifications',
    ANALYTICS_SERVICE: 'http://localhost:8000/api/analytics',
  },
  STAGING: {
    API_GATEWAY: 'https://staging-api.shopsphere.com',
    // All services use API Gateway base
  },
  PRODUCTION: {
    API_GATEWAY: 'https://api.shopsphere.com',
    // All services use API Gateway base
  },
};

export const getApiUrl = (service: keyof typeof API_CONFIG['LOCAL']): string => {
  return API_CONFIG[CURRENT_ENV][service];
};
```

---

## Service Status Quick Check

| Service | LOCAL | DOCKER | STAGING | PRODUCTION |
|---------|-------|--------|---------|------------|
| User Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Product Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Inventory Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Order Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Payment Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Shipping Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Review Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Recommendation Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Notification Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |
| Analytics Service | OFFLINE | OFFLINE | OFFLINE | OFFLINE |

---

## Environment Variables Template

```bash
# .env.local
REACT_APP_ENV=LOCAL
REACT_APP_API_GATEWAY_URL=

# Service URLs (LOCAL - direct connection)
REACT_APP_USER_SERVICE_URL=http://localhost:3001
REACT_APP_PRODUCT_SERVICE_URL=http://localhost:3002
REACT_APP_INVENTORY_SERVICE_URL=http://localhost:3003
REACT_APP_ORDER_SERVICE_URL=http://localhost:3004
REACT_APP_PAYMENT_SERVICE_URL=http://localhost:3005
REACT_APP_SHIPPING_SERVICE_URL=http://localhost:3006
REACT_APP_REVIEW_SERVICE_URL=http://localhost:3007
REACT_APP_RECOMMENDATION_SERVICE_URL=http://localhost:3008
REACT_APP_NOTIFICATION_SERVICE_URL=http://localhost:3009
REACT_APP_ANALYTICS_SERVICE_URL=http://localhost:3010
```

```bash
# .env.docker
REACT_APP_ENV=DOCKER
REACT_APP_API_GATEWAY_URL=http://localhost:8000

# All services behind gateway
REACT_APP_USER_SERVICE_URL=http://localhost:8000/api/users
REACT_APP_PRODUCT_SERVICE_URL=http://localhost:8000/api/products
# ... etc
```

---

## Change Log

| Date | Change | By |
|------|--------|-----|
| 2026-02-14 | Environment config created | Lead |
