# Analytics Service - Development Guide

## Project Structure

```
analytics-service/
├── pom.xml                           # Maven dependencies and build config
├── README.md                         # API documentation
├── EPICS.md                          # Feature epics and requirements
├── .gitignore                        # Git ignore rules
├── docs/
│   └── EPICS.md                      # Feature breakdown by phase
├── src/
│   ├── main/
│   │   ├── java/com/shopsphere/analytics/
│   │   │   ├── AnalyticsServiceApplication.java    # Main application class
│   │   │   ├── config/               # Spring configurations
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── RabbitMQConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/           # REST API controllers
│   │   │   │   ├── EventController.java
│   │   │   │   ├── SalesAnalyticsController.java
│   │   │   │   ├── ProductAnalyticsController.java
│   │   │   │   ├── UserAnalyticsController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   └── ExportController.java
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   ├── EventDTO.java
│   │   │   │   ├── BatchEventDTO.java
│   │   │   │   ├── SalesMetricDTO.java
│   │   │   │   ├── SalesSummaryDTO.java
│   │   │   │   ├── ProductMetricDTO.java
│   │   │   │   ├── UserMetricDTO.java
│   │   │   │   ├── DashboardDTO.java
│   │   │   │   ├── ExportRequestDTO.java
│   │   │   │   ├── ExportResponseDTO.java
│   │   │   │   └── ApiResponseDTO.java
│   │   │   ├── exception/            # Exception handling
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── model/                # JPA entities
│   │   │   │   ├── Event.java
│   │   │   │   ├── SalesMetric.java
│   │   │   │   ├── ProductMetric.java
│   │   │   │   ├── UserMetric.java
│   │   │   │   ├── UserCohort.java
│   │   │   │   ├── Export.java
│   │   │   │   └── SearchAnalytic.java
│   │   │   ├── repository/           # Spring Data JPA repositories
│   │   │   │   ├── EventRepository.java
│   │   │   │   ├── SalesMetricRepository.java
│   │   │   │   ├── ProductMetricRepository.java
│   │   │   │   ├── UserMetricRepository.java
│   │   │   │   ├── UserCohortRepository.java
│   │   │   │   ├── SearchAnalyticRepository.java
│   │   │   │   └── ExportRepository.java
│   │   │   └── service/              # Business logic services
│   │   │       ├── EventService.java
│   │   │       ├── SalesAnalyticsService.java
│   │   │       ├── ProductAnalyticsService.java
│   │   │       ├── UserAnalyticsService.java
│   │   │       ├── SearchAnalyticsService.java
│   │   │       ├── DashboardService.java
│   │   │       └── ExportService.java
│   │   └── resources/
│   │       ├── application.yml       # Main application configuration
│   │       ├── application-test.yml  # Test configuration
│   │       └── schema.sql            # Database schema
│   └── test/
│       └── java/com/shopsphere/analytics/
│           └── service/
│               └── EventServiceTest.java

```

## Setup Instructions

### 1. Prerequisites

