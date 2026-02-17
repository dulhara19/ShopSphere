# 👤 User Service - Implementation Roadmap

## 🛠️ Phase 1: Foundation & Security Layer
**Goal:** Base project setup, PostgreSQL integration, and JWT infrastructure.

- [x] **1.1 Project Configuration**
    - `pom.xml` setup with Spring Boot 3.x, Security, JPA, PostgreSQL, Redis, and Lombok.
    - `application.yml` setup (Server Port: 3001, DB Connection strings).
- [x] **1.2 Core Domain Models**
    - `User.java` Entity (UUID for ID, unique email,passwordHash, firstName, lastName, phone, and roles).
    - `Role` Enum (CUSTOMER, SELLER, ADMIN).
- [x] **1.3 Security Base**
    - Setup `BCryptPasswordEncoder` for password security.
    - Create `JwtUtils` class (Sign, Parse, Expiration logic).

---

## 🔑 Phase 2: Core Auth APIs (Epic 1.1)
**Goal:** Enable users to join the platform and stay authenticated.

- [x] **2.1 User Registration**
    - `POST /api/auth/register` endpoint.
    - Logic: Email duplication check & Password hashing.
- [ ] **2.2 Authentication & JWT**
    - `POST /api/auth/login` endpoint.
    - Credential validation & Token generation (Access 15m / Refresh 7d).
- [ ] **2.3 Token Management**
    - `POST /api/auth/refresh` for token rotation.
    - Logout logic (Token blacklisting in Redis).

---

## 👤 Phase 3: Profile & Role Management (Epic 1.2 & 1.3)
**Goal:** Manage user data and Role-Based Access Control (RBAC).

- [ ] **3.1 Profile APIs**
    - `GET /api/users/me` (Profile retrieval).
    - `PUT /api/users/{id}` (Update Name, Phone, Address).
- [ ] **3.2 Admin Features**
    - `GET /api/admin/users` (Paginated list).
    - `PUT /api/admin/users/{id}/role` (Role update logic).
- [ ] **3.3 Access Control**
    - Method security using `@PreAuthorize` (ADMIN/CUSTOMER roles).

---

## 🔗 Phase 4: Inter-Service Communication (Epic 1.4)
**Goal:** Connect with Gateway and other Microservices.

- [ ] **4.1 Internal Validation**
    - `POST /internal/auth/validate` for API Gateway.
- [ ] **4.2 Data Lookup**
    - `GET /internal/users/{id}` (Lightweight DTO for other services).
- [ ] **4.3 Event Publishing**
    - Configure RabbitMQ/Kafka producer.
    - Publish `user.registered` event.

---

## 👮 Phase 5: Advanced Security & Auditing
**Goal:** Modern auth features and system auditing.

- [ ] **5.1 OAuth2 Integration**
    - Google/Facebook Login integration.
- [ ] **5.2 Account Recovery**
    - Forgot Password/Reset Password flow via Email.
- [ ] **5.3 Audit & Monitoring**
    - Implement JPA Auditing (`createdAt`, `updatedAt`).
    - Audit logging for sensitive actions.
