# Inventory Service - Implementation Complete

## Overview

The Inventory Service is a Spring Boot microservice responsible for managing inventory tracking and stock reservations for the ShopSphere e-commerce platform.

**Port:** 3003
**Tech Stack:** Spring Boot 2.7.14, Spring Data JPA, PostgreSQL, Redis, Kafka

## Implemented Features

### Phase 1 - MVP (Core Features)

#### Epic 1.1: Basic Inventory Management ✅
- **1.1.1**: Initialize inventory for product (POST `/api/inventory`)
- **1.1.2**: Get stock level by product ID (GET `/api/inventory/{productId}`)
- **1.1.3**: Update stock quantity (PUT `/api/inventory/{productId}`)
- **1.1.4**: Bulk stock update (POST `/api/inventory/bulk-update`) - *Partially implemented*
- **1.1.5**: Delete inventory record (DELETE `/api/inventory/{productId}`)

**Database Table:** `inventory`
- `id` (UUID, PK)
- `product_id` (UUID, Unique)
- `quantity` (Long)
- `reserved_quantity` (Long)
- `low_stock_threshold` (Long)
- `status` (Enum: IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
- `created_at` (Timestamp)
- `last_updated` (Timestamp)

#### Epic 1.2: Stock Reservation System ✅
- **1.2.1**: Reserve stock for checkout (POST `/api/inventory/reserve`)
- **1.2.2**: Confirm reservation (POST `/api/inventory/confirm`)
- **1.2.3**: Release reservation (POST `/api/inventory/release`)
- **1.2.4**: Check availability (POST `/api/inventory/check-availability`)
- **1.2.5**: Reservation expiry job - Auto-releases expired reservations every 5 minutes

**Database Table:** `stock_reservation`
- `id` (UUID, PK)
- `product_id` (UUID)
- `quantity` (Long)
- `status` (Enum: PENDING, CONFIRMED, RELEASED)
- `order_id` (UUID)
- `created_at` (Timestamp)
- `expires_at` (Timestamp)
- `confirmed_at` (Timestamp)
- `released_at` (Timestamp)

#### Epic 1.3: Low Stock Alerts ✅
- **1.3.1**: Set low stock threshold (PUT `/api/inventory/{productId}/threshold`)
- **1.3.2**: Detect low stock - triggered on stock updates
- **1.3.3**: Send low stock notification via Kafka
- **1.3.4**: Get low stock products (GET `/api/inventory/low-stock`)
- **1.3.5**: Out of stock handling (GET `/api/inventory/out-of-stock`)

#### Epic 1.4: Internal Service Communication ✅
- **1.4.1**: Get stock for product (GET `/internal/inventory/{productId}`)
- **1.4.2**: Batch get stock levels (POST `/internal/inventory/batch`)
- **1.4.3**: Stock update events published to Kafka

## API Endpoints

### Public Endpoints (Base URL: `/api/inventory`)

#### Inventory Management
```
POST   /inventory                          - Create inventory
GET    /inventory/{productId}              - Get stock level
PUT    /inventory/{productId}              - Update stock quantity
POST   /inventory/bulk-update              - Bulk update stock
DELETE /inventory/{productId}              - Delete inventory
```

#### Stock Reservation
```
POST   /inventory/reserve                  - Reserve stock
POST   /inventory/confirm                  - Confirm reservation
POST   /inventory/release                  - Release reservation
POST   /inventory/check-availability       - Check stock availability
```

#### Monitoring
```
GET    /inventory/low-stock                - Get all low stock items
GET    /inventory/out-of-stock             - Get all out of stock items
GET    /inventory/{productId}/reservations - Get product reservations
GET    /inventory/order/{orderId}/reservations - Get order reservations
```

### Internal Endpoints (Base URL: `/internal/inventory`)

```
GET    /internal/inventory/{productId}              - Get stock (internal)
POST   /internal/inventory/batch                    - Batch get stocks
GET    /internal/inventory/{productId}/available/{quantity} - Check availability
```

## Project Structure

```
inventory-service/
├── src/
│   ├── main/
│   │   ├── java/com/shopsphere/inventory/
│   │   │   ├── config/              - Spring configuration
│   │   │   ├── controller/          - REST controllers
│   │   │   ├── dto/                 - Data Transfer Objects
│   │   │   ├── event/               - Event handling
│   │   │   ├── exception/           - Custom exceptions
│   │   │   ├── model/               - JPA entities
│   │   │   ├── repository/          - Data repositories
│   │   │   ├── security/            - Security config (if needed)
│   │   │   ├── service/             - Business logic
│   │   │   └── InventoryServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml       - Main configuration
│   │       └── application-test.yml  - Test configuration
│   └── test/
│       └── java/com/shopsphere/inventory/
└── pom.xml
```

## Service Classes

### InventoryService
- Handles basic inventory operations (CRUD)
- Stock level queries
- Status updates (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)

### ReservationService
- Stock reservation for checkout flow
- Reservation confirmation and release
- Automatic expiry handling via scheduled job (@Scheduled)
- Availability checking

### LowStockService
- Low stock threshold management
- Low stock detection and notifications
- Out of stock handling
- Integration with Notification Service

### InventoryEventService
- Publishes stock-related events to Kafka
- Topics: `stock-updated`, `stock-low`, `stock-out-of-stock`

### NotificationService
- Sends notifications via Kafka
- Topics: `low-stock-notification`, `out-of-stock-notification`

## Database Models

### Inventory Entity
```java
- id: UUID (Primary Key)
- productId: UUID (Unique, Foreign Key to Product Service)
- quantity: Long (total stock)
- reservedQuantity: Long (stock reserved for pending orders)
- lowStockThreshold: Long (configurable per product)
- status: StockStatus (computed based on quantity and threshold)
- createdAt: LocalDateTime
- lastUpdated: LocalDateTime
- availableQuantity: Long (computed: quantity - reservedQuantity)
```

### StockReservation Entity
```java
- id: UUID (Primary Key)
- productId: UUID
- quantity: Long
- status: ReservationStatus (PENDING, CONFIRMED, RELEASED)
- orderId: UUID (reference to Order Service)
- createdAt: LocalDateTime
- expiresAt: LocalDateTime (auto-expires after 15 minutes - configurable)
- confirmedAt: LocalDateTime
- releasedAt: LocalDateTime
```

## Configuration

### Key Properties (application.yml)

```yaml
server:
  port: 3003

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopsphere_inventory
    username: postgres
    password: postgres

  jpa:
    hibernate:
      ddl-auto: validate  # Use 'update' for development, 'validate' for production

  kafka:
    bootstrap-servers: localhost:9092

inventory:
  reservation:
    expiry-minutes: 15  # How long a reservation lasts before auto-release
    cleanup-interval-minutes: 5  # How often to check for expired reservations
  low-stock:
    default-threshold: 5
    notification-enabled: true
```

## Kafka Events

### Published Events
1. **stock-updated**: Triggered when inventory quantity changes
2. **stock-low**: Triggered when stock falls below threshold
3. **stock-out-of-stock**: Triggered when stock reaches zero
4. **low-stock-notification**: Notification to seller
5. **out-of-stock-notification**: Notification to seller

## Scheduled Jobs

### Reservation Expiry Job
- **Frequency**: Every 5 minutes (configurable via `inventory.reservation.cleanup-interval-minutes`)
- **Purpose**: Auto-releases expired reservations to free up reserved stock
- **Logic**: Finds all PENDING reservations where `expiresAt < now` and releases them

## Exception Handling

Custom exceptions implemented:
- `ProductNotFoundException`: When inventory not found for a product
- `InsufficientStockException`: When requested quantity exceeds available stock
- `ReservationNotFoundException`: When reservation ID not found
- `InventoryException`: Base exception for inventory-related errors

Global exception handler with proper HTTP status codes and error responses.

## Testing

Test configuration available in `application-test.yml` using H2 in-memory database.

## Integration Points

### Kafka Topics to Subscribe
- `product.created` - Initialize inventory when new product created
- `product.deleted` - Delete inventory when product deleted
- `order.placed` - Trigger confirmation of reservations
- `order.cancelled` - Release reservations

### Kafka Topics to Publish
- `stock-updated` - Notify other services of stock changes
- `stock-low` - Alert about low inventory
- `stock-out-of-stock` - Alert about out of stock
- `low-stock-notification` - Send notification to Notification Service
- `out-of-stock-notification` - Send notification to Notification Service

## Next Steps for Phase 2

- Multi-warehouse support
- Stock history and audit trail
- Inventory analytics
- ML-based demand forecasting
- Advanced reporting

---

**Owner**: Team Member 3
**Last Updated**: February 10, 2026
