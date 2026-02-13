# PR Review Report: inventory-service

**Reviewer:** Master Agent
**Date:** 2026-02-14
**Branch:** dev (from service/inventory-service merge)
**Service:** Inventory Service
**Owner:** Team Member 3

---

## Summary

This PR implements **Phase 1 (MVP)** of the Inventory Service, providing core inventory tracking and reservation capabilities for the ShopSphere e-commerce platform. The implementation includes:

- Basic inventory management (CRUD operations)
- Stock reservation system for checkout flows
- Low stock alerts and notifications
- Internal service-to-service communication endpoints
- Kafka event publishing for stock updates

---

## EPICS Progress

### MVP Status: **3/4 Epics Substantially Complete**

| Epic | Status | Completion |
|------|--------|------------|
| Epic 1.1: Basic Inventory Management | **MOSTLY COMPLETE** | 4/5 stories |
| Epic 1.2: Stock Reservation System | **COMPLETE** | 5/5 stories |
| Epic 1.3: Low Stock Alerts | **COMPLETE** | 5/5 stories |
| Epic 1.4: Internal Service Communication | **COMPLETE** | 3/3 stories |

---

## Detailed EPICS Validation

### Epic 1.1: Basic Inventory Management

| Story | Description | Implemented | Notes |
|-------|-------------|-------------|-------|
| 1.1.1 | Initialize inventory for product | **YES** | `POST /api/inventory` - Creates record with default quantity |
| 1.1.2 | Get stock level by product ID | **YES** | `GET /api/inventory/{productId}` - Returns quantity and status |
| 1.1.3 | Update stock quantity (Seller) | **YES** | `PUT /api/inventory/{productId}` - Validates non-negative |
| 1.1.4 | Bulk stock update | **PARTIAL** | Endpoint exists but returns TODO comment |
| 1.1.5 | Delete inventory record | **YES** | `DELETE /api/inventory/{productId}` |

**Acceptance Criteria Check:**
- [x] Create inventory record when product created
- [x] Default quantity: 0
- [x] Link to productId (unique constraint)
- [x] Return current quantity and status
- [x] Validate non-negative quantity
- [x] Record update timestamp (via JPA @UpdateTimestamp)
- [ ] CSV import support for bulk update (NOT IMPLEMENTED)

### Epic 1.2: Stock Reservation System

| Story | Description | Implemented | Notes |
|-------|-------------|-------------|-------|
| 1.2.1 | Reserve stock for checkout | **YES** | `POST /api/inventory/reserve` |
| 1.2.2 | Confirm reservation | **YES** | `POST /api/inventory/confirm` |
| 1.2.3 | Release reservation | **YES** | `POST /api/inventory/release` |
| 1.2.4 | Check availability | **YES** | `POST /api/inventory/check-availability` |
| 1.2.5 | Reservation expiry job | **YES** | @Scheduled every 5 minutes |

**Acceptance Criteria Check:**
- [x] Decrease available quantity on reserve
- [x] Increase reserved quantity on reserve
- [x] Return reservation ID
- [x] Fail if insufficient stock (InsufficientStockException)
- [x] Convert reserved to sold on confirm
- [x] Auto-release after 15 minutes (configurable)
- [x] Batch check support (partial - needs improvement)

### Epic 1.3: Low Stock Alerts

| Story | Description | Implemented | Notes |
|-------|-------------|-------------|-------|
| 1.3.1 | Set low stock threshold | **YES** | `PUT /api/inventory/{productId}/threshold` |
| 1.3.2 | Detect low stock | **YES** | Via `updateStatus()` method |
| 1.3.3 | Send low stock notification | **YES** | Via NotificationService |
| 1.3.4 | Get low stock products | **YES** | `GET /api/inventory/low-stock` |
| 1.3.5 | Out of stock handling | **YES** | `GET /api/inventory/out-of-stock` + events |

**Acceptance Criteria Check:**
- [x] Per product threshold
- [x] Default threshold configurable (5)
- [x] Trigger when quantity <= threshold
- [x] Update status to LOW_STOCK/OUT_OF_STOCK
- [x] Notify via Notification Service
- [x] Publish events (low stock, out of stock)

### Epic 1.4: Internal Service Communication

