# Phase 4.3: Event Publishing - Comprehensive Testing Guide

## 🎯 TESTING PHASE 4.3 (Event Publishing)

**Status:** Ready to test  
**Prerequisites:** RabbitMQ running on Docker  
**Service Port:** 3001  

---

## ✅ QUICK START (5 minutes)

### 1. Verify RabbitMQ is Running
```bash
# Check if RabbitMQ container is running
docker ps | grep rabbitmq

# If not running, start it:
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 2. Verify Service is Running
```bash
# Check if user-service is running on port 3001
curl http://localhost:3001/actuator/health
```

### 3. Register a User and Monitor Events
```bash
# Terminal 1: Register user
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Event",
    "lastName": "Test",
    "email": "event@test.com",
    "password": "Pass@123"
  }'

# Terminal 2: Open RabbitMQ dashboard
# URL: http://localhost:15672
# Username: guest
# Password: guest
# Navigate to: Queues → user.created.queue
# Verify: Message count increased
```

---

## 📊 DETAILED TEST SCENARIOS

### Test 1: User Registration Event Publishing
**Objective:** Verify that user registration publishes an event to RabbitMQ

#### Steps:
1. **Note current queue status**
   ```bash
   # Open RabbitMQ Dashboard
   URL: http://localhost:15672
   Go to: Queues → user.created.queue
   Note: Current message count
   ```

2. **Register a new user**
   ```bash
   curl -X POST http://localhost:3001/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Alice",
       "lastName": "Johnson",
       "email": "alice@example.com",
       "password": "SecurePass@123"
     }'
   ```

3. **Expected Response**
   ```json
   {
     "id": "uuid-here",
     "email": "alice@example.com",
     "firstName": "Alice",
     "lastName": "Johnson",
     "username": "alice@example.com",
     "roles": ["CUSTOMER"],
     "isEnabled": true,
     "createdAt": "2026-02-21T...",
     "message": "User registered successfully"
   }
   ```

4. **Verify event was published**
   - Check RabbitMQ Dashboard → Queues → user.created.queue
   - **Expected:** Message count increased by 1
   - **Result:** ✅ PASS / ❌ FAIL

#### Expected Result:
✅ User registered successfully  
✅ Event published to user.created.queue  
✅ Message contains: id, email, firstName, lastName, roles  

---

### Test 2: Event Message Format Verification
**Objective:** Verify the event message is in correct JSON format

#### Steps:
1. **Register user**
   ```bash
   curl -X POST http://localhost:3001/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Bob",
       "lastName": "Smith",
       "email": "bob@example.com",
       "password": "SecurePass@123"
     }'
   ```

2. **View message in RabbitMQ Dashboard**
   - Go to: Queues → user.created.queue → Get Messages
   - **Expected payload:**
   ```json
   {
     "id": "uuid-value",
     "username": "bob@example.com",
     "email": "bob@example.com",
     "firstName": "Bob",
     "lastName": "Smith",
     "phone": null,
     "roles": ["CUSTOMER"]
   }
   ```

3. **Verify fields**
   - ✅ id (UUID)
   - ✅ email
   - ✅ firstName
   - ✅ lastName
   - ✅ roles (List<String>)

#### Expected Result:
✅ Message is valid JSON  
✅ All required fields present  
✅ Data types are correct  

---

### Test 3: Multiple User Registration (Load Test)
**Objective:** Verify system handles multiple registrations with event publishing

#### Steps:
1. **Register 5 users quickly**
   ```bash
   for i in {1..5}; do
     curl -X POST http://localhost:3001/api/auth/register \
       -H "Content-Type: application/json" \
       -d "{
         \"firstName\": \"User$i\",
         \"lastName\": \"Test\",
         \"email\": \"user$i@test.com\",
         \"password\": \"Pass@123\"
       }"
     echo "User $i registered"
   done
   ```

2. **Check queue message count**
   - RabbitMQ Dashboard → Queues → user.created.queue
   - **Expected:** Message count increased by 5

3. **Verify message ordering**
   - Get all messages from queue
   - Verify timestamps are in order

#### Expected Result:
✅ All 5 users registered  
✅ All 5 events published  
✅ Queue has exactly 5 new messages  

---

### Test 4: Event Publishing Error Resilience
**Objective:** Verify registration succeeds even if event publishing fails

#### Steps:
1. **Stop RabbitMQ temporarily**
   ```bash
   docker stop rabbitmq
   ```

2. **Attempt to register user**
   ```bash
   curl -X POST http://localhost:3001/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Resilient",
       "lastName": "User",
       "email": "resilient@test.com",
       "password": "Pass@123"
     }'
   ```

3. **Expected Response**
   - ✅ HTTP 200 OK (registration succeeds)
   - ✅ User is in database
   - ⚠️ Event not published (RabbitMQ down)

4. **Restart RabbitMQ**
   ```bash
   docker start rabbitmq
   ```

5. **Verify user exists in database**
   ```bash
   curl -X POST http://localhost:3001/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "email": "resilient@test.com",
       "password": "Pass@123"
     }'
   ```

#### Expected Result:
✅ Registration succeeds despite RabbitMQ unavailable  
✅ User is saved to database  
✅ Service logs show event publishing error  
✅ No exception thrown  

---

### Test 5: Event Consumption by Subscriber
**Objective:** Verify events can be consumed by other services

#### Prerequisites:
- A listener service (Notification Service, Analytics Service, etc.)

#### Steps:
1. **Create a test listener** (optional - for verification)
   ```bash
   # In another terminal, create a simple listener:
   # This is pseudo-code - actual implementation depends on service
   
   @RabbitListener(queues = "user.created.queue")
   public void handleUserCreatedEvent(UserInternalDto event) {
       System.out.println("Received event: " + event.getEmail());
       // Process event...
   }
   ```

2. **Register a user**
   ```bash
   curl -X POST http://localhost:3001/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Listener",
       "lastName": "Test",
       "email": "listener@test.com",
       "password": "Pass@123"
     }'
   ```

3. **Verify event is consumed**
   - Listener should log received event
   - Message disappears from queue (if listener is active)

#### Expected Result:
✅ Event received by listener  
✅ Message removed from queue  
✅ Listener processed event successfully  

---

### Test 6: Using test-register.http File
**Objective:** Run predefined tests for event publishing

#### File: PHASE_4.3_DATA_LOOKUP.http

```http
### Test 1: Register a new user
POST http://localhost:3001/api/auth/register
Content-Type: application/json

