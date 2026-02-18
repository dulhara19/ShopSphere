# 🔐 RBAC DEVELOPER QUICK REFERENCE

**Date:** February 19, 2026  
**Status:** ✅ IMPLEMENTATION COMPLETE  
**Authority Pattern:** `hasAuthority('ROLE_NAME')`

---

## 📋 QUICK START

### The Standard Pattern

For all new controller methods requiring authorization, use:

```java
@PreAuthorize("hasAuthority('ROLE_NAME')")
```

### Role Definitions

| Role | Authority | Use Case |
|------|-----------|----------|
| ADMIN | `ADMIN` | System administrators |
| SELLER | `SELLER` | Vendors/merchants |
| CUSTOMER | `CUSTOMER` | Regular users |

---

## 🛠️ COPY-PASTE TEMPLATES

### Template 1: Admin-Only Endpoint

```java
@GetMapping("/api/admin/reports")
@PreAuthorize("hasAuthority('ADMIN')")
public ResponseEntity<?> getReports() {
    // Admin-only logic here
    return ResponseEntity.ok(...);
}
```

---

### Template 2: Seller-Only Endpoint

```java
@PostMapping("/api/seller/products")
@PreAuthorize("hasAuthority('SELLER')")
public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
    // Seller-only logic here
    return ResponseEntity.ok(...);
}
```

---

### Template 3: Authenticated Users (Any Role)

```java
@GetMapping("/api/users/profile")
@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
public ResponseEntity<?> getProfile() {
    // Any authenticated user can access
    return ResponseEntity.ok(...);
}
```

---

### Template 4: Multiple Specific Roles

```java
@GetMapping("/api/content/moderate")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
public ResponseEntity<?> getModerationQueue() {
    // Only admins or moderators
    return ResponseEntity.ok(...);
}
```

---

### Template 5: Public Endpoint (No Authorization)

```java
@PostMapping("/api/auth/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // No @PreAuthorize - public endpoint
    return ResponseEntity.ok(...);
}
```

---

## 🔑 HOW RBAC WORKS

### Behind the Scenes

```
1. User logs in → JWT token generated with roles claim
2. Client sends JWT in Authorization header
3. JwtAuthenticationFilter extracts roles from JWT
4. Roles converted to GrantedAuthority objects
5. @PreAuthorize checks if user has required authority
6. Request allowed or 403 Forbidden returned
```

---

## 📝 IMPLEMENTATION CHECKLIST

When adding new protected endpoints:

- [ ] Add `@PreAuthorize("hasAuthority('...')")` annotation
- [ ] Specify correct authority name (ADMIN, SELLER, CUSTOMER, etc.)
- [ ] Use consistent naming across all endpoints
- [ ] Add javadoc comment explaining authorization
- [ ] Test with valid and invalid roles
- [ ] Verify 403 Forbidden for insufficient authority
- [ ] Verify 401 Unauthorized for missing token

---

## ✅ VERIFICATION

### Test Admin Endpoint

```bash
# With ADMIN role - Success (200)
curl -X GET "http://localhost:3001/api/admin/users" \
  -H "Authorization: Bearer <ADMIN_JWT>"

# With CUSTOMER role - Forbidden (403)
curl -X GET "http://localhost:3001/api/admin/users" \
  -H "Authorization: Bearer <CUSTOMER_JWT>"

# No token - Unauthorized (401)
curl -X GET "http://localhost:3001/api/admin/users"
```

---

## 🎯 COMMON PATTERNS

### Pattern 1: Single Role Required

```java
@PreAuthorize("hasAuthority('ADMIN')")
```

### Pattern 2: Any of Multiple Roles

```java
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SELLER')")
```

### Pattern 3: All Authenticated Users

```java
@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SELLER') or hasAuthority('ADMIN')")
```

### Pattern 4: Role Plus Permission Check

```java
@PreAuthorize("hasAuthority('SELLER') and @permissionService.canEdit(#id)")
```

---

## ⚠️ IMPORTANT NOTES

✅ **DO:**
- Use `hasAuthority('ROLE_NAME')` for role checks
- Capitalize role names (ADMIN, SELLER, CUSTOMER)
- Add @PreAuthorize to all protected endpoints
- Document authorization requirements
- Test with multiple user roles
- Use consistent naming across endpoints

❌ **DON'T:**
- Use `hasRole('ROLE_ROLE_NAME')` - wrong format
- Mix different authorization patterns
- Forget to add @PreAuthorize on protected endpoints
- Use hardcoded role checks in method body
- Bypass authorization checks

---

## 🚀 AUTHORITY EXTRACTION

The RBAC system automatically:

1. **Extracts roles from JWT** via `JwtAuthenticationFilter`
2. **Converts to GrantedAuthority** using `SimpleGrantedAuthority`
3. **Sets in SecurityContext** for `@PreAuthorize` evaluation
4. **Enables authorization** at method level

You don't need to do anything - just add `@PreAuthorize` and it works!

---

## 📞 REFERENCE FILES

- **Implementation Details:** `RBAC_IMPLEMENTATION_GUIDE.md`
- **Test Examples:** `test-register.http`
- **Filter Code:** `JwtAuthenticationFilter.java`
- **JWT Utility:** `JwtUtils.java`
- **Auth Service:** `AuthService.java`
- **Security Config:** `SecurityConfig.java`

---

## ✨ SUMMARY

```
@PreAuthorize("hasAuthority('ROLE_NAME')")
↓
Checks if authenticated user has specified authority
↓
Authority set during login via JWT token
↓
Simple, consistent, production-ready
```

---

**Date:** February 19, 2026  
**Status:** ✅ Ready for Implementation  
**Pattern:** `hasAuthority('ROLE_NAME')`

