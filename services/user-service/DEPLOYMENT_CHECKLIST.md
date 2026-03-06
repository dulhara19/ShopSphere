# Phase 4.3: Final Deployment Checklist

## ✅ PRE-DEPLOYMENT VERIFICATION

### Environment Setup
- [ ] Java 17+ installed: `java -version`
- [ ] Maven 3.8+ installed: `mvn -version`
- [ ] Docker installed: `docker --version`
- [ ] Git installed: `git --version`
- [ ] PostgreSQL running: `docker ps | grep postgres`
- [ ] Redis running: `docker ps | grep redis`
- [ ] RabbitMQ ready to start: `docker-compose config | grep rabbitmq`

### Code Review
- [ ] All files created (7 files)
- [ ] All files modified (2 files)
- [ ] No merge conflicts
- [ ] No uncommitted changes in sensitive files
- [ ] pom.xml has correct AMQP dependency
- [ ] RabbitMQConfig.java has all beans
- [ ] UserEventPublisher.java has 3 methods
- [ ] AuthService.java has event publishing code

### Build Verification
- [ ] Clean build succeeds: `mvn clean`
- [ ] Dependencies resolve: `mvn dependency:resolve`
- [ ] No compilation errors: `mvn compile`
- [ ] No test failures: `mvn test` (optional)
- [ ] JAR/WAR builds: `mvn package`

---

## ✅ DEPLOYMENT STEPS

### Step 1: Start Infrastructure
```bash
# Start RabbitMQ
docker-compose up -d rabbitmq
sleep 10

# Verify RabbitMQ is running
docker ps | grep rabbitmq
curl -s -u guest:guest http://localhost:15672/api/overview
```

**Checklist:**
- [ ] RabbitMQ container started
- [ ] Management API responsive (curl success)
- [ ] No error messages in docker logs

### Step 2: Build Application
```bash
cd services/user-service

# Clean build
mvn clean install -DskipTests=true

# Verify build
ls -la target/*.jar
```

**Checklist:**
- [ ] Build completes without errors
- [ ] JAR file created in target/
- [ ] No AMQP import errors
- [ ] Classpath includes spring-boot-starter-amqp

### Step 3: Start Application
```bash
mvn spring-boot:run
# OR
java -jar target/user-service-1.0.0-SNAPSHOT.jar
```

**Expected Logs (watch for these):**
```
[INFO] Tomcat started on port(s): 3001
[INFO] RabbitMQ connection established
[INFO] Spring application started successfully
```

**Checklist:**
- [ ] Application starts on port 3001
- [ ] No connection errors
- [ ] No AMQP errors
- [ ] Health endpoint works: `curl http://localhost:3001/actuator/health`

---

## ✅ FUNCTIONAL TESTING

### Test 1: Basic Registration
```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test1@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'
```

**Checklist:**
- [ ] HTTP 200 response
- [ ] User data returned
- [ ] User ID is UUID
- [ ] Roles contain CUSTOMER

### Test 2: Event in Queue
```bash
# Check RabbitMQ UI
# URL: http://localhost:15672
# Username: guest
# Password: guest
```

**In RabbitMQ UI:**
- [ ] Queues tab opens
- [ ] user.created.queue visible
- [ ] Queue shows 1+ messages
- [ ] Click to view message content

### Test 3: Message Structure
**Expected JSON in RabbitMQ:**
```json
{
  "id": "...",
  "email": "test1@example.com",
  "firstName": "Test",
  "lastName": "User",
  "roles": ["CUSTOMER"]
}
```

**Checklist:**
- [ ] JSON is valid
- [ ] Contains id (UUID)
- [ ] Contains email
- [ ] Contains firstName
- [ ] Contains lastName
- [ ] Contains roles array

### Test 4: Logging
```bash
# Check application logs (should see):
tail -f logs/user-service.log | grep "Publishing user.created"
```

**Expected Log Lines:**
```
[INFO] Publishing user.created event for user: test1@example.com
[DEBUG] User created event published successfully for user: test1@example.com
```

**Checklist:**
- [ ] Info log shows publishing started
- [ ] Debug log shows success
- [ ] No error logs
- [ ] Timestamps make sense

---

## ✅ EDGE CASE TESTING

### Test 5: Duplicate Email
```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test1@example.com",
    "password": "Password123!",
    "roles": ["CUSTOMER"]
  }'
```

**Expected Result:**
- [ ] HTTP 400 Bad Request
- [ ] Error message about duplicate email
- [ ] No new message in RabbitMQ queue

### Test 6: Invalid Password
```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test3@example.com",
    "password": "123",
    "roles": ["CUSTOMER"]
  }'
```

**Expected Result:**
- [ ] HTTP 400 Bad Request
- [ ] Validation error
- [ ] No message in queue

### Test 7: RabbitMQ Down (Advanced)
```bash
# Stop RabbitMQ
docker-compose down

# Try registration
curl -X POST http://localhost:3001/api/auth/register ...

# Expected: 200 OK (registration succeeds)
# Error logged but registration succeeds
```

