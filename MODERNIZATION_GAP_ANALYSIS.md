# RevWorkforce - Cloud-Native Modernization Gap Analysis

This document outlines the architectural differences between the **current implementation** of RevWorkforce and the **target requirements** defined in the P3 Cloud-Native Microservices modernization project. 

It serves as a roadmap for the remaining work required to successfully transition the application to a true cloud-native, Spring Cloud-driven architecture.

---

## 1. Microservices Re-Architecture & Domain Splitting

**Current State:**
The backend is split into 3 "bundled" services:
- `auth-employee-service` (Authentication and Employee management)
- `leave-performance-service` (Leaves, Performance, and Goals)
- `notification-service` (Notifications)

**Target State (To Be Implemented):**
The monolithic services must be refactored into **6 strictly distinct, domain-driven services**:
1. **User Service:** Extract Authentication, RBAC (Role-Based Access Control), and basic employee profile management.
2. **Employee Management Service:** Extract Employee onboarding/offboarding, departments, designations, announcements, and system configurations.
3. **Leave Service:** Handle leave balances, applications, workflows, quotas, and company holidays.
4. **Performance Service:** Handle self-reviews, goal setting/tracking, manager feedback, and performance ratings.
5. **Reporting Service (NEW):** Build a dedicated service to generate HR dashboards, leave utilization reports, and key metrics.
6. **Notification Service:** Adapt the existing service to handle real-time in-app notifications specifically triggered via events.

---

## 2. Database-Per-Service Architecture

**Current State:**
A single monolithic MySQL database (`revworkforce_db`) is shared across all services. The schema relies heavily on SQL Foreign Keys bridging different domains (e.g., `leave_applications` directly referencing `employees(id)`).

**Target State (To Be Implemented):**
- **Independent Schemas:** Break `schema.sql` into 6 separate databases (or strictly isolated schemas) such as `user_db`, `leave_db`, `performance_db`, etc.
- **Independent Lifecycles:** Each service must maintain its own database credentials, entity relationships, and Spring Data JPA repositories.
- **Relational Decoupling:** Remove all SQL Foreign Keys that span across different service boundaries. Cross-service relationships must be handled logically in the Java code (e.g., Leave Service stores an `employeeId` column as a standard integer, not a strict foreign key to another DB).

---

## 3. Spring Cloud Infrastructure Integration

**Current State:**
The project uses standard Spring Boot applications. Routing is handled by an external Nginx proxy, and services communicate simply by reading/writing to the shared MySQL database.

**Target State (To Be Implemented):**
Integrate the complete **Spring Cloud stack** to manage the distributed system:
1. **Eureka Server (Service Discovery):** Build a new Spring Boot application acting as a Netflix Eureka Service Registry. All 6 microservices must register themselves upon startup so they can resolve each other dynamically.
2. **Spring Cloud Gateway (API Gateway):** Replace the Nginx proxy with a Java-based Spring Cloud Gateway. This gateway must include:
   - Dynamic routing to the 6 microservices via Eureka.
   - Global rate-limiting.
   - **Resilience4j** circuit breakers to handle downstream failures gracefully.
3. **Config Server:** Create a Spring Cloud Config Server to host centralized application properties (e.g., fetching `application.yml` configurations from a dedicated Git repository instead of hardcoding them locally).
4. **OpenFeign Clients:** Implement synchronous HTTP communication between the newly separated microservices. For example, the Leave Service will use a Feign Client to verify if a user exists in the User Service since it can no longer query the User database directly.

---

## 4. DevOps, CI/CD, and Cloud Deployment

**Current State:**
Deployment is limited to local development using a standard `docker-compose.yml` file.

**Target State (To Be Implemented):**
- **Automated Pipelines:** Implement CI/CD workflows using **Jenkins** or **GitHub Actions** to automate the building, testing (Maven), and containerization (Docker) of the 6 backend services and the Angular frontend.
- **AWS Cloud Infrastructure:** Deploy the applications to Amazon Web Services.
  - Set up **AWS EC2** instances to host the containers or Gateway.
  - Provision Managed Databases using **AWS RDS** (Relational Database Service) for the MySQL instances.

---

## Summary of Next Steps

To meet the requirements of the P3 Monolith-to-Microservices transition, developers should prioritize work in the following order:

1. **Bootstrap the Infrastructure:** Create the Eureka Server and Spring Cloud Config Server.
2. **Refactor the Codebase:** Split the existing 3 projects into the required 6 distinct Spring Boot modules.
3. **Database Migration:** Deconstruct the monolithic `revworkforce_db`. Drop cross-boundary foreign keys and configure specific database connections for each service.
4. **Inter-Service Communication:** Replace shared DB queries with Feign Client REST calls.
5. **API Gateway:** Implement the Spring Cloud Gateway to route traffic and apply Resilience4j.
6. **Cloud Pipeline:** Setup GitHub Actions/Jenkins pipelines deploying builds directly to AWS EC2 and RDS environments.
