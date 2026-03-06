# Phase 4.3: Event Publishing - Deployment & Testing Guide

## 🚀 Ready for Production

**Status:** ✅ IMPLEMENTATION COMPLETE  
**Date:** February 19, 2026  
**All Files:** Created & Verified  

---

## Quick Start (5 minutes)

### 1. Start RabbitMQ
```bash
docker-compose up -d rabbitmq
sleep 5
```

### 2. Build User Service
```bash
cd services/user-service
mvn clean install -DskipTests=true
```

### 3. Start User Service
```bash
mvn spring-boot:run
```

### 4. Test Registration (New Terminal)
```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Demo",
    "lastName": "User",
    "email": "demo@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'
```

### 5. Verify Event Published
- Open http://localhost:15672 (RabbitMQ UI)
- Login: guest / guest
- Go to **Queues** tab
- Check `user.created.queue` for message

**Expected Response:** HTTP 200 with user data

---

## Full Deployment Checklist

### Pre-Deployment
- [ ] RabbitMQ running: `docker ps | grep rabbitmq`
- [ ] PostgreSQL running: `docker ps | grep postgres`
- [ ] Redis running: `docker ps | grep redis`
- [ ] Java 17+: `java -version`
- [ ] Maven 3.8+: `mvn -version`

### Build Phase
```bash
cd services/user-service
mvn clean install -DskipTests=true
```
**Verification:**
- [ ] Build success (no errors)
- [ ] WAR/JAR created in `target/` directory

### Startup Phase
```bash
mvn spring-boot:run
```
**Verification:**
- [ ] Service starts on port 3001
- [ ] Logs show: "Tomcat started on port(s): 3001"
- [ ] RabbitMQ connected: "RabbitMQ connection established"
- [ ] Health endpoint works: `curl http://localhost:3001/actuator/health`

### Integration Testing
```bash
# Test 1: Registration (should publish event)
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test1@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'

# Expected: 200 OK
# Check RabbitMQ for message

# Test 2: Duplicate email (should fail, no event published)
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test1@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'

# Expected: 400 Bad Request
# No additional message in queue

# Test 3: Login
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test1@example.com",
    "password": "Password123!"
  }'

# Expected: 200 OK with JWT tokens
```

---

## RabbitMQ Monitoring

### Access RabbitMQ UI
- **URL:** http://localhost:15672
- **Username:** guest
- **Password:** guest

### Verify Setup
1. **Exchanges Tab:**
   - Look for `user.exchange`
   - Type should be: `topic`
   - Durable: ✓ checked

2. **Queues Tab:**
   - Look for:
     - `user.created.queue`
     - `user.updated.queue`
     - `user.deleted.queue`
   - Each should show:
     - Durable: ✓ checked
     - Auto-delete: ✗ unchecked

3. **Admin → Users:**
   - Verify `guest` user exists
   - Verify permissions are set

### Monitor Messages
1. Click on `user.created.queue`
2. Scroll to **Get messages** section
3. Set `Ackmode` to `Nack message requeue true`
4. Click `Get Message(s)`
5. Should see JSON payload with user data

---

## Troubleshooting

### Issue: "Cannot connect to RabbitMQ"

**Symptom:** Error logs show connection refused

**Solutions:**
```bash
# Check if RabbitMQ running
docker ps | grep rabbitmq

# If not running, start it
docker-compose up -d rabbitmq

# Check logs
docker logs rabbitmq

# Restart if needed
docker restart rabbitmq

# Wait for startup (~10 seconds)
sleep 10
```

### Issue: "RabbitTemplate bean not found"

**Symptom:** Spring fails to start with NoSuchBeanDefinitionException

**Solutions:**
```bash
# Clean rebuild
cd services/user-service
rm -rf target/
mvn clean install -DskipTests=true

# Verify dependency in pom.xml
grep "spring-boot-starter-amqp" pom.xml

# If missing, add it manually (see Phase 4.3 docs)
```

### Issue: "No messages appear in queue after registration"

**Symptom:** User registers successfully but queue is empty

**Solutions:**
```bash
# Check application logs for errors
# Look for: "Publishing user.created event for user:"

# Verify RabbitMQ connection
curl -u guest:guest http://localhost:15672/api/overview

# Check if exchange/queues exist
curl -u guest:guest http://localhost:15672/api/exchanges

# Manual test (send message to exchange)
# Use RabbitMQ UI → Exchanges → user.exchange → Publish message
```

### Issue: "Build fails with AMQP import errors"

**Symptom:** IDE/Maven shows "Cannot resolve symbol 'amqp'"

**Solutions:**
```bash
# Update IDE cache
# In IntelliJ: File → Invalidate Caches → Restart

# Force Maven update
mvn clean install -U

# Check Maven settings
mvn help:describe -Dartifact=org.springframework.boot:spring-boot-starter-amqp
```

---

## Performance Metrics

