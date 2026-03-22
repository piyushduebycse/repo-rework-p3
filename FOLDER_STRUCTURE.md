# RevWorkforce Microservices - Folder Structure Guide

This document maps out the strict Domain-Driven folder hierarchy of the completely refactored P3 Cloud-Native architecture. 

All legacy monolithic controllers and deeply coupled JPA join structures have been forcefully decoupled into these explicitly isolated folders. Inter-service data retrieval is handled strictly over HTTP OpenFeign clients instead of direct SQL mappings.

```text
revworkforce/
 │
 ├── common/                          # Global Shared Context (Compiled independently)
 │   ├── src/main/java...dto/         # Universal Authentication & Employee DTOs
 │   ├── src/main/java...exception/   # Universal Global Exception Handlers
 │   └── src/main/java...security/    # Single JwtAuthFilter configured for all services
 │
 ├── api-gateway/                     # Spring Cloud Gateway (Port 8080)
 ├── config-server/                   # Spring Cloud Config (Port 8888)
 ├── service-discovery/               # Netflix Eureka Registry (Port 8761)
 │
 ├── user-service/                    # Authentication, Role/JWT Minting (Port 8081)
 │   └── DB: revworkforce_user_db
 │
 ├── leave-service/                   # Applications, Quotas, Holidays (Port 8082)
 │   └── DB: revworkforce_leaves_db
 │
 ├── performance-service/             # Goal Tracking, Manager Feedbacks (Port 8083)
 │   └── DB: revworkforce_performance_db
 │
 ├── employee-service/                # Departments, Designations, Profiles (Port 8084)
 │   └── DB: revworkforce_employee_db
 │
 ├── reporting-service/               # Global Admin Datasets (Port 8085)
 │   └── DB: revworkforce_reporting_db
 │
 ├── notification-service/            # Dynamic Socket Alerts (Port 8086)
 │   └── DB: revworkforce_notifications_db
 │
 ├── frontend/                        # Angular Client Application
 │   └── src/app/core/guards/         # Split Routing (auth.guard.ts & role.guard.ts)
 │
 ├── docker-compose.yml               # Complete orchestration infrastructure
 ├── init-databases.sql               # Automated volume schema initialization script
 └── pom.xml                          # Parent Maven Reactor declaring all modules
```

## Architectural Enforcement Highlights:
1. **No Shared Schema Access:** No microservice is allowed to import a Repository from another module.
2. **OpenFeign Standardized:** If the `leave-service` needs employee metadata, it relies exclusively on `com.revworkforce.leaveservice.client.EmployeeClient` to execute a REST loopback request to `employee-service`.
3. **Database Security:** Each microservice has its own unique, isolated database schema instantiated from `init-databases.sql`.
