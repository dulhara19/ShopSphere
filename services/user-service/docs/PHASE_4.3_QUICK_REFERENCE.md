# Phase 4.3: Quick Reference Guide

## What Was Implemented

### 1. RabbitMQ Dependency
Added to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

### 2. RabbitMQ Configuration
File: `com.shopsphere.user.config.RabbitMQConfig`

**Exchange:** `user.exchange` (TopicExchange)
**Queues:**
- `user.created.queue` → routing key: `user.created`
- `user.updated.queue` → routing key: `user.updated`
- `user.deleted.queue` → routing key: `user.deleted`

**Message Converter:** Jackson2JsonMessageConverter (JSON serialization)

### 3. Event Publisher Service
File: `com.shopsphere.user.service.UserEventPublisher`

**Public Methods:**
- `publishUserCreatedEvent(UserInternalDto userDto)` - sends to user.created.queue
- `publishUserUpdatedEvent(UserInternalDto userDto)` - sends to user.updated.queue
- `publishUserDeletedEvent(UserInternalDto userDto)` - sends to user.deleted.queue

### 4. Registration Integration
Updated: `com.shopsphere.user.service.AuthService`

**registerUser() now:**
1. Saves user to database
2. Creates UserInternalDto from saved user
3. Publishes user.created event via UserEventPublisher
4. Logs any event publishing failures but doesn't fail registration

---

## Message Format (JSON)

```json
{
  "id": "uuid-string",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["CUSTOMER", "SELLER"]
}
```

---

## How to Test

### 1. Start RabbitMQ
```bash
docker-compose up -d rabbitmq
# Wait for container to start (check: docker ps)
```

### 2. Start User Service
```bash
cd services/user-service
mvn spring-boot:run
```

### 3. Register a New User
```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'
```

### 4. Check RabbitMQ UI
- Navigate to http://localhost:15672
- Username: guest | Password: guest
- Go to **Queues** tab
- Look for `user.created.queue`
- Should see 1 message

---

## Environment Check

```bash
# 1. Check Maven dependency (in pom.xml)
grep "spring-boot-starter-amqp" services/user-service/pom.xml

# 2. Check RabbitMQ running
docker ps | grep rabbitmq

# 3. Check RabbitMQ connectivity
curl -u guest:guest http://localhost:15672/api/overview

# 4. Verify files created
ls -la services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java
ls -la services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java
```

---

## Troubleshooting

### Issue: "Cannot connect to RabbitMQ"
**Solution:** Ensure RabbitMQ is running
```bash
docker-compose ps
docker-compose logs rabbitmq
# If not running: docker-compose up -d rabbitmq
```

### Issue: "RabbitTemplate bean not found"
**Solution:** Rebuild the project to resolve dependencies
```bash
cd services/user-service
mvn clean install -DskipTests
```

### Issue: No message appears in queue
**Solution:** Check application logs
```bash
# Look for: "Publishing user.created event for user: test@example.com"
# And: "User created event published successfully for user: test@example.com"
```

### Issue: JSON serialization errors
**Solution:** Ensure UserInternalDto has Lombok annotations
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
```

---

## Code Examples

### Publishing a Custom Event (if needed)
```java
// In any service that injects UserEventPublisher
UserInternalDto userDto = UserInternalDto.builder()
    .id(UUID.randomUUID())
    .email("user@example.com")
    .firstName("John")
    .lastName("Doe")
    .roles(List.of("CUSTOMER"))
    .build();

userEventPublisher.publishUserCreatedEvent(userDto);
```

### Consuming Events in Another Service (next phase)
```java
@RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
public void handleUserCreated(UserInternalDto userDto) {
    log.info("Received user.created event for: {}", userDto.getEmail());
    // Process the event (send email, create profile, etc.)
}
```

---

## Configuration Reference (application.yml)

Current RabbitMQ configuration:
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 3s
          max-attempts: 3
```

---

## Files Modified/Created

| File | Status | Description |
|------|--------|-------------|
| pom.xml | Modified | Added spring-boot-starter-amqp dependency |
| RabbitMQConfig.java | Created | RabbitMQ exchange, queues, bindings, message converter |
| UserEventPublisher.java | Created | Event publishing service with 3 event types |
| AuthService.java | Modified | Integrated user.created event publishing in registerUser() |
| PHASE_4.3_EVENT_PUBLISHING.md | Created | Detailed implementation documentation |

---

## Next Steps

1. **Build & Test:** Run `mvn clean install -DskipTests` to resolve all dependencies
2. **Start Services:** Run RabbitMQ and User Service
3. **Test Event Flow:** Register a new user and verify message in RabbitMQ queue
4. **Create Consumers:** Implement event consumers in Notification/Analytics/Inventory services (Phase 4.4)

---

## Support & Documentation

- **RabbitMQ Management:** http://localhost:15672 (guest/guest)
- **Spring AMQP Docs:** https://spring.io/projects/spring-amqp
- **Full Details:** See PHASE_4.3_EVENT_PUBLISHING.md

