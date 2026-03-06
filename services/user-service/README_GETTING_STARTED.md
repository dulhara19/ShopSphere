# 🚀 GETTING STARTED - EPIC 2.4 ADDRESS MANAGEMENT

**Status:** Ready to Test & Deploy
**Date:** March 5, 2026

---

## ⏱️ ESTIMATED TIME: 1.5 - 2 HOURS

---

## ✅ PRE-FLIGHT CHECKLIST

Before you begin, ensure you have:

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.8+ installed (`mvn -version`)
- [ ] PostgreSQL running (`psql --version`)
- [ ] Database created: `shopsphere_user_dev`
- [ ] Git cloned (optional, for version control)

---

## 🎬 QUICK START (5 STEPS)

### Step 1: Navigate to Project (2 min)
```bash
cd F:\DEA2\ShopSphere\services\user-service
```

### Step 2: Build Project (3 min)
```bash
mvn clean compile
```
**Expected:** ✅ BUILD SUCCESS

### Step 3: Run Tests (5 min)
```bash
mvn clean test
```
**Expected:** ✅ All tests pass (27+)

### Step 4: Start Application (2 min)
```bash
mvn spring-boot:run
```
**Expected:** ✅ Application listening on port 3001

### Step 5: Test Endpoints (5 min)
```bash
# Option A: Use IntelliJ REST Client
# Open: PHASE_3_EPIC_2.4_ADDRESS_TESTS.http
# Click "Run" on each request

# Option B: Use curl commands (see below)
```

**Total Time:** ~20 minutes to full verification

---

## 🔑 KEY FILES TO KNOW

### Documentation (Start Here)
1. **Quick Start** → `PHASE_3_EPIC_2.4_QUICK_START_GUIDE.md` (20 min)
2. **Technical Details** → `docs/PHASE_3_EPIC_2.4_ADDRESS_MANAGEMENT.md` (30 min)
3. **This File** → `README_GETTING_STARTED.md` (you are here)

### Source Code (What Was Built)
- `src/main/java/com/shopsphere/user/model/Address.java`
- `src/main/java/com/shopsphere/user/repository/AddressRepository.java`
- `src/main/java/com/shopsphere/user/dto/AddressDto.java`
- `src/main/java/com/shopsphere/user/service/AddressService.java`
- `src/main/java/com/shopsphere/user/service/impl/AddressServiceImpl.java`
- `src/main/java/com/shopsphere/user/controller/AddressController.java`

### Tests (How It's Verified)
- `src/test/java/.../AddressServiceImplTest.java` (Unit tests)
- `src/test/java/.../AddressControllerTest.java` (Integration tests)

### API Tests (How to Test Manually)
- `PHASE_3_EPIC_2.4_ADDRESS_TESTS.http` (14+ HTTP requests)

---

## 🧪 TESTING GUIDE

### Option 1: IntelliJ REST Client (Easiest) ⭐
```
1. Open file: PHASE_3_EPIC_2.4_ADDRESS_TESTS.http
2. Find "###" separators (request blocks)
3. Click "Run" link above each request
4. View response in right panel
5. Update token/IDs for subsequent requests
```

### Option 2: Command Line (curl)
```bash
# 1. Register a user
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "Test@123"
  }'

# 2. Login to get token
TOKEN=$(curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123"
  }' | jq -r '.accessToken')

# 3. Create an address
curl -X POST http://localhost:3001/api/users/me/addresses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'

# 4. Get all addresses
curl -X GET http://localhost:3001/api/users/me/addresses \
  -H "Authorization: Bearer $TOKEN"
```

### Option 3: Postman
```
1. Create new request collection
2. Copy requests from PHASE_3_EPIC_2.4_ADDRESS_TESTS.http
3. Set base URL: http://localhost:3001
4. Add Authorization header with Bearer token
5. Run requests one by one
```

---

## ✅ VERIFICATION CHECKLIST

After completing the 5 quick start steps, verify:

### Build & Compilation ✅
- [ ] `mvn clean compile` succeeds with 0 errors
- [ ] 0 compilation warnings

### Tests ✅
- [ ] `mvn clean test` passes
- [ ] 15+ unit tests pass
- [ ] 12+ integration tests pass
- [ ] Test execution time < 30 seconds

### Application Startup ✅
- [ ] Application starts with `mvn spring-boot:run`
- [ ] No exception errors in logs
- [ ] Listening on port 3001
- [ ] Takes < 10 seconds to start

### Database ✅
- [ ] PostgreSQL connection successful
- [ ] `shopsphere_user_dev` database exists
- [ ] `addresses` table auto-created
- [ ] `users` table has `addresses` relationship

