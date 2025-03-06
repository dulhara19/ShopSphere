# Shipping Service - PR Review Checklist

Reference from PR #20 review. Use this to verify the new PR resolves all issues.

---

## Critical Issues (Must Fix)

### 1. Wrong directory structure
- **Problem:** Code placed at `shipping-service/` (repo root) instead of `services/shipping-service/`
- **Expected:** All source code under `services/shipping-service/src/main/java/...`
- **Check:** No files should exist at repo root `shipping-service/`

### 2. Wrong Java package name
- **Problem:** Package is `com.example.shipping_service`
- **Expected:** `com.shopsphere.shipping` (matches all other services)
- **Check:** All Java files use `package com.shopsphere.shipping.*`

### 3. Remove api-gateway/ directory
- **Problem:** PR added a full `api-gateway/` at repo root with Spring Cloud Gateway + a separate Vite React frontend
- **Expected:** We do NOT use an API gateway. Our frontend proxies via Next.js rewrites. This entire directory must not be included.

### 4. Remove discovery-server/ directory
- **Problem:** PR added a Eureka discovery server at repo root
- **Expected:** We do NOT use Eureka service discovery. This entire directory must not be included.

### 5. Remove separate docker-compose.yml
- **Problem:** PR added its own `docker-compose.yml` at root that spins up MySQL and other containers
- **Expected:** We have a single master `docker-compose.yml` at repo root. The shipping service should be added there, not in a separate file.

---

## Major Issues (Should Fix)

### 6. Remove Eureka config from application.yaml
- **Problem:** `application.yaml` registers with Eureka (`localhost:8761`)
- **Expected:** Remove the `eureka:` block entirely. We don't run Eureka.
- **Check:** No `eureka` or `@EnableDiscoveryClient` references

### 7. MongoDB should use shared infra
- **Problem:** `application.yaml` points to `localhost:27017`
- **Expected:** Should connect to the shared MongoDB from our master `docker-compose.yml`
- **Config should be:**
  ```yaml
  spring:
    data:
      mongodb:
        host: ${MONGODB_HOST:localhost}
        port: ${MONGODB_PORT:27017}
        database: shopsphere_shipping
  ```

### 8. Remove MySQL references
- **Problem:** The docker-compose in PR #20 had a MySQL container (`shipping_db`), but the actual code uses MongoDB
- **Expected:** No MySQL references anywhere. Confirmed the code uses `spring-boot-starter-data-mongodb`, `MongoRepository`, and `@Document`

### 9. Port must be 3006
- **Check:** `server.port=3006` in application.yaml (this was correct in PR #20)

---

## Minor Issues (Nice to Fix)

### 10. CORS configuration
- **Problem:** `WebConfig.java` hardcodes `allowedOrigins("http://localhost:5173")` (Vite dev server)
- **Expected:** Either remove custom CORS config (Next.js proxy handles it) or set to `http://localhost:3000`

### 11. Use DTOs instead of raw entities
- **Problem:** Controller returns `Shipping` entity directly
- **Expected:** Use request/response DTOs (e.g., `CreateShipmentRequest`, `ShipmentResponse`)

### 12. Sinhala comments
- **Problem:** Some inline comments are in Sinhala
- **Expected:** All comments in English for team consistency

---

## What Was Working (Keep These)

The core logic from PR #20 was functional. Make sure the new PR retains:

- [x] MongoDB model with `@Document(collection = "shipping")` and validation annotations
- [x] `MongoRepository` with `findByTrackingNumber`
- [x] Shipping cost calculation by carrier (FedEx, DHL, UPS)
- [x] Create label / Track / Update status endpoints
- [x] PDF label generation using iText
- [x] `GlobalExceptionHandler` for validation errors
- [x] Port 3006

---

## API Endpoints Expected

Per the EPICS.md, these are the MVP endpoints:

```
POST   /api/shipping/validate-address
GET    /api/shipping/zones
GET    /api/shipping/zones/{country}
POST   /api/shipping/calculate-rate
POST   /api/shipping/create-label
GET    /api/shipping/track/{trackingNumber}
PUT    /api/shipping/update-status/{trackingNumber}
GET    /api/shipping/download-label/{trackingNumber}
```

---

## Quick Merge Checklist

- [ ] All code under `services/shipping-service/`
- [ ] Package: `com.shopsphere.shipping`
- [ ] No `api-gateway/`, `discovery-server/`, or extra `docker-compose.yml`
- [ ] No Eureka references
- [ ] MongoDB config with env variable support
- [ ] No MySQL references
- [ ] Port 3006
- [ ] CORS removed or updated to localhost:3000
- [ ] Uses DTOs for API responses
- [ ] All comments in English
