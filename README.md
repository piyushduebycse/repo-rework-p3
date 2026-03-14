# RevWorkforce - HRM Management System

RevWorkforce is a modern, full-stack Human Resource Management (HRM) system built with a **microservices architecture**. It provides streamlined employee data management, leave tracking, performance reviews, goal management, and real-time notifications.

---

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.3, Spring Security (JWT), Spring Data JPA, MySQL 8
- **Frontend**: Angular 16, Angular Material, SCSS, Chart.js
- **Infrastructure**: Docker, Docker Compose, Nginx (API Gateway)
- **Architecture**: Microservices with REST APIs, JWT Authentication, Role-Based Access Control (RBAC)

---

## Microservices Architecture

The application is split into **4 independently deployable services** that communicate through a shared MySQL database and are fronted by an Nginx API Gateway.

```
                          +------------------+
                          |   Browser/Client |
                          +--------+---------+
                                   |
                                   | HTTP (port 80)
                                   v
                     +-------------+-------------+
                     |   Nginx (API Gateway)     |
                     |   + Angular Static Files  |
                     +---+-------+-------+-------+
                         |       |       |
            /api/auth/*  |       |       | /api/notifications/*
            /api/admin/* |       |       |
         /api/employees/*|       |       |
                         v       |       v
              +-----------+      |    +---------------------+
              | Auth &    |      |    | Notification        |
              | Employee  |      |    | Service             |
              | Service   |      |    | (port 8083)         |
              | (port 8081)|     |    +---------------------+
              +-----------+      |
                                 | /api/leaves/*
                                 | /api/performance/*
                                 | /api/goals/*
                                 | /api/manager/*
                                 v
                      +---------------------+
                      | Leave &             |
                      | Performance Service |
                      | (port 8082)         |
                      +---------------------+

              All services connect to:
                    +------------+
                    |  MySQL 8   |
                    | (port 3306)|
                    +------------+
```

### Service Breakdown

| Service | Port | Responsibility |
|---------|------|----------------|
| **auth-employee-service** | 8081 | User login/logout, JWT token generation, employee profiles, admin CRUD operations (add/update/deactivate employees), dashboard metrics |
| **leave-performance-service** | 8082 | Leave balance & applications, leave approval/rejection, performance reviews (draft/submit/feedback), goal CRUD, manager team operations |
| **notification-service** | 8083 | Create/read/mark notifications, unread count tracking |
| **common** | (shared library) | Shared DTOs, JWT security filters, exception handlers - packaged as a JAR dependency used by all services |
| **frontend** | 80 | Angular 16 SPA served by Nginx, which also acts as the API Gateway routing requests to the correct backend service |

### How the Microservices Work Together

1. **Shared Database**: All three services connect to the same MySQL database (`revworkforce_db`). This is a pragmatic approach that avoids the complexity of inter-service HTTP calls while still keeping services independently deployable.

2. **Shared JWT Authentication**: All services use the same JWT secret key, so a token issued by the auth-employee-service is valid across all services. The `common` module provides the shared `JwtAuthFilter` that each service plugs into its Spring Security chain.

3. **API Gateway (Nginx)**: The Angular frontend sends all API requests to the same origin (`/api/*`). Nginx inspects the URL path and routes each request to the correct backend microservice. The frontend code does not need to know about multiple backend services.

4. **Common Module**: Shared code (DTOs, security filters, exception handlers) lives in the `common` Maven module. Each microservice includes it as a dependency, ensuring consistent request/response formats and security behavior across all services.

5. **Notification Flow**: When a leave is approved or a review is submitted, the leave-performance-service writes a notification record directly to the `notifications` table. The notification-service reads from the same table to serve the frontend.

---

## Prerequisites

### Option A: Docker (Recommended - Easiest)

Only **one thing** needed:

- **Docker Desktop** (includes Docker Compose) - [Download here](https://www.docker.com/products/docker-desktop/)

### Option B: Manual Setup

1. **Java 17 JDK** (e.g., Microsoft OpenJDK 17 or Temurin)
2. **Apache Maven 3.6+**
3. **MySQL Server 8.0+**
4. **Node.js 18+** & **npm**
5. **Angular CLI** (`npm install -g @angular/cli`)

---

## Quick Start with Docker (Recommended)

This is the easiest way to run the entire application. One command starts everything.

### Step 1: Clone or Download the Project

```bash
git clone <repository-url>
cd revworkforce
```

Or if you received a ZIP file, extract it and navigate to the `revworkforce` folder.

### Step 2: Start Everything

```bash
docker-compose up --build
```

This will:
- Pull and start a MySQL 8 container
- Automatically run `schema.sql` to create the database, tables, and seed data
- Build and start all 3 backend microservices
- Build the Angular frontend and serve it via Nginx

### Step 3: Access the Application

Open your browser and go to:

```
http://localhost
```

That's it! The application is fully running.

### Stopping the Application

```bash
# Stop all containers (keeps data)
docker-compose down

# Stop and delete all data (fresh start)
docker-compose down -v
```

### Rebuilding After Code Changes

```bash
docker-compose up --build
```

---

## Manual Setup (Without Docker)

### 1. Database Setup

```bash
# Log into MySQL
mysql -u root -p

# Run the schema file
source schema.sql;
```

This creates the `revworkforce_db` database with all tables, departments, designations, leave types, and the default Admin user.

### 2. Start Auth & Employee Service (Port 8081)

```bash
cd auth-employee-service
mvn spring-boot:run
```

### 3. Start Leave & Performance Service (Port 8082)

```bash
cd leave-performance-service
mvn spring-boot:run
```

### 4. Start Notification Service (Port 8083)

```bash
cd notification-service
mvn spring-boot:run
```

### 5. Start Frontend (Port 4200)

```bash
cd frontend
npm install
ng serve
```

Access at `http://localhost:4200`

> **Note**: When running manually, the frontend at port 4200 talks to `http://localhost:8080/api` by default. You may need to update `frontend/src/environments/environment.ts` to point to the correct service ports, or run the monolithic `backend` service on port 8080 instead.

---

## Default Login Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | `admin@revworkforce.com` | `password` |

Once logged in as Admin, you can create Manager and Employee accounts through the Admin Dashboard. New employees are created with the default password: `Welcome@123`

| Role | Email | Password |
|------|-------|----------|
| **Employee** | `darshanraval@revworkforce.com` | `Welcome@123` |
| **Manager** | `prateekagrawal@revworkforce.com` | `Welcome@123` |

> These accounts are pre-seeded via `schema.sql`. The Admin can create additional users from the dashboard.

---

## Features

### Authentication & Security
- JWT-based stateless authentication
- Role-based access control (RBAC) with three roles: ADMIN, MANAGER, EMPLOYEE
- Secure password hashing with BCrypt
- Token shared across all microservices via common JWT secret

### Employee Management (Admin)
- Add, update, and deactivate/reactivate employees
- Assign departments, designations, and managers
- View dashboard metrics (total, active, inactive employees)

### Leave Management
- Apply for leave with multi-step stepper UI
- View leave balances (Casual, Sick, Paid Leave)
- Cancel pending/approved leaves
- View company holidays
- **Manager**: Approve/reject team leave requests with comments

### Performance Reviews
- Create and save review drafts
- Submit self-assessments with ratings
- **Manager**: Provide feedback and manager ratings

### Goal Tracking
- Create goals with priority levels and deadlines
- Track progress percentage
- **Manager**: View team goals and add comments

### Notifications
- Real-time notification bell with unread count
- Automatic notifications for leave approvals, rejections, review submissions
- Mark individual or all notifications as read

### Role-Specific Dashboards
- **Admin**: Employee management, system settings, metrics
- **Manager**: Team leave requests, performance reviews, goal oversight
- **Employee**: Personal dashboard, leave applications, self-reviews, goals

---

## Project Structure

```
revworkforce/
|
|-- common/                          # Shared library module
|   |-- src/main/java/.../common/
|       |-- dto/                     # Request & Response DTOs
|       |-- exception/               # Global exception handlers
|       |-- security/                # JWT filter, token provider, entry point
|
|-- auth-employee-service/           # Microservice: Auth + Employee + Admin
|   |-- src/main/java/.../authemployee/
|       |-- controller/              # AuthController, EmployeeController, AdminController
|       |-- entity/                  # Employee, Department, Designation, etc.
|       |-- repository/              # JPA repositories
|       |-- service/                 # Service interfaces + implementations
|       |-- config/                  # Security & CORS config
|   |-- Dockerfile
|
|-- leave-performance-service/       # Microservice: Leave + Performance + Goals
|   |-- src/main/java/.../leaveperformance/
|       |-- controller/              # LeaveController, PerformanceController, GoalController, ManagerController
|       |-- entity/                  # LeaveApplication, PerformanceReview, Goal, etc.
|       |-- repository/              # JPA repositories
|       |-- service/                 # Service interfaces + implementations
|       |-- config/                  # Security & CORS config
|   |-- Dockerfile
|
|-- notification-service/            # Microservice: Notifications
|   |-- src/main/java/.../notification/
|       |-- controller/              # NotificationController
|       |-- entity/                  # Notification
|       |-- repository/              # NotificationRepository
|       |-- service/                 # NotificationService + implementation
|       |-- config/                  # Security & CORS config
|   |-- Dockerfile
|
|-- frontend/                        # Angular 16 SPA
|   |-- src/app/
|       |-- core/                    # Guards, interceptors, services
|       |-- features/                # Feature modules (auth, dashboard, leave, performance)
|       |-- shared/                  # Shared components (sidebar, topbar, notifications)
|   |-- nginx.conf                   # API Gateway routing config
|   |-- Dockerfile
|
|-- backend/                         # Original monolithic backend (legacy reference)
|
|-- docker-compose.yml               # Orchestrates all services
|-- schema.sql                       # Database initialization script
|-- .dockerignore                    # Docker build exclusions
```

---

## API Endpoints

### Auth & Employee Service (port 8081)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | Login and get JWT token | Public |
| POST | `/api/auth/logout` | Logout | Authenticated |
| GET | `/api/auth/me` | Get current user profile | Authenticated |
| PUT | `/api/auth/me` | Update own profile | Authenticated |
| GET | `/api/employees/profile` | Get employee profile | Authenticated |
| GET | `/api/employees/manager` | Get assigned manager | Authenticated |
| GET | `/api/employees/directory` | List all active employees | Authenticated |
| GET | `/api/admin/employees` | List all employees (with filters) | ADMIN |
| POST | `/api/admin/employees` | Add new employee | ADMIN |
| PUT | `/api/admin/employees/{id}` | Update employee | ADMIN |
| PUT | `/api/admin/employees/{id}/deactivate` | Deactivate employee | ADMIN |
| PUT | `/api/admin/employees/{id}/reactivate` | Reactivate employee | ADMIN |
| GET | `/api/admin/dashboard/metrics` | Dashboard stats | ADMIN |

### Leave & Performance Service (port 8082)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/leaves/balance` | Get leave balances | Authenticated |
| POST | `/api/leaves/apply` | Apply for leave | Authenticated |
| GET | `/api/leaves/my` | Get my leave history | Authenticated |
| PUT | `/api/leaves/{id}/cancel` | Cancel leave | Authenticated |
| GET | `/api/leaves/holidays` | Get company holidays | Authenticated |
| POST | `/api/performance/review` | Save review draft | Authenticated |
| PUT | `/api/performance/review/{id}/submit` | Submit review | Authenticated |
| GET | `/api/performance/reviews` | Get my reviews | Authenticated |
| POST | `/api/goals` | Create goal | Authenticated |
| GET | `/api/goals` | Get my goals | Authenticated |
| PUT | `/api/goals/{id}` | Update goal | Authenticated |
| DELETE | `/api/goals/{id}` | Delete goal | Authenticated |
| GET | `/api/manager/leaves/team` | Get team leaves | MANAGER/ADMIN |
| PUT | `/api/manager/leaves/{id}/approve` | Approve leave | MANAGER/ADMIN |
| PUT | `/api/manager/leaves/{id}/reject` | Reject leave | MANAGER/ADMIN |
| GET | `/api/manager/performance/reviews` | Get team reviews | MANAGER/ADMIN |
| PUT | `/api/manager/performance/reviews/{id}/feedback` | Give feedback | MANAGER/ADMIN |
| GET | `/api/manager/goals/{empId}` | Get team member goals | MANAGER/ADMIN |
| PUT | `/api/manager/goals/{id}/comment` | Comment on goal | MANAGER/ADMIN |

### Notification Service (port 8083)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/notifications` | Get all notifications | Authenticated |
| GET | `/api/notifications/unread` | Get unread notifications | Authenticated |
| PUT | `/api/notifications/{id}/read` | Mark as read | Authenticated |
| PUT | `/api/notifications/read-all` | Mark all as read | Authenticated |
| POST | `/api/notifications` | Create notification | Authenticated |

---

## How to Share This Project

### Option 1: Share via Git Repository

```bash
# Initialize git (if not already)
cd revworkforce
git init
git add .
git commit -m "RevWorkforce microservices with Docker"

# Push to GitHub/GitLab
git remote add origin https://github.com/your-username/revworkforce.git
git push -u origin main
```

The other person just needs to:
```bash
git clone https://github.com/your-username/revworkforce.git
cd revworkforce
docker-compose up --build
# Open http://localhost
```

### Option 2: Share as ZIP File

1. Make sure you **exclude** these folders before zipping (they are large and auto-generated):
   - `node_modules/` (in frontend)
   - `target/` (in each Java module)
   - `.angular/` (in frontend)

2. Zip the `revworkforce` folder
3. Share the ZIP file
4. The recipient extracts it and runs:
   ```bash
   cd revworkforce
   docker-compose up --build
   ```

### What the Recipient Needs

- **Docker Desktop** installed - that's the only requirement
- No need to install Java, Maven, Node.js, MySQL, or Angular CLI
- Docker handles everything automatically

---

## Environment Variables

All services support configuration via environment variables (set in `docker-compose.yml`):

| Variable | Default | Description |
|----------|---------|-------------|
| `MYSQL_HOST` | `localhost` | MySQL server hostname |
| `MYSQL_PORT` | `3306` | MySQL server port |
| `MYSQL_USER` | `root` | MySQL username |
| `MYSQL_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | (base64 key) | JWT signing secret (must be same across all services) |
| `SERVER_PORT` | varies per service | HTTP port the service listens on |
| `CORS_ORIGINS` | `http://localhost:4200` | Allowed CORS origins |

---

## Troubleshooting

### Docker Issues

**Port already in use:**
```bash
# Check what's using port 80, 8081, 8082, 8083, or 3307
netstat -ano | findstr :80
# Stop the conflicting process or change the port in docker-compose.yml
```

**MySQL not ready errors:**
The services have `depends_on` with health checks, but if you still see connection errors, wait 30 seconds and the services will auto-restart and connect.

**Fresh start (reset everything):**
```bash
docker-compose down -v
docker-compose up --build
```

### Manual Setup Issues

**Maven build fails:**
```bash
# Build common module first
cd common
mvn clean install -DskipTests

# Then build any service
cd ../auth-employee-service
mvn clean package -DskipTests
```

**Frontend CORS errors:**
Make sure the backend service has the correct CORS origins configured in `application.properties`.

---

## Build & Testing

```bash
# Build all backend modules
mvn clean package -DskipTests

# Run backend tests
mvn test

# Frontend production build
cd frontend
ng build --configuration production
```

---

## License

This project is for educational and demonstration purposes.