| Story | Description | Implemented | Notes |
|-------|-------------|-------------|-------|
| 1.4.1 | Get stock for product (internal) | **YES** | `GET /internal/inventory/{productId}` |
| 1.4.2 | Batch get stock levels | **YES** | `POST /internal/inventory/batch` |
| 1.4.3 | Stock update events | **YES** | Kafka events published |

---

## Contract Compliance

### Endpoints Validated Against `shared/contracts/inventory-service.yaml`

| Endpoint | Method | Contract Match | Issues |
|----------|--------|----------------|--------|
| `/api/inventory` | POST | **YES** | Creates inventory correctly |
| `/api/inventory/{productId}` | GET | **YES** | Returns InventoryResponse |
| `/api/inventory/{productId}` | PUT | **PARTIAL** | Missing `operation` field (SET/ADD/SUBTRACT) |
| `/api/inventory/{productId}` | DELETE | **YES** | Returns 204 No Content |
| `/api/inventory/bulk-update` | POST | **NO** | Not fully implemented (TODO) |
| `/api/inventory/{productId}/threshold` | PUT | **PARTIAL** | Takes query param instead of request body |
| `/api/inventory/reserve` | POST | **PARTIAL** | Single item request vs array of items |
| `/api/inventory/confirm` | POST | **YES** | Matches contract |
| `/api/inventory/release` | POST | **YES** | Matches contract |
| `/api/inventory/check-availability` | POST | **PARTIAL** | Contract expects items array |
| `/api/inventory/low-stock` | GET | **PARTIAL** | Missing pagination params |
| `/api/inventory/out-of-stock` | GET | **PARTIAL** | Missing pagination params |
| `/internal/inventory/{productId}` | GET | **YES** | Matches contract |
| `/internal/inventory/batch` | POST | **YES** | Matches contract |

### Contract Compliance Issues:

1. **Reserve Stock Request:** Contract expects `items` array, implementation takes single product
2. **Check Availability Request:** Contract expects `items` array, implementation has both but dispatch logic uses `Object` type
3. **Set Threshold:** Contract expects request body, implementation uses query param
4. **Update Inventory:** Contract has `operation` enum (SET/ADD/SUBTRACT), not implemented
5. **Low/Out of Stock Lists:** Missing pagination support (page, size params)

---

## Event Schema Validation

**Note:** `shared/event-schemas/inventory-events.json` file does NOT exist.

### Events Being Published:

| Event | Topic | Payload |
|-------|-------|---------|
| `inventory.updated` | `stock-updated` | productId, quantity, reservedQuantity, availableQuantity, status, timestamp |
| `inventory.low` | `stock-low` | Same as above + eventType: LOW_STOCK |
| `inventory.out` | `stock-out-of-stock` | Same as above + eventType: OUT_OF_STOCK |

**Missing Events per EPICS:**
- `inventory.reserved` - NOT PUBLISHED
- `inventory.released` - NOT PUBLISHED

**Schema Issues:**
- [ ] Event schema file needs to be created at `shared/event-schemas/inventory-events.json`
- [ ] Events should use standardized EventEnvelope structure

---

## Code Quality Assessment

### Strengths:
- [x] Clean separation of concerns (Controller, Service, Repository layers)
- [x] Proper use of DTOs for API responses
- [x] Comprehensive exception handling with GlobalExceptionHandler
- [x] Good use of Lombok to reduce boilerplate
- [x] Proper JPA entity design with indexes
- [x] Transactional annotations on service methods
- [x] Logging implemented throughout
- [x] Configuration externalized via application.yml

### Issues Found:

#### Critical Issues (Must Fix):

1. **No Tests Found** (`services/inventory-service/src/test/**/*.java` - empty)
   - Acceptance criteria requires minimum 80% coverage
   - No unit tests for services
   - No integration tests for controllers

2. **Scheduled Job Syntax Error** (`ReservationService.java:150`)
   ```java
   @Scheduled(fixedRateString = "${inventory.reservation.cleanup-interval-minutes:5}m", ...)
   ```
   - `5m` suffix is invalid - Spring expects milliseconds or uses different syntax
   - Should be `fixedRate = 300000` or use `@Scheduled(cron = "...")`

3. **checkAvailability Method Uses Object Type** (`InventoryController.java:121`)
   ```java
   public ResponseEntity<?> checkAvailability(@Valid @RequestBody Object request)
   ```
   - Type safety issue - `instanceof` checks unreliable with JSON deserialization
   - Will always fail batch checking as Jackson won't deserialize to `CheckAvailabilityRequest`

