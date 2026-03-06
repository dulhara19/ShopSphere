# 🔍 REDIS DEBUG IMPLEMENTATION - COMPLETE

**Status:** ✅ Debug logging added to all relevant methods
**Date:** March 5, 2026
**Purpose:** Diagnose why tokens are not being stored in Redis

---

## 📋 What Was Done

I've added comprehensive debug logging to help diagnose why tokens aren't being stored in Redis:

### ✅ 3 Methods Enhanced

1. **AuthService.logout()** (Primary method)
   - Token extraction from Bearer prefix
   - Token validation status
   - Expiration date extraction
   - **Current time logging**
   - **Expiration time logging**
   - **TTL calculation with breakdown**
   - TTL > 0 verification
   - Success/failure status

2. **TokenBlacklistService.blacklistToken()** (Redis storage)
   - RedisTemplate null check (bean injection verification)
   - Redis connection verification
   - **SET operation logging**
   - **Post-set verification** (hasKey check)
   - **TTL verification** (getExpire call)
   - Exception details with full stack trace

3. **TokenBlacklistService.isTokenBlacklisted()** (Token verification)
   - Redis lookup logging
   - Blacklist status reporting
   - TTL remaining display
   - Fail-open strategy confirmation

---

## 🎯 Key Metrics Being Logged

### Before Storage (AuthService.logout)
```
Current Time (ms):     [System.currentTimeMillis()]
Expiration Time (ms):  [Token exp claim]
TTL Calculated (ms):   [Expiration - Current]
TTL in Seconds:        [TTL / 1000]
TTL in Minutes:        [TTL / 1000 / 60]
TTL > 0?               [true/false] ← CRITICAL
```

### During Storage (TokenBlacklistService.blacklistToken)
```
Redis Key:             token:blacklist:{full_token}
Redis Value:           "blacklisted"
Redis TTL:             {expirationTimeMillis} MILLISECONDS
SET Operation Status:  ✓ Success or ❌ Failed
Token Exists Check:    true/false ← VERIFICATION
TTL Verification:      {actual_ttl_in_redis} ms
```

---

## 🚀 How to Use the Debug Logs

### Step 1: Compile & Run
```bash
cd F:\DEA2\ShopSphere\services\user-service
mvn clean compile
mvn spring-boot:run
```

### Step 2: Perform Logout
Use your test-register.http file or:
```bash
curl -X POST http://localhost:3001/api/auth/logout \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Step 3: Check Console Output
Look for the bordered debug sections:
```
========== LOGOUT DEBUG START ==========
...debug info...
========== LOGOUT DEBUG END ==========
```

### Step 4: Analyze the Output

**If you see:**
```
TTL Calculated (ms):   900000
TTL > 0?               true
✓ Token blacklisted successfully with TTL: 900000 ms
```
→ **Token should be in Redis!**

**If you see:**
```
TTL Calculated (ms):   -5000
TTL > 0?               false
⚠ TTL is <= 0 (token already expired)
```
→ **Use a fresh token from login**

---

## 🔧 Troubleshooting Guide

### Issue 1: Token Not in Redis (TTL > 0 but still not stored)

Check these logs:
```
✓ RedisTemplate is available          → Bean is injected ✓
✓ SET operation completed successfully → SET worked ✓
✓ Verification - Token exists: ???     → Check this value!
```

If "Token exists" is `false`, it means:
1. **Serialization issue** - Token key format problem
2. **Redis issue** - Data not persisting
3. **Connection issue** - SET didn't actually execute

**Solution:**
```bash
# Connect to Redis and check manually
redis-cli

# List all keys
KEYS *

# Try to set manually
SET test:key "value" EX 100

