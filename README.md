# RevWorkforce Microservices Architecture - Run & Test Guide

This repository contains the completely modernized **Cloud-Native Microservices Edition** of the RevWorkforce HR Management System (Phase 3). 

The application has been decomposed from a massive Spring Boot Monolith into 9 secure, independently deployable bounded contexts using the Spring Cloud Stack and Domain-Driven Design principles.

---

## 🛠 Prerequisites for Local Deployment

To run this project locally on your machine, ensure you have the following installed:
1. **Docker Desktop** (Make sure the Docker Engine is running)
2. **Docker Compose**
3. **Java 17 (JDK)**
4. **Apache Maven 3.9+**
5. **Node.js (v18+) & Angular CLI**

---

## 🚀 Running the Application via Docker (Locally)

This project has been heavily optimized with multi-stage `Dockerfile` configurations and a monolithic `docker-compose.yml` file. Docker native networking handles everything automatically.

### Step 1: Initialize the Infrastructure
Open your terminal in the root `revworkforce` directory and run:

```bash
docker-compose up --build -d
```

**What this does natively behind the scenes:**
1. Spins up a centralized `MySQL 8.0` container on port `3307`.
2. Executes `init-databases.sql` to natively create 6 strictly isolated database schemas (`revworkforce_user_db`, `revworkforce_employee_db`, `revworkforce_leaves_db`, `revworkforce_performance_db`, `revworkforce_notifications_db`, `revworkforce_reporting_db`).
3. Uses Maven inside Docker to compile `common` shared libraries.
4. Compiles and launches all 9 backend microservices in strict dependency order (`config-server` -> `service-discovery` -> Domains -> `api-gateway`).

*Note: Allow ~60-90 seconds for all Spring Boot applications to finish booting natively. You can check logs using `docker-compose logs -f`.*

### Step 2: Boot the Angular Frontend
Because cloud deployment of frontends requires NGINX clusters, for local development, we spin up the Angular live server natively:

```bash
cd frontend
npm install
npm start
```
The application will be accessible at: **`http://localhost:4200`**

---

## 🧪 Testing the Microservices (Interview Requirement)

Because the databases have been cleanly isolated, unit tests must be executed strictly within their individual microservice domains.

### Running Backend Unit Tests
Open your terminal and navigate into the specific service folder you want to text (e.g. `leave-service`):

```bash
# Navigate into a specific microservice
cd leave-service

# Run its isolated test suite natively using Maven
mvn test
```

If you wish to test all backend services at once, execute this from the absolute root directory:
```bash
mvn clean test -am
```

### Verification Endpoints (Post-Boot)
Once the `docker-compose up -d` stack is green, you can verify the microservices are successfully communicating:
- **Eureka Service Registry:** `http://localhost:8761` (Verify all services are registered under instances).
- **API Gateway Entrypoint:** `http://localhost:8080/api/...` (All Angular UI traffic routes strictly through this port).

---

## 🔄 Rebuilding Services After Java Edits

If you make modifications to a Java file inside a service (e.g., `performance-service`), you do not need to reboot the entire infrastructure. Rebuild only that isolated container:

```bash
docker-compose down performance-service
docker-compose up --build -d performance-service
```

---

## 🗄️ Viewing All 6 Isolated Databases

Once `docker-compose up` is running, you can connect to the MySQL container and inspect all 6 isolated micro-databases.

### Option A: Via Docker Exec (No MySQL Client Needed)
Run this command in your terminal to open a MySQL shell **inside** the container:

```bash
docker exec -it revworkforce-mysql mysql -u root -proot
```

Once inside the MySQL shell, run:

```sql
-- Show all 6 isolated databases
SHOW DATABASES;

-- Inspect a specific service's database tables
USE revworkforce_user_db;
SHOW TABLES;

USE revworkforce_leaves_db;
SHOW TABLES;

USE revworkforce_performance_db;
SHOW TABLES;

USE revworkforce_employee_db;
SHOW TABLES;

USE revworkforce_notifications_db;
SHOW TABLES;

USE revworkforce_reporting_db;
SHOW TABLES;
```

### Option B: Via MySQL Workbench / DBeaver (GUI)
Connect a GUI client using these exact parameters:

| Property | Value |
|---|---|
| **Host** | `127.0.0.1` |
| **Port** | `3307` *(mapped to Docker container)* |
| **User** | `root` |
| **Password** | `root` |

After connecting, expand the **schemas panel** on the left — you will see all 6 isolated databases listed.