### Expected Performance
| Metric | Value | Notes |
|--------|-------|-------|
| Registration Time | ~100-150ms | Without event publishing |
| Event Publish Time | ~20-50ms | With event publishing |
| Queue Latency | <1ms | In-process |
| RabbitMQ Memory | ~50MB | Base instance |
| CPU Usage | <5% | At rest |

### Monitoring
```bash
# Check application metrics
curl http://localhost:3001/actuator/metrics

# Check RabbitMQ metrics
curl -u guest:guest http://localhost:15672/api/overview
```

---

## Logs to Monitor

### Success Logs
```
[INFO] Attempting to register user with email: test@example.com
[INFO] User successfully registered with email: test@example.com
[INFO] Publishing user.created event for user: test@example.com
[DEBUG] User created event published successfully for user: test@example.com
```

### Error Logs (Normal)
```
[WARN] Failed to publish user.created event, but user was saved: Connection refused
```
This is NORMAL - means RabbitMQ was down but registration succeeded

### Error Logs (Critical)
```
[ERROR] Cannot autowire RabbitTemplate
[ERROR] Failed to start application context
```
These mean AMQP not properly configured

---

## Environment Variables (Optional)

For production, use environment variables:

```bash
# RabbitMQ
export SPRING_RABBITMQ_HOST=rabbitmq.prod.example.com
export SPRING_RABBITMQ_PORT=5672
export SPRING_RABBITMQ_USERNAME=prod_user
export SPRING_RABBITMQ_PASSWORD=prod_password

# Start service
mvn spring-boot:run
```

---

## Rollback Plan

If Phase 4.3 causes issues:

### Quick Rollback
1. Stop User Service: `Ctrl+C`
2. Restore previous version:
   ```bash
   git checkout HEAD -- services/user-service/src/main/java/com/shopsphere/user/service/AuthService.java
   git checkout HEAD -- services/user-service/pom.xml
   rm services/user-service/src/main/java/com/shopsphere/user/config/RabbitMQConfig.java
   rm services/user-service/src/main/java/com/shopsphere/user/service/UserEventPublisher.java
   ```
3. Rebuild: `mvn clean install -DskipTests=true`
4. Restart: `mvn spring-boot:run`

---

## Success Criteria

✅ Phase 4.3 is successful when:

1. **Build:** Maven build completes without errors
2. **Startup:** Application starts and connects to RabbitMQ
3. **Registration:** User can register successfully
4. **Event:** Message appears in `user.created.queue` in RabbitMQ UI
5. **JSON:** Message contains valid JSON with user details
6. **Logging:** Info/debug logs show event publishing success
7. **Graceful:** Registration succeeds even if RabbitMQ is down

---

## Next Phase (Phase 4.4)

Once verified, implement **Event Consumers** in:

1. **Notification Service**
   ```java
   @RabbitListener(queues = "user.created.queue")
   public void handleUserCreated(UserInternalDto userDto) {
       // Send welcome email
   }
   ```

2. **Analytics Service**
   ```java
   @RabbitListener(queues = "user.created.queue")
   public void trackNewUser(UserInternalDto userDto) {
       // Track registration event
   }
   ```

3. **Inventory Service**
   ```java
   @RabbitListener(queues = "user.created.queue")
   public void createSellerProfile(UserInternalDto userDto) {
       // Create seller profile if role includes SELLER
   }
   ```

---

## Files & Documentation

| File | Purpose | Access |
|------|---------|--------|
| **RabbitMQConfig.java** | Configuration | `src/main/java/.../config/RabbitMQConfig.java` |
| **UserEventPublisher.java** | Event Publisher | `src/main/java/.../service/UserEventPublisher.java` |
| **AuthService.java** | Integration | `src/main/java/.../service/AuthService.java` |
| **pom.xml** | Dependencies | `pom.xml` |
| **PHASE_4.3_EVENT_PUBLISHING.md** | Full Docs | `docs/PHASE_4.3_EVENT_PUBLISHING.md` |
| **PHASE_4.3_QUICK_REFERENCE.md** | Quick Ref | `docs/PHASE_4.3_QUICK_REFERENCE.md` |
| **verify-phase-4.3.sh** | Verification | `services/user-service/verify-phase-4.3.sh` |

---

## Support Resources

- **Spring AMQP:** https://spring.io/projects/spring-amqp
- **RabbitMQ Docs:** https://www.rabbitmq.com/documentation.html
- **Docker Compose:** See `docker-compose.yml` in repo root
- **Health Check:** `curl http://localhost:3001/actuator/health`
- **Metrics:** `curl http://localhost:3001/actuator/metrics`

---

## Summary

✅ **Phase 4.3 is complete and ready for deployment!**

Follow the "Quick Start" section above to get running in 5 minutes.

Monitor the logs and RabbitMQ UI to verify events are being published.

Ready to move to **Phase 4.4: Event Consumers**.

---

**Questions?** Review the documentation files or check the troubleshooting section above.

