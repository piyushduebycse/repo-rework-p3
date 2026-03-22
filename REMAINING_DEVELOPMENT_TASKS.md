# Phase 3: Remaining Manual Development Tasks

This document highlights the exact **Java Programming** tasks required by developers to finalize the RevWorkforce Microservices Modernization. 

*Note: 100% of the architectural scaffolding, Docker configurations, POM dependencies, Database routing, API Gateway configurations, and GitHub CI/CD Pipelines have been completed. The remaining work revolves strictly around bridging cross-domain Java logic errors.*

---

## 1. Resolve Cross-Domain Compilation Errors (OpenFeign)
Because the monolithic `Leave` and `Performance` files were forcefully moved into isolated microservices (`leave-service` and `performance-service`), they are currently trying to execute local SQL lookups on the `Employee` table. Since `leave_db` and `employee_db` are now physically separated, their Repositories will fail to compile.

**Action Required:**
- You must delete all `employeeRepository.findById()` calls inside the Leave and Performance services. 
- Replace them with OpenFeign HTTP calls (e.g., injecting an `@FeignClient` that asks the `employee-service` for the employee's data).
- *Reference the `UserClient.java` interface inside `employee-service` for the exact structural pattern.*

---

## 2. Standardize Shared DTOs in the `common` Library
When the Leave Service asks the Employee Service for a user's name via OpenFeign, it will return JSON. The Leave Service needs a Java object to map that JSON into.

**Action Required:**
- You need to create `EmployeeDTO`, `LeaveDTO`, and `PerformanceDTO` classes inside the **`common/src/main/java/com/revworkforce/common/dto`** folder.
- This acts as a shared contract so all 6 microservices can safely serialize and deserialize the OpenFeign JSON payloads without duplicating class definitions.

---

## 3. Build the HR Reporting Service
The `reporting-service` has been fully scaffolded, but its `src/main/java` folder is currently empty. The P3 scope requires an analytics dashboard.

**Action Required:**
- Write a `@RestController` in the Reporting Service that uses Feign Clients to fetch data from the Leave Service (e.g., *Total Leaves Today*) and Employee Service (e.g., *Total Active Headcount*). 
- It must aggregate this data into a single JSON response map for the Angular frontend's Admin charts.

---

## 4. Update Angular API Routes
The `api-gateway` (Port `8080`) is now the sole entry point for your application. The Angular application currently targets the old `auth-employee-service` and `leave-performance-service` domains/ports directly.

**Action Required:**
- Open your Angular frontend codebase. 
- Change the base URL in `environment.ts` and `environment.prod.ts` to point strictly to `http://localhost:8080/api/` (or your production Gateway IP). 
- Ensure all the Angular HTTP Services match the newly split backend routes.

---

## 5. Validate Distributed Security
**Action Required:**
- Ensure the `common` module's `JwtAuthFilter` successfully authenticates the bearer token on every single microservice request. 
- Because you are using a shared JWT Secret defined in the Config Server/Environment Variables, you simply need to verify that tokens minted by `user-service` are successfully decoded by `leave-service` when making secure `POST`/`PUT` requests.
