# 🚀 PHASE 3.2: ADMIN FEATURES - QUICK START GUIDE

**Status:** ✅ COMPLETE  
**Date:** February 18, 2026  
**Phase:** 3.2 - Admin Features

---

## 📋 WHAT'S BEEN IMPLEMENTED

### ✅ Two Admin Endpoints
1. **GET /api/admin/users** - List all users with pagination
2. **PUT /api/admin/users/{id}/role** - Update user roles

### ✅ Security Features
- `@EnableMethodSecurity` annotation in SecurityConfig
- `@PreAuthorize("hasRole('ADMIN')")` on both endpoints
- JWT token validation required
- Role-based access control

### ✅ New Classes
- **UpdateUserRoleRequest.java** - DTO for role updates

### ✅ Modified Classes
- **SecurityConfig.java** - Added method security
- **UserService.java** - Added 2 new methods
- **UserServiceImpl.java** - Implemented 2 new methods
- **UserController.java** - Added 2 new endpoints

---

## 🧪 QUICK TEST

### Test 1: Get All Users (Admin)
```bash
curl -X GET "http://localhost:3001/api/admin/users" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "content": [
    {
      "id": "uuid-1",
      "email": "user1@example.com",
      "roles": ["ROLE_CUSTOMER"],
      ...
    }
  ],
  "totalElements": 150,
  "totalPages": 8,
  "numberOfElements": 20
}
```

### Test 2: Update User Roles (Admin)
```bash
curl -X PUT "http://localhost:3001/api/admin/users/uuid-1/role" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"]
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": "uuid-1",
  "email": "user1@example.com",
  "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"],
  "message": "User roles updated successfully"
}
```

### Test 3: Access Denied (Non-Admin)
```bash
curl -X GET "http://localhost:3001/api/admin/users" \
  -H "Authorization: Bearer YOUR_CUSTOMER_JWT_TOKEN"
```

**Expected Response (403 Forbidden):**
```
Access Denied
```

---

## 🔑 KEY POINTS

| Feature | Details |
|---------|---------|
| **Authentication** | JWT Bearer Token (required) |
| **Authorization** | ADMIN role (checked by @PreAuthorize) |
| **Pagination** | Page 0 by default, 20 items per page |
| **Sorting** | Support: email, createdAt, updatedAt |
| **Validation** | Roles set cannot be empty |
| **Transactions** | All DB operations are atomic |
| **Logging** | Full audit trail of operations |

---

## 📊 ENDPOINT SUMMARY

### GET /api/admin/users
```
Method:       GET
Path:         /api/admin/users
Auth:         Required (ADMIN role)
Params:       page, size, sort (all optional)
Returns:      Page<UserResponse>
Status:       200 OK / 403 Forbidden / 401 Unauthorized
```

**Example:**
```
GET /api/admin/users?page=0&size=20&sort=email,asc
```

### PUT /api/admin/users/{id}/role
```
Method:       PUT
Path:         /api/admin/users/{id}/role
Auth:         Required (ADMIN role)
Body:         UpdateUserRoleRequest { roles: Set<Role> }
Returns:      UserResponse
Status:       200 OK / 400 Bad Request / 403 Forbidden / 404 Not Found
```

**Example:**
```
PUT /api/admin/users/b1530e06-e511-4044-b107-89325c36ba81/role
{
  "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"]
}
```

---

## 🛡️ SECURITY CHECKS

| Scenario | Result |
|----------|--------|
| ADMIN accesses /api/admin/users | ✅ SUCCESS |
| CUSTOMER accesses /api/admin/users | ❌ 403 FORBIDDEN |
| SELLER accesses /api/admin/users | ❌ 403 FORBIDDEN |
| No token provided | ❌ 401 UNAUTHORIZED |
| Expired token | ❌ 401 UNAUTHORIZED |
| Invalid token | ❌ 401 UNAUTHORIZED |

---

## 📝 CONFIGURATION IN application.yml

**Already Configured:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:postgresql://localhost:5432/shopsphere_user_db
    username: postgres
    password: password
  security:
    jwt:
      secret: ${JWT_SECRET_KEY}
      expiration: 900000  # 15 min in ms
      refresh-expiration: 604800000  # 7 days in ms
```

**No additional configuration needed!**

---

## 🚀 PRODUCTION CHECKLIST

- [x] Method security enabled
- [x] Role-based access control implemented
- [x] Input validation in place
- [x] Error handling comprehensive
- [x] Logging and monitoring setup
- [x] Pagination implemented
- [x] Transactions atomic
- [x] Documentation complete
- [x] Test scenarios provided
- [x] Ready for deployment

---

## 📚 ADDITIONAL RESOURCES

**Documentation Files:**
- `PHASE_3.2_COMPLETE.md` - Full implementation details
- `PHASE_3.2_ADMIN_TESTS.http` - 20 test scenarios
- `UserService .md` - Roadmap (updated)

**Related Phases:**
- Phase 3.1 - Profile APIs ✅ COMPLETE
- Phase 3.3 - Access Control (coming next)

---

## 💡 COMMON SCENARIOS

### Scenario 1: Promote Customer to Seller
```json
{
  "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"]
}
```

### Scenario 2: Make User Admin
```json
{
  "roles": ["ROLE_ADMIN"]
}
```

### Scenario 3: Demote Admin to Customer
```json
{
  "roles": ["ROLE_CUSTOMER"]
}
```

### Scenario 4: Revoke All Permissions (Not Recommended)
```json
{
  "roles": []  // ❌ This will fail - at least one role required
}
```

---

## ❌ COMMON ERRORS & SOLUTIONS

### Error: "Access Denied: User does not have required ADMIN role"
**Cause:** Token doesn't have ADMIN role  
**Solution:** Use an admin JWT token

### Error: "Roles cannot be empty"
**Cause:** Sent empty roles array  
**Solution:** Include at least one role in the request

### Error: "User not found"
**Cause:** Invalid user ID in URL  
**Solution:** Use a valid user ID from the database

### Error: "401 Unauthorized"
**Cause:** Missing or invalid JWT token  
**Solution:** Include valid Bearer token in Authorization header

---

## 🔄 WORKFLOW EXAMPLE

1. **Admin logs in** → Gets JWT token with ROLE_ADMIN
2. **Admin calls GET /api/admin/users** → Sees all users
3. **Admin calls PUT /api/admin/users/{id}/role** → Updates user's roles
4. **Updated user logs in** → Gets new JWT with updated roles
5. **User accesses new endpoints** → Based on updated roles

---

## 📞 TESTING WITH INSOMNIA/POSTMAN

### Step 1: Create Admin Login Request
```
POST http://localhost:3001/api/auth/login
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

### Step 2: Extract JWT Token
```
Take the token from response
Store as: {{admin_token}}
```

### Step 3: Test Get All Users
```
GET http://localhost:3001/api/admin/users
Authorization: Bearer {{admin_token}}
```

### Step 4: Test Update Roles
```
PUT http://localhost:3001/api/admin/users/{userId}/role
Authorization: Bearer {{admin_token}}
Body: {
  "roles": ["ROLE_CUSTOMER", "ROLE_SELLER"]
}
```

---

## ✨ NEXT PHASE: 3.3 Access Control

**Coming Soon:**
- Fine-grained permission checks
- Resource-level access control
- Additional role scenarios
- Advanced authorization logic

---

**Phase 3.2 Status:** ✅ COMPLETE & READY  
**Next Phase:** Phase 3.3 - Access Control  
**Date:** February 18, 2026

