# Microservices Migration Track
This document tracks the progress of migrating RevWorkforce from a bundled architecture to a fully cloud-native Spring Cloud architecture.

## Phase 1: Infrastructure Setup
- [x] Set up Eureka Service Registry
- [x] Set up Spring Cloud Config Server (Optional, but recommended for architecture)
- [x] Set up Spring Cloud Gateway (API Gateway) replacing Nginx

## Phase 2: Service Splitting & Database Decomposition
- [x] Break down `schema.sql` into per-service schemas (In Progress - split `user_db`)
- [x] Extract User Service (Auth, RBAC, Profile) - Fully Extracted (Port 8081)
- [x] Extract Employee Management Service (Onboarding, Departments, Announcements)
- [x] Refactor Leave Service (Balances, Workflows, Holidays) - Code migrated to `leave-service`
- [x] Refactor Performance Service (Reviews, Goals) - Code migrated to `performance-service`
- [x] Implement Reporting Service (Dashboards, Metrics) - Scaffolded structure
- [x] Adapt Notification Service

## Phase 3: Inter-Service Communication
- [x] Implement OpenFeign clients for cross-service calls (Pattern initialized in `employee-management-service`)
- [x] Implement Resilience4j circuit breakers in the Gateway / Feign clients

## Phase 4: CI/CD (Ongoing/Later)
- [ ] Setup GitHub Actions / Jenkins pipelines (AWS deployment skipped per requirement)

---
*Created on:* 2026-03-22
