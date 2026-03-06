# Phase 4.2: Data Lookup - Quick Reference Guide

## 📌 Overview
Phase 4.2 enables other microservices to retrieve user information by ID without authentication for inter-service communication.

## 🔧 What Was Implemented

### 1️⃣ UserInternalDto (Enhanced)
**Location:** `src/main/java/com/shopsphere/user/dto/UserInternalDto.java`

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInternalDto {
    private UUID id;              // ← User unique identifier
    private String username;      // ← NEW: Username
    private String email;         // ← Email address
    private String firstName;     // ← First name
    private String lastName;      // ← Last name
    private String phone;         // ← NEW: Phone number
    private List<String> roles;   // ← User roles
}
```

### 2️⃣ InternalUserController (New)
**Location:** `src/main/java/com/shopsphere/user/controller/internal/InternalUserController.java`

```java
@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserInternalDto> getInternalUser(@PathVariable UUID id)
    // Returns: 200 OK with UserInternalDto
    // Returns: 404 Not Found if user doesn't exist
}
```

### 3️⃣ UserServiceImpl (Updated)
**Location:** `src/main/java/com/shopsphere/user/service/impl/UserServiceImpl.java`

**Method:** `getInternalUserById(UUID userId)`
- Finds user by ID in database
- Maps to UserInternalDto with username and phone
- Throws 404 if not found
- Transactional read-only for performance

### 4️⃣ SecurityConfig (Verified)
**Location:** `src/main/java/com/shopsphere/user/config/SecurityConfig.java`

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/internal/**").permitAll()  // ✅ Permits all /internal endpoints
    .anyRequest().authenticated()
)
```

## 🚀 API Endpoint

### GET /internal/users/{id}

**Purpose:** Retrieve user details for internal service-to-service communication

**Path Parameter:**
- `id` (UUID): User's unique identifier

**Headers:**
- None required (no authentication)
- Optional: `X-Service-Name` for identifying calling service

**Response - Success (200 OK):**
```json
{
  "id": "7154f9b2-f948-4944-9728-afd4aebe7e3e",
  "username": "saman",
  "email": "saman@example.com",
  "firstName": "Saman",
  "lastName": "Perera",
  "phone": "0771234567",
  "roles": ["ADMIN"]
}
```

**Response - Not Found (404):**
```json
{
  "timestamp": "2026-02-21T...",
  "status": 404,
  "error": "Not Found",
  "message": "User not found: 00000000-0000-0000-0000-000000000000"
}
```

## 💻 Usage Examples

### cURL
```bash
# Fetch user by ID
curl -X GET http://localhost:3001/internal/users/7154f9b2-f948-4944-9728-afd4aebe7e3e

# With service name header (optional)
curl -X GET http://localhost:3001/internal/users/7154f9b2-f948-4944-9728-afd4aebe7e3e \
  -H "X-Service-Name: order-service"
```

### Java (RestTemplate)
```java
@Autowired
private RestTemplate restTemplate;

// Fetch user
UserInternalDto user = restTemplate.getForObject(
    "http://user-service:3001/internal/users/{id}",
    UserInternalDto.class,
    userId
);

// Use user data
String email = user.getEmail();
String phone = user.getPhone();
List<String> roles = user.getRoles();
```

### Java (WebClient - Reactive)
```java
@Autowired
private WebClient.Builder webClientBuilder;

UserInternalDto user = webClientBuilder.build()
    .get()
    .uri("http://user-service:3001/internal/users/{id}", userId)
    .retrieve()
    .bodyToMono(UserInternalDto.class)
    .block();
```

### Python (Requests)
```python
import requests

response = requests.get(
    'http://localhost:3001/internal/users/7154f9b2-f948-4944-9728-afd4aebe7e3e'
)

if response.status_code == 200:
    user_data = response.json()
    print(f"Username: {user_data['username']}")
    print(f"Email: {user_data['email']}")
    print(f"Phone: {user_data['phone']}")
elif response.status_code == 404:
    print("User not found")
```

## 📋 Test Cases

| Test Case | Method | URL | Expected Status | Notes |
|-----------|--------|-----|-----------------|-------|
| Valid user | GET | `/internal/users/{valid-uuid}` | 200 | Returns full UserInternalDto |
| Non-existent user | GET | `/internal/users/{invalid-uuid}` | 404 | User not found message |
| Invalid UUID | GET | `/internal/users/not-a-uuid` | 400 | Validation error |
| No auth needed | GET | `/internal/users/{id}` (no bearer) | 200 | Works without JWT |
| Service header | GET | `/internal/users/{id}` (with X-Service-Name) | 200 | Header optional |