{
    "firstName": "HTTP",
    "lastName": "Test",
    "email": "http@test.com",
    "password": "Pass@123"
}

### Test 2: Check RabbitMQ Dashboard (manual)
# URL: http://localhost:15672
# Go to: Queues → user.created.queue
# Verify message is present

### Test 3: Register with existing email (should fail but not publish event)
POST http://localhost:3001/api/auth/register
Content-Type: application/json

{
    "firstName": "Duplicate",
    "lastName": "Test",
    "email": "http@test.com",
    "password": "Pass@123"
}

### Expected Response:
# HTTP 409 Conflict
# User already exists
# No event published
```

---

## 🔍 MONITORING & DEBUGGING

### Check Service Logs
```bash
# View user-service logs (if running with Maven)
# Look for messages like:
# "Publishing user.created event for user: alice@example.com"
# "User created event published successfully"

# In Docker container:
docker logs -f user-service-container
```

### RabbitMQ Dashboard Checks
```
1. Exchanges:
   - Navigate to: Exchanges
   - Verify: "user.exchange" exists
   - Type: Topic
   - Status: Running

2. Queues:
   - Navigate to: Queues
   - Verify all 4 queues:
     ✓ user.created.queue
     ✓ user.updated.queue
     ✓ user.deleted.queue
     ✓ user.password.reset.queue

3. Connections:
   - Navigate to: Connections
   - Verify: User service connected
   - Status: Open