**Expected Result:**
- [ ] HTTP 200 OK (registration succeeds)
- [ ] User saved to database
- [ ] Error logged in application logs
- [ ] No error thrown to user

---

## ✅ PERFORMANCE TESTING

### Test 8: Response Time
```bash
time curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**Expected:**
- [ ] Real: ~100-200ms
- [ ] User: ~50-100ms
- [ ] Sys: ~20-50ms

### Test 9: RabbitMQ Message Time
- [ ] Message appears in queue within 1 second
- [ ] Queue processing is immediate
- [ ] No bottlenecks observed

---

## ✅ MONITORING & METRICS

### Check Health Endpoints
```bash
# Application health
curl http://localhost:3001/actuator/health

# Metrics
curl http://localhost:3001/actuator/metrics

# RabbitMQ health
curl -u guest:guest http://localhost:15672/api/overview
```

**Checklist:**
- [ ] Application health is UP
- [ ] RabbitMQ shows in metrics
- [ ] RabbitMQ API responds
- [ ] Memory usage reasonable (~50-100MB)

### Check Logs
```bash
# Tail logs for 30 seconds
tail -f logs/user-service.log | head -100

# Check for errors
grep -i error logs/user-service.log | tail -20

# Check for warnings
grep -i warn logs/user-service.log | tail -20
```

**Checklist:**
- [ ] No ERROR level logs (expected)
- [ ] WARN level logs acceptable
- [ ] INFO logs show activity
- [ ] DEBUG logs show detail

---

## ✅ ROLLBACK PLAN

### If Issues Occur (Rollback)
```bash
# Stop application
Ctrl+C

# Check what's wrong
docker logs rabbitmq
tail -f logs/user-service.log

# Rollback code (if needed)
git status
git diff pom.xml
git diff services/user-service/src/main/java/com/shopsphere/user/service/AuthService.java

# If rollback needed
git checkout HEAD -- pom.xml
git checkout HEAD -- services/user-service/src/main/java/com/shopsphere/user/service/AuthService.java

# Restart
mvn clean install -DskipTests=true
mvn spring-boot:run
```

**Checklist:**
- [ ] Changes understood before rollback
- [ ] Can identify root cause
- [ ] Know how to roll back safely
- [ ] Have git history to reference

---

## ✅ SIGN-OFF CHECKLIST

### Final Verification
- [ ] All pre-deployment checks passed
- [ ] Application builds successfully
- [ ] Application starts without errors
- [ ] Registration works (Test 1 passed)
- [ ] Events appear in queue (Test 2 passed)
- [ ] Event structure is correct (Test 3 passed)
- [ ] Logging works (Test 4 passed)
- [ ] Error cases handled (Tests 5-6 passed)
- [ ] Performance acceptable (Test 8-9 passed)
- [ ] Health endpoints working
- [ ] Documentation reviewed
- [ ] Rollback plan understood

### Ready for Production?
- [ ] Yes, all checks passed
- [ ] No, see issues below

---

## 📋 ISSUES FOUND (if any)

### Issue #1: ________________
**Symptom:** ________________  
**Root Cause:** ________________  
**Fix:** ________________  
**Status:** [ ] Fixed [ ] Escalated

### Issue #2: ________________
**Symptom:** ________________  
**Root Cause:** ________________  
**Fix:** ________________  
**Status:** [ ] Fixed [ ] Escalated

---

## 👤 SIGN-OFF

**Tested By:** _______________________  
**Date:** _______________________  
**Time:** _______________________  
**Status:** [ ] PASS [ ] FAIL

**Approver:** _______________________  
**Date:** _______________________  

**Notes:**
```
_________________________________________________________________

_________________________________________________________________

_________________________________________________________________
```

---

## 📞 CONTACTS

| Role | Name | Contact |
|------|------|---------|
| DevOps | | |
| QA | | |
| Project Manager | | |
| On-Call Support | | |

---

## 📚 DOCUMENTATION REFERENCES

- Quick Start: docs/PHASE_4.3_QUICK_REFERENCE.md
- Full Guide: docs/PHASE_4.3_EVENT_PUBLISHING.md
- Deployment: docs/PHASE_4.3_DEPLOYMENT_GUIDE.md
- Changes: docs/PHASE_4.3_CHANGES_REFERENCE.md

---

## 🚀 POST-DEPLOYMENT

After successful deployment:

1. **Monitor for 24 hours**
   - [ ] Check logs every hour
   - [ ] Monitor error rates
   - [ ] Monitor queue lengths

2. **Document Lessons Learned**
   - [ ] What went well
   - [ ] What could be improved
   - [ ] Any unexpected issues

3. **Proceed to Phase 4.4**
   - [ ] Start implementation of event consumers
   - [ ] Notification Service first
   - [ ] Then Analytics Service
   - [ ] Finally Inventory Service

---

**Deployment Checklist Version:** 1.0  
**Last Updated:** February 19, 2026  
**Status:** Ready for Deployment