## 🔐 Security

✅ **No Authentication Required**
- Internal endpoints for service-to-service communication
- Within secure microservice network

✅ **No Sensitive Data Exposed**
- Password never returned
- Only public user profile data

✅ **UUID Validation**
- Spring validates UUID format
- Returns 400 for invalid UUIDs

✅ **Audit Logging**
- All requests logged with user ID
- Service name logged if provided

## 🎯 Which Services Use This?

| Service | Use Case |
|---------|----------|
| Order Service | Fetch customer/seller details when creating/retrieving orders |
| Product Service | Fetch seller info when displaying products |
| Inventory Service | Validate user ownership of inventory |
| Notification Service | Get email/phone for sending notifications |
| Analytics Service | Aggregate user metadata for reports |
| Recommendation Service | Get user profile for personalization |

## 📝 Request/Response Flow

```
┌─────────────────────────────────────────────────────────────┐
│                     Order Service                           │
│  (Needs user info for order display)                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ GET /internal/users/{userId}
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     User Service                            │
│  (Provides user data via internal endpoint)                 │
│                                                              │
│  1. Find user by ID in database                             │
│  2. Map to UserInternalDto                                  │
│  3. Return JSON response                                    │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ 200 OK {UserInternalDto}
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     Order Service                           │
│  (Receives user data and uses it)                           │
│                                                              │
│  - Display customer name                                   │
│  - Send notification to customer phone                     │
│  - Check if user is seller for product listings            │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Troubleshooting

### Issue: 404 Not Found
**Solution:** Verify the user ID exists in the database
```sql
SELECT id, username, email FROM users WHERE id = '7154f9b2-f948-4944-9728-afd4aebe7e3e';
```

### Issue: 400 Bad Request
**Solution:** Ensure UUID format is valid
```
Valid:   7154f9b2-f948-4944-9728-afd4aebe7e3e
Invalid: 7154f9b2f948494497 28afd4aebe7e3e (spaces or wrong format)
```

### Issue: Connection Refused
**Solution:** Ensure user-service is running on port 3001
```bash
# Check if service is running
curl http://localhost:3001/actuator/health
```

### Issue: Slow Response
**Solution:** This uses database index - should be fast (~20-50ms)
- Check database connection
- Check database load
- Consider caching in Phase 4.4

## 📚 Related Files

| File | Purpose |
|------|---------|
| PHASE_4.2_DATA_LOOKUP.md | Detailed implementation guide |
| PHASE_4.2_DATA_LOOKUP.http | 25+ test cases |
| PHASE_4.2_IMPLEMENTATION_CHECKLIST.md | Complete checklist & verification |
| UserInternalDto.java | DTO with new fields |
| InternalUserController.java | Controller with GET endpoint |
| UserServiceImpl.java | Service logic for data retrieval |

## 🔄 Workflow Summary

```
1. Other service needs user data
   ↓
2. Calls GET /internal/users/{id}
   ↓
3. InternalUserController.getInternalUser() invoked
   ↓
4. UserServiceImpl.getInternalUserById() called
   ↓
5. Database lookup by user ID
   ↓
6. User found? → Map to UserInternalDto → Return 200
   User not found? → Return 404
   ↓
7. Other service receives response
   ↓
8. Uses user data (email, phone, roles, etc.)
```

## ✅ Verification Checklist

- [ ] User service running on port 3001
- [ ] PostgreSQL database connected
- [ ] Test user exists in database
- [ ] GET /internal/users/{valid-id} returns 200 with data
- [ ] GET /internal/users/{invalid-id} returns 404
- [ ] No authentication header required
- [ ] Username field present in response
- [ ] Phone field present in response
- [ ] Roles field present in response
- [ ] Logs show audit trail

## 🎓 Key Learnings

✅ Internal endpoints for service-to-service communication don't need authentication  
✅ Use RequestParam or @PathVariable based on REST conventions  
✅ Always validate input and return appropriate HTTP status codes  
✅ Log all requests for audit trail  
✅ Never expose sensitive data (passwords, tokens)  
✅ Use DTOs for API responses to control what's exposed  

---

**Phase Status:** ✅ COMPLETE

**Ready for:** Phase 4.3 (Batch User Lookup)

