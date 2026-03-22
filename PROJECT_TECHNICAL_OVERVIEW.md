# RevWorkforce - Comprehensive Technical Overview (Cloud-Native Edition)

This document provides a deep, comprehensive technical overview of **RevWorkforce**, a modern, full-stack Human Resource Management (HRM) system. 

Following the P3 modernization initiative, the monolithic backend has been fully refactored into a scalable, domain-driven **Spring Cloud Microservices Architecture**. This guide is intended for developers, architects, and DevOps engineers to understand the inner workings, database schemas, microservice communication, and authentication flow of the application.

---

## 1. Executive Summary

**RevWorkforce** is designed to streamline employee data management, leave tracking, performance reviews, goal management, HR reporting, and real-time notifications. 

To ensure massive scalability, strict fault isolation, and independent deployability, the application utilizes a **Cloud-Native Microservices Architecture** connecting 6 independent Spring Boot services via a **Spring Cloud Gateway**, utilizing **Netflix Eureka** for service discovery, **Spring Cloud Config** for centralized property management, and **OpenFeign** for inter-service communication. Each microservice maintains its own strict MySQL 8 Database schema (`user_db`, `employee_db`, `leaves_db`, `performance_db`, `notifications_db`, `reporting_db`).

---

## 2. Technology Stack Breakdown

### Frontend
- **Framework:** Angular 16.2.x (SPA)
- **UI Components:** Angular Material 16, SCSS
- **State/Reactivity:** RxJS 7.8
- **Data Visualization:** Chart.js (for dashboards)

### Backend (Microservices)
- **Core Platform:** Java 17
- **Framework:** Spring Boot 3.2.3, Spring Cloud 2023.0
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway (with Resilience4j Circuit Breakers)
- **Communication:** Spring Cloud OpenFeign
- **Security:** Spring Security, JWT (JSON Web Tokens)
- **Data Access:** Spring Data JPA / Hibernate
- **Build Tool:** Apache Maven 3.6+

### Database
- **Relational Database:** MySQL 8.0
- **Architecture:** Database-per-service pattern (`revworkforce_user_db`, `revworkforce_employee_db`, `revworkforce_leaves_db`, `revworkforce_performance_db`, `revworkforce_notifications_db`, `revworkforce_reporting_db`).

---

## 3. High-Level Architecture

The architecture utilizes the Spring Cloud suite. The Angular frontend makes all requests to the **API Gateway** on port 8080. The Gateway intelligently routes requests using **Eureka Discovery** to one of the 6 backend microservices.

```text
                          [ Angular 16 Client ]
                                   |
                          (HTTP requests :8080)
                                   v
                     +-----------------------------+
                     |  Spring Cloud API Gateway   |
                     |  (Resilience4j Fallbacks)   |
                     +-------------+---------------+
                                   |
           (Routes requests dynamically via Eureka Discovery)
                                   |
          +------------------------+------------------------+
          |                        |                        |
 /api/auth/*                /api/employees/*           /api/leaves/* ...
          |                        |                        |
          v                        v                        v
+-------------------+    +--------------------+    +--------------------+
|   User Service    |    |  Employee Service  |    |  Leave Service     |
|     (:8081)       |    |     (:8084)        |    |     (:8082)        |
+---------+---------+    +---------+----------+    +---------+----------+
          |                        |                        |
[user_db] v             [employee_db] v               [leaves_db] v
     [ MySQL 8 ]              [ MySQL 8 ]               [ MySQL 8 ]
```

### Key Architectural Decisions:
1. **Database-Per-Service Pattern**: Total schema isolation. The `leaves_db` cannot perform SQL joins against the `user_db`. This guarantees fault isolation and allows individual services to scale databases independently.
2. **OpenFeign Clients**: Because databases are separated, services communicate internally over HTTP via `@FeignClient` interfaces.
3. **Resilience4j Circuit Breakers**: Configured inside the API Gateway, these prevent slow underlying microservices from bottlenecking the entire system by immediately returning fallback responses (`503 Service Unavailable`).
4. **Shared Common Module**: A shared compile-time Maven library (`common`) reduces boilerplate by centralizing DTOs, Global Exception Handlers, and `JwtAuthFilter` security validation.

