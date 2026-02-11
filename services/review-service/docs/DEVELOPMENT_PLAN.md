# 🗺️ Review Service - Implementation Roadmap

## 🛠️ Phase 1: Foundation & Data Layer
Goal: Base project setup saha MongoDB integration.

- [x] **1.1 Project Configuration**
    - `pom.xml` ekata dependencies ekathu kirima (MongoDB, Redis, Lombok).
    - `application.yml` eke MongoDB connection saha server port 3007 setup kirima.
- [x] **1.2 Core Domain Models**
    - `Review.java` entity eka hadima (rating, title, body, status, etc.).
    - `ReviewStatus` Enum (PENDING, APPROVED, REJECTED) hadima.
- [x] **1.3 Repository Layer**
    - `ReviewRepository` (MongoRepository) interface eka create kirima.

---

## 🚀 Phase 2: Core APIs (Epic 1.1 & 1.3)
Goal: Reviews danna saha balanna puluwan basic functions.

- [x] **2.1 Review Submission**
    - `POST /api/reviews` endpoint eka hadima.
    - Review ekak save karana logic eka `ReviewService` eke liyanna.
- [x] **2.2 Review Retrieval**
    - `GET /api/reviews/product/{productId}` (Pagination & Sorting ekka).
    - `GET /api/reviews/{reviewId}` individual review ganna endpoint eka.
- [ ] **2.3 Review Management**
    - Edit review (30-day limit) saha Delete review (Soft delete) logic.
- [ ] **2.4 Helpfulness Feature**
    - Helpful vote count update kirima.

---

## ⚡ Phase 3: Rating Aggregation & Caching (Epic 1.2)
Goal: Performance improve kirima saha rating calculations.

- [ ] **3.1 Aggregation Logic**
    - Average rating saha star distribution calculate kirima.
- [ ] **3.2 Redis Integration**
    - Calculated ratings Redis wala cache kirima (Fast access).
- [ ] **3.3 Summary API**
    - `GET /api/reviews/product/{productId}/summary` endpoint eka.

---

## 🔗 Phase 4: Inter-Service Communication (Epic 1.6)
Goal: Anith microservices ekka data share kirima.

- [ ] **4.1 Feign Clients Setup**
    - `Order Service` ekata katha karala "Verified Purchase" badge eka check kirima.
- [ ] **4.2 Event Publishing**
    - `review.created` event eka RabbitMQ/Kafka haraha publish kirima.
- [ ] **4.3 Internal Endpoints**
    - Product Service ekata ratings laba dena internal APIs.

---

## 👮 Phase 5: Moderation & Advanced Features
Goal: Spam control saha Phase 2 requirements.

- [ ] **5.1 Moderation System**
    - Admin review queue saha status update (Approve/Reject).
- [ ] **5.2 Comments System**
    - Review ekakata comments saha seller responses danna puluwan kalla.
- [ ] **5.3 Media Uploads (Phase 2)**
    - AWS S3 use karala images/videos upload kirima.