Install the following:
- **Java 21**: [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- **Maven 3.8+**: [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 13+**: [Download](https://www.postgresql.org/download/)
- **Redis 7+**: [Download](https://redis.io/download)
- **RabbitMQ 3.12+**: [Download](https://www.rabbitmq.com/download.html)

### 2. Database Setup

```sql
-- Create database
CREATE DATABASE analytics_db;

-- Connect to database
\c analytics_db;

-- Tables will be created automatically via schema.sql on app startup
```

### 3. Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/analytics_db
    username: postgres
    password: your_password
  
  rabbitmq:
    host: localhost
    username: guest
    password: guest
  
  redis:
    host: localhost
    port: 6379
```

### 4. Build

```bash
# Navigate to project directory
cd analytics-service

# Clean and build
mvn clean install

# Run tests
mvn test
```

### 5. Run Application

```bash
# Using Maven
mvn spring-boot:run

# Or run JAR
mvn clean package
java -jar target/analytics-service-1.0.0.jar
```

The service will start on **http://localhost:3010**

---

## Architecture Details

### Layered Architecture

```
REST Controllers
        ↓
Business Services
        ↓
Data Repositories
        ↓
Database / Cache / Message Queue
```

### Key Design Patterns

1. **Service Layer Pattern**: Business logic separated from controllers
2. **Repository Pattern**: Data access abstraction
3. **DTO Pattern**: Clean API contracts
4. **Exception Handler Pattern**: Centralized error handling
5. **Caching Strategy**: Redis for performance optimization

---

## Event Flow

### Synchronous Event Ingestion

```
POST /api/analytics/events
      ↓
EventController validates
      ↓
EventService.trackEvent()
      ↓
EventRepository.save()
      ↓
PostgreSQL Database
```

### Asynchronous Event Processing (RabbitMQ)

```
Event sent to RabbitMQ
      ↓
EventService.consumeEvent() listener
      ↓
EventService.trackEvent()
      ↓
PostgreSQL Database
```

---

## Service Responsibilities

### EventService
- Track individual and batch events
- Consume events from RabbitMQ message queue
- Retrieve events by various filters

### SalesAnalyticsService
- Aggregate sales metrics
- Calculate revenue, orders, AOV
- Compare periods with percentage changes
- Cache results in Redis

### ProductAnalyticsService
- Track product-level metrics
- Identify top products by views/sales/revenue
- Calculate conversion rates

### UserAnalyticsService
- Track user behavior and registration
- Calculate DAU, WAU, MAU
- Analyze user retention via cohorts
- Segment users by behavior

### DashboardService
- Aggregate data from all analytics services
- Provide admin and seller dashboards
- Cache dashboard data with TTL
- Allow cache invalidation

### ExportService
- Process export requests asynchronously
- Generate CSV/Excel files
- Track export history
- Handle export status management

---

## Database Schema Overview

### Events Table
Stores raw event data from all applications
- Indexed by: event_type, user_id, timestamp, product_id

### Metrics Tables
Pre-aggregated data for fast queries
- **SalesMetrics**: Daily/weekly/monthly sales data
- **ProductMetrics**: Product performance data
- **UserMetrics**: User behavioral data
- **UserCohorts**: Cohort analysis data
- **SearchAnalytics**: Search query tracking
- **Exports**: Export history tracking

---

## API Authentication

Currently, the event ingestion endpoints are public. For other endpoints:

1. Clients send JWT token in Authorization header:
```
Authorization: Bearer {jwt_token}
```

2. SecurityConfig validates the token (to be implemented with JWT provider)

---

## Caching Strategy

**Redis Cache Keys:**
```
sales:summary:{from_date}:{to_date}         (60 min TTL)
product:top-viewed:{from}:{to}:{limit}      (30 min TTL)
product:top-selling:{from}:{to}:{limit}     (30 min TTL)
user:dau:{date}                             (30 min TTL)
user:mau:{start_month}:{end_month}          (60 min TTL)
dashboard:admin:{date}                      (15 min TTL)
dashboard:seller:{seller_id}:{date}         (15 min TTL)
```

---

## Testing

Run unit tests:
```bash
mvn test
```

Sample test class provided in `EventServiceTest.java`

---

## Monitoring & Logging

### Actuator Endpoints
- Health: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Info: `GET /actuator/info`

### Logging Configuration
configured in `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.shopsphere: DEBUG
```

---

## Common Issues & Solutions

### Cannot connect to PostgreSQL
- Ensure PostgreSQL is running: `pg_isready -h localhost -p 5432`
- Check credentials in application.yml
- Verify database exists

### Redis connection failed
- Ensure Redis is running: `redis-cli ping` (should return PONG)
- Check host and port in application.yml

### RabbitMQ not connecting
- Ensure RabbitMQ is running on port 5672
- Check credentials (default: guest/guest)
- Verify virtual host is "/"

---

## Performance Optimization Tips

1. **Database Indexes**: Already configured in schema.sql for common queries
2. **Caching**: Dashboard and metric data are cached with appropriate TTLs
3. **Async Processing**: Export operations run asynchronously
4. **Batch Ingestion**: Use batch event endpoint for high-volume ingestion
5. **Query Optimization**: Repository queries are optimized with specific field selections

---

## Future Enhancements (Phase 2)

- Real-time WebSocket dashboards
- Machine learning-based forecasting
- A/B testing analytics module
- Distributed tracing with Sleuth
- Metrics export to external systems
- Advanced anomaly detection

---

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Write tests for new functionality
3. Follow existing code style and patterns
4. Submit pull request with description

---

## Support & Contact

For issues or questions, reach out to the Analytics Service team.

---

**Last Updated**: 2024-01-15  
**Version**: 1.0.0 (MVP)  
**Status**: ✅ Ready for Development