---

## 4. Microservices Deep Dive

### 1. Spring Cloud Infrastructure
- **Eureka Discovery Server (`:8761`)**: The central registry. Services ping this server on startup so the Gateway and Feign clients know where to route traffic.
- **Config Server (`:8888`)**: Externalizes configuration parameters across all microservices.
- **API Gateway (`:8080`)**: The single entry point for frontend requests, managing CORS, rate limits, and fallback routing.

### 2. User Service
**Responsibilities:** Identity management and authentication.
- **Entities:** `User` (email, password_hash, role).
- **Functions:** Processing login requests, hashing passwords with BCrypt, issuing JWTs.
- **Database:** `user_db`

### 3. Employee Management Service
**Responsibilities:** Core HR logic and company structure.
- **Entities:** `EmployeeProfile`, `Department`, `Designation`, `Announcement`, `ActivityLog`.
- **Functions:** Onboarding/Offboarding employees, managing company departments, viewing the directory. Queries `user-service` via Feign to validate permissions.
- **Database:** `employee_db`

### 4. Leave Service
**Responsibilities:** Tracking and processing time off.
- **Entities:** `LeaveApplication`, `LeaveBalance`, `CompanyHoliday`, `LeaveType`.
- **Functions:** Requesting leaves, checking casual/sick balances, manager approval workflows.
- **Database:** `leave_db`

### 5. Performance Service
**Responsibilities:** Employee growth tracking.
- **Entities:** `PerformanceReview`, `Goal`.
- **Functions:** Self-assessments, goal setting and progress percentage, manager feedback ratings.
- **Database:** `performance_db`

### 6. Reporting Service
**Responsibilities:** HR Dashboards and analytics.
- **Functions:** Aggregates data from other services to generate organization-wide metrics, leave utilization charts, and general HR dashboards.

### 7. Notification Service
**Responsibilities:** Real-time user alerts.
- **Entities:** `Notification`.
- **Functions:** Tracks unread/read states for system events (e.g., Leave Approvals).
- **Database:** `notification_db`

---

## 5. Security & Authentication Flow

Data validation occurs via **Stateless JWT Authorization**:

1. **Login:** User submits `{email, password}` to Gateway $\rightarrow$ `user-service`.
2. **Verification:** `user-service` verifies password and issues a signed JWT containing the `userId` and `role`.
3. **Token Transmission:** Frontend stores token and attaches it via HTTP HTTP Interceptor (`Authorization: Bearer <token>`).
4. **Token Validation:** Every individual microservice (e.g., `leave-service`) imports the `common` module's `JwtAuthFilter` which cryptographically validates the token on incoming requests without needing to ask the `user-service` or read a database.

**Role-Based Access Control (RBAC):**
Controller methods are secured natively:
- `@PreAuthorize("hasRole('ADMIN')")`
- `@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")`

---

## 6. Frontend Architecture (Angular 16)

- **Modular Design:** Divided into `core` (singletons, interceptors, auth guards), `shared` (reusable UI sidebars), and `features` (lazy-loaded modules).
- **API Pointing:** The `environment.ts` routes all traffic strictly to the Spring Cloud Gateway (`http://localhost:8080/api`). The frontend is entirely unaware of the 6 individual backend microservices or their ports.
- **Reactive State:** Makes extensive use of `RxJS` Observables to manage UI state dynamically.

---

## 7. Future Deployment Strategies

As the application is now strictly Cloud-Native, it is optimized for high-end orchestration:
- **Kubernetes (K8s):** The independent services can be containerized and managed via Kubernetes, allowing the `leave-service` to scale to dozens of pods during the holidays while the `user-service` remains static.
- **AWS Deployment:** Designed to be natively compatible with AWS EC2/ECS for computing, and AWS RDS for managing the 6 distributed MySQL databases.