# Get it back
GET test:key
```

### Issue 2: TTL is 0 or Negative

**Log shows:**
```
Current Time (ms):     1709640000000
Expiration Time (ms):  1709639999000  ← Older than current time!
TTL Calculated (ms):   -1000          ← NEGATIVE!
TTL > 0?               false
```

**Solution:** Use a fresh token!
```bash
1. Call POST /api/auth/login to get new token
2. Immediately call POST /api/auth/logout
3. TTL should be ~900000 ms (15 minutes)
```

### Issue 3: RedisTemplate is NULL

**Log shows:**
```
❌ RedisTemplate is NULL! Bean not properly injected.
```

**Solution:**
1. Verify RedisConfig.java has @Configuration annotation
2. Verify @Bean method for redisTemplate
3. Check pom.xml has spring-boot-starter-data-redis
4. Restart application

---

## 📊 Expected Log Output (Complete Example)

```
========== LOGOUT DEBUG START ==========
Attempting to logout and blacklist token
Extracted token from Bearer prefix
Token (first 50 chars): eyJhbGciOiJIUzM4NCJ9.eyJyb2xlcyI6WyJDVVNUT01FUiJdLCJ0b2tlblR5cGUi
✓ Token validation passed
✓ Expiration date extracted: Wed Mar 05 10:15:23 IST 2026
--- TTL CALCULATION DEBUG ---
Current Time (ms):     1709640000000
Expiration Time (ms):  1709640900000
TTL Calculated (ms):   900000
TTL in Seconds:        900
TTL in Minutes:        15
TTL > 0?               true
--- END TTL DEBUG ---
✓ TTL is positive, proceeding with blacklist
========== REDIS BLACKLIST DEBUG START ==========
Token (first 50 chars): eyJhbGciOiJIUzM4NCJ9.eyJyb2xlcyI6WyJDVVNUT01FUiJdLCJ0b2tlblR5cGUi
Expiration Time (ms):   900000
Expiration Time (sec):  900
Redis Key (first 100 chars): token:blacklist:eyJhbGciOiJIUzM4NCJ9.eyJyb2xlcyI6WyJDVVNUT01FUiJdLCJ0b2tlblR5cGU...
Redis Value: 'blacklisted'
Redis TTL: 900000 MILLISECONDS
Attempting to connect to Redis...
✓ RedisTemplate is available
Attempting SET operation on Redis...
✓ SET operation completed successfully
✓ Verification - Token exists in Redis: true
✓ Verification - Token TTL in Redis: 900000 ms (900 seconds)
✓ Token blacklisted successfully with TTL: 900000 ms
========== REDIS BLACKLIST DEBUG END (SUCCESS) ==========
========== LOGOUT DEBUG END (SUCCESS) ==========
```

---

## ✅ Verification After Debugging

### 1. Check Redis Directly
```bash
redis-cli

# Should see keys
KEYS token:blacklist:*

# Get token details
GET token:blacklist:YOUR_TOKEN_HERE
# Should return: "blacklisted"

# Check TTL
TTL token:blacklist:YOUR_TOKEN_HERE
# Should return: positive number (seconds remaining)
```

### 2. Test Token is Blacklisted
```bash
# Try to use the token after logout
curl -X GET http://localhost:3001/api/users/me \
  -H "Authorization: Bearer BLACKLISTED_TOKEN"

# Expected: HTTP 401 with message "Token has been revoked"
```

---

## 🎯 Summary

✅ **Added debug logging to diagnose Redis storage issue**
✅ **Logs capture TTL calculation at each step**
✅ **Logs verify Redis operations**
✅ **Comprehensive error reporting**
✅ **Step-by-step troubleshooting guide provided**

---

## 📝 Files Updated

1. ✅ AuthService.java - logout() method with TTL debugging
2. ✅ TokenBlacklistService.java - blacklistToken() with Redis verification
3. ✅ TokenBlacklistService.java - isTokenBlacklisted() with lookup logging

---

## 🚀 Next Action

1. Restart your application
2. Perform logout using your test file
3. **Share the console output** with me
4. I'll analyze and provide specific fixes

---

**Status: ✅ DEBUGGING INFRASTRUCTURE READY**