### API Endpoints ✅
- [ ] POST `/api/users/me/addresses` returns 201
- [ ] GET `/api/users/me/addresses` returns 200
- [ ] PUT `/api/users/me/addresses/{id}/default` returns 200
- [ ] DELETE `/api/users/me/addresses/{id}` returns 204

### Security ✅
- [ ] Unauthenticated requests return 401
- [ ] Invalid token returns 401
- [ ] Non-matching user returns 404
- [ ] Admin-only endpoints work with admin token

### Data Integrity ✅
- [ ] First address auto-set as default
- [ ] Only one default per user
- [ ] Deleting default sets next as default
- [ ] Deleting non-existent address returns 404

---

## 🐛 TROUBLESHOOTING QUICK FIXES

### "mvn: command not found"
**Solution:** Maven not installed or not in PATH
```bash
# Install Maven or add to PATH
# Windows: Set MAVEN_HOME and add to PATH
# Linux/Mac: brew install maven
```

### "Connection refused" for PostgreSQL
**Solution:** PostgreSQL not running
```bash
# Start PostgreSQL service
# Windows: Services.msc → PostgreSQL → Start
# Mac: brew services start postgresql
# Linux: sudo service postgresql start
```

### "Database 'shopsphere_user_dev' does not exist"
**Solution:** Create the database
```bash
createdb shopsphere_user_dev
```

### "Port 3001 already in use"
**Solution:** Kill process on port 3001
```bash
# Linux/Mac:
lsof -i :3001 | grep LISTEN | awk '{print $2}' | xargs kill -9

# Windows:
netstat -ano | findstr :3001
taskkill /PID {PID} /F
```

### Tests failing with "Connection refused"
**Solution:** Database not running during tests
```bash
# Ensure PostgreSQL is running, then:
mvn clean test
```

### "JWT token expired"
**Solution:** Token validity is 15 minutes
```bash
# Get a new token:
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "Test@123"}'
```

**For more help:** See `PHASE_3_EPIC_2.4_QUICK_START_GUIDE.md` Troubleshooting section

---

## 📊 WHAT YOU'LL SEE

### Successful Build Output
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXX s
[INFO] Finished at: ...
```

### Successful Test Output
```
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Successful Startup
```
2026-03-05 10:30:00 INFO  UserServiceApplication : Started UserServiceApplication in X.XXs
2026-03-05 10:30:00 INFO  Tomcat : Tomcat started on port(s): 3001
```

### Successful Endpoint Response
```json
{
  "id": "uuid",
  "street": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA",
  "isDefault": true
}
```

---

## 📈 NEXT STEPS AFTER VERIFICATION

### If All Checks Pass ✅
1. Read full documentation
2. Review source code
3. Code review with team
4. Merge to main branch
5. Deploy to staging

### If Issues Found ❌
1. Check error messages
2. Refer to troubleshooting guide
3. Review Quick Start Guide
4. Consult technical documentation

---

## 🎯 SUCCESS CRITERIA

You've successfully set up Epic 2.4 when:

✅ All tests pass
✅ Application starts without errors
✅ Can create addresses via API
✅ Can retrieve addresses
✅ Can set default address
✅ Can delete addresses
✅ Security prevents unauthorized access

---

## 📞 NEED HELP?

### Documentation
- `PHASE_3_EPIC_2.4_QUICK_START_GUIDE.md` - Detailed setup
- `docs/PHASE_3_EPIC_2.4_ADDRESS_MANAGEMENT.md` - Technical details
- `PHASE_3_EPIC_2.4_DELIVERABLES_CHECKLIST.md` - Verification

### Code Examples
- Test files show expected behavior
- `PHASE_3_EPIC_2.4_ADDRESS_TESTS.http` shows API usage
- Javadoc comments in source code

### Error Messages
- Check application logs
- Review test output
- See Troubleshooting section

---

## ⏰ TIME BREAKDOWN

| Task | Time |
|------|------|
| Navigate & Setup | 2 min |
| Build Project | 3 min |
| Run Tests | 5 min |
| Start App | 2 min |
| Manual Testing | 5 min |
| Review Results | 3 min |
| **TOTAL** | **~20 min** |

---

## 🎉 YOU'RE READY!

Everything is set up and ready for you to:
1. ✅ Build the project
2. ✅ Run the tests
3. ✅ Start the application
4. ✅ Test the endpoints
5. ✅ Review the code
6. ✅ Deploy with confidence

**Let's go!** 🚀

---

**Start with:** `PHASE_3_EPIC_2.4_QUICK_START_GUIDE.md`
**Or begin directly:** `mvn clean test` in the user-service directory

---

**Status:** ✅ Ready to Go
**Date:** March 5, 2026
**Confidence Level:** 🟢 HIGH - All systems tested and verified

