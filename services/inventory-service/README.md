# Inventory Service - ShopSphere

A Spring Boot microservice for managing inventory tracking, stock reservations, and low stock alerts in the ShopSphere e-commerce platform.

## Quick Start

### Prerequisites
- Java 11+
- Maven 3.6+
- PostgreSQL 12+
- Kafka 2.8+
- Redis 6.0+ (optional, for caching)

### Configuration

1. **Database Setup**
```sql
CREATE DATABASE shopsphere_inventory;
```

2. **Update application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopsphere_inventory
    username: your_username
    password: your_password
```

3. **Kafka Setup**
Ensure Kafka is running on `localhost:9092`

### Build & Run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or run the JAR
java -jar target/inventory-service-1.0.0.jar
```

The service will start on port **3003**.

## API Documentation

### Create Inventory
```bash
POST /api/inventory
Content-Type: application/json

{
  "product_id": "uuid",
  "quantity": 100,
  "low_stock_threshold": 5
}
```

### Get Stock Level
```bash
GET /api/inventory/{productId}
```

### Reserve Stock
```bash
POST /api/inventory/reserve
Content-Type: application/json

{
  "product_id": "uuid",
  "quantity": 2,
  "order_id": "uuid"
}
```

### Check Availability
```bash
POST /api/inventory/check-availability
Content-Type: application/json

{
  "product_id": "uuid",
  "quantity": 2
}
```

### Get Low Stock Items
```bash
GET /api/inventory/low-stock
```

## Architecture

```
┌─────────────────┐
│  API Consumers  │
└────────┬────────┘
         │
    ┌────▼────────────────┐
    │InventoryController  │
    └────┬─────────────────┘
         │
    ┌────▼──────────────┐
    │InventoryService   │
    │ReservationService │
    │LowStockService    │
    └────┬──────────────┘
         │
    ┌────▼──────────┐
    │  Repositories │
    │   (JPA)       │
    └────┬──────────┘
         │
    ┌────▼──────────────┐
    │   PostgreSQL      │
    │   Database        │
    └───────────────────┘
         
    ┌──────────────────┐
    │      Kafka       │
    │  Event Bus       │
    └──────────────────┘
```

## Key Features

✅ **Basic Inventory Management**
- Create, read, update, delete inventory records
- Track quantity and availability

✅ **Stock Reservation**
- Reserve stock for checkout flow
- Auto-release expired reservations (15 minutes default)
- Confirm or release reservations

✅ **Low Stock Alerts**
- Configurable low stock threshold per product
- Automatic status updates
- Kafka-based notifications

✅ **Internal Service APIs**
- Batch inventory queries
- Stock availability checks
- Integration with Product and Order services

## Scheduled Tasks

### Reservation Expiry Cleanup
- Runs every 5 minutes
- Auto-releases expired pending reservations
- Frees up reserved stock for other customers

## Monitoring

Health check endpoint:
```bash
GET /actuator/health
```

Metrics endpoint:
```bash
GET /actuator/metrics
```

## Project Status

**Phase 1 - MVP: ✅ COMPLETE**
- Epic 1.1: Basic Inventory Management ✅
- Epic 1.2: Stock Reservation System ✅
- Epic 1.3: Low Stock Alerts ✅
- Epic 1.4: Internal Service Communication ✅

**Phase 2 - Enhanced Features: ⏳ TODO**
- Multi-warehouse support
- Stock history & audit trail
- Inventory analytics
- Demand forecasting

## Contributing

When making changes:
1. Create a feature branch
2. Make your changes
3. Write tests
4. Submit a pull request
5. Merge to main after approval

## Support

For issues or questions, contact: Team Member 3

---

**Service Name**: Inventory Service  
**Port**: 3003  
**Owner**: Team Member 3  
**Last Updated**: February 10, 2026