#### Warnings (Should Fix):

1. **Bulk Update Not Implemented** (`InventoryController.java:68-73`)
   - Returns `202 Accepted` but does nothing
   - Has TODO comment

2. **Missing Input Validation on Threshold** (`InventoryController.java:157-159`)
   - No validation that threshold is positive

3. **Hardcoded Database Credentials** (`application.yml:7-8`)
   ```yaml
   username: postgres
   password: postgres
   ```
   - Should use environment variables or secrets management

4. **Missing Pagination** on `getLowStockProducts` and `getOutOfStockProducts`

5. **Context Path Configuration** (`application.yml:47`)
   - `context-path: /api` means endpoints are `/api/inventory/*`
   - But controller also has `/inventory` mapping
   - Actual endpoints are `/api/inventory/*` - may cause confusion vs contract

#### Suggestions (Nice to Have):

1. Add OpenAPI/Swagger documentation annotations
2. Consider using Spring Data REST for simpler CRUD
3. Add health check endpoint
4. Consider Redis caching for frequently accessed inventory data
5. Add metrics/observability

---

## Security Assessment

| Check | Status | Notes |
|-------|--------|-------|
| SQL Injection | **SAFE** | Using JPA parameterized queries |
| Input Validation | **PARTIAL** | @Valid on requests, but missing some |
| Auth/AuthZ | **MISSING** | No security configured (expected at gateway?) |
| Secrets | **WARNING** | Hardcoded DB password in config |
| CORS | **N/A** | Not configured (expected at gateway?) |

---

## Architecture Compliance

| Check | Status |
|-------|--------|
| Uses Spring Boot | **YES** |
| Uses Spring Data JPA | **YES** |
| Repository pattern | **YES** |
| Service layer pattern | **YES** |
| DTOs separate from entities | **YES** |
| Follows Google Java Style | **MOSTLY** |
| Uses Kafka for events | **YES** |

---

## Decision

### CHANGES REQUESTED

Please address the following before merge:

#### Must Fix:
1. **Add unit and integration tests** - Minimum 80% coverage required
2. **Fix scheduled job syntax** - `fixedRateString` value is invalid
3. **Fix checkAvailability endpoint** - Type-safe request handling needed
4. **Create event schema file** - `shared/event-schemas/inventory-events.json`

#### Should Fix:
5. Implement bulk update functionality (or remove the endpoint)
6. Add pagination to low-stock and out-of-stock endpoints
7. Align request/response formats with API contract
8. Remove hardcoded credentials from application.yml

#### Missing Events:
9. Publish `inventory.reserved` event when stock is reserved
10. Publish `inventory.released` event when reservation is released

---

## Update Instructions for Master Status

Once issues are resolved and PR is approved, update `_bmad-output/master/service-status/inventory-service.md`:

```yaml
mvpStatus: IN_PROGRESS → PHASE_1_COMPLETE
lastReviewedPR: dev-phase1-review
lastUpdated: 2026-02-14
```

Update story statuses:
- Epic 1.1: 4/5 DONE (1.1.4 partial)
- Epic 1.2: 5/5 DONE
- Epic 1.3: 5/5 DONE
- Epic 1.4: 3/3 DONE

---

## Files Changed Summary

### Controllers (2 files)
- `InventoryController.java` - Main API endpoints
- `InternalInventoryController.java` - Internal service endpoints

### Services (4 files)
- `InventoryService.java` - Core inventory operations
- `ReservationService.java` - Reservation management
- `LowStockService.java` - Low stock detection/alerts
- `InventoryEventService.java` - Kafka event publishing
- `NotificationService.java` - Notification integration

### Models/Entities (2 files)
- `Inventory.java` - Inventory entity
- `StockReservation.java` - Reservation entity

### Repositories (2 files)
- `InventoryRepository.java`
- `StockReservationRepository.java`

### DTOs (6 files)
- `InventoryDTO.java`
- `ReservationDTO.java`
- Request DTOs (4 files)

### Config (2 files)
- `KafkaConfig.java`
- `application.yml`

### Exception Handling (5 files)
- `GlobalExceptionHandler.java`
- Custom exception classes (4 files)

### Tests (0 files)
- **NO TESTS FOUND** - Critical issue

---

*Review generated by Master Agent on 2026-02-14*