4. Channels:
   - Navigate to: Channels
   - Verify: RabbitTemplate channel open
   - Status: Running
```

### Check Message Content
```
RabbitMQ Dashboard → Queues → user.created.queue → Get Messages

Expected JSON:
{
  "id": "7154f9b2-f948-4944-9728-afd4aebe7e3e",
  "username": "alice@example.com",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Johnson",
  "phone": null,
  "roles": ["CUSTOMER"]
}
```

---

## ✅ COMPREHENSIVE TEST CHECKLIST

### Functional Tests
- [ ] User registration publishes event
- [ ] Event contains correct fields
- [ ] Event is in JSON format
- [ ] Multiple registrations publish multiple events
- [ ] Event message survives RabbitMQ restart

### Resilience Tests
- [ ] Registration succeeds if RabbitMQ is down
- [ ] Service logs error but doesn't fail
- [ ] User is saved to database despite RabbitMQ issues
- [ ] No exception propagates to client

### Performance Tests
- [ ] 5 registrations complete within reasonable time
- [ ] Each event published within acceptable latency
- [ ] No memory leaks from repeated event publishing

### Integration Tests
- [ ] Events can be consumed by other services
- [ ] Message format is compatible with consumers
- [ ] Proper serialization/deserialization

### Security Tests
- [ ] No sensitive data in event messages
- [ ] Password hash not included in event
- [ ] Only user metadata is shared

---

## 📋 TEST EXECUTION SUMMARY

### Quick Test (5 min)
```bash
# 1. Ensure RabbitMQ running
docker ps | grep rabbitmq

# 2. Start user-service
mvn spring-boot:run

# 3. Register user
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@test.com","password":"Pass@123"}'

# 4. Check RabbitMQ Dashboard
# http://localhost:15672
# Verify message in user.created.queue
```

### Full Test Suite (30 min)
1. Run all 6 test scenarios above
2. Verify each expected result
3. Check logs for any errors
4. Document any issues

### Production Ready Check
- ✅ All tests pass
- ✅ No errors in logs
- ✅ RabbitMQ queues operational
- ✅ Events properly serialized
- ✅ Error handling working

---

## 🎊 SUCCESS INDICATORS

✅ **Phase 4.3 is working correctly when:**

1. User registers successfully
2. Response includes user details
3. RabbitMQ dashboard shows new message
4. Message is in valid JSON format
5. All fields are present and correct
6. Multiple registrations work properly
7. Registration succeeds even if RabbitMQ down
8. Events can be consumed by subscribers
9. No exceptions in application logs
10. System is responsive and performant

---

## 📞 TROUBLESHOOTING

### Issue: No message in queue after registration
**Possible Causes:**
1. RabbitMQ not running
2. Connection failed
3. Event publishing caught exception silently

**Solution:**
- Check service logs for error messages
- Verify RabbitMQ is running: `docker ps`
- Check RabbitMQ logs: `docker logs rabbitmq`
- Verify application.yml has correct RabbitMQ config

### Issue: Message format incorrect
**Possible Causes:**
1. Jackson2JsonMessageConverter not configured
2. UserInternalDto missing fields

**Solution:**
- Verify RabbitMQConfig has messageConverter() bean
- Check UserInternalDto has all fields
- Restart service

### Issue: Registration fails with RabbitMQ error
**Possible Causes:**
1. Error handling not working
2. Exception propagating to client

**Solution:**
- Verify try-catch in AuthService.registerUser()
- Check logs for full error message
- Restart RabbitMQ

---

## 🎓 NEXT STEPS

After Phase 4.3 verification:

### Phase 4.4: Add Event Consumers
- [ ] Notification Service listens to user.created
- [ ] Analytics Service processes events
- [ ] Recommendation Service initializes on creation

### Phase 5: Advanced Event Publishing
- [ ] Add Dead Letter Queue for failed events
- [ ] Implement event replay mechanism
- [ ] Add event audit logging

---

**Phase 4.3 Testing Guide - COMPLETE**

**Ready to execute all test scenarios.**

Date: February 21, 2026

