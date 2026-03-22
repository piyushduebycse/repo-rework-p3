-- ==========================================================
-- REVWORKFORCE MICROSERVICES MASTER SCHEMA REFERENCE
-- ==========================================================
-- This file documents the 6 isolated database schemas used in the 
-- Cloud-Native Microservices migration. Traditional SQL Joins 
-- have been replaced by OpenFeign Inter-Service Communication.
-- ==========================================================

-- 1. USER & AUTHENTICATION SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_user_db;
USE revworkforce_user_db;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,                -- BCrypt hashed
  role ENUM('EMPLOYEE','MANAGER','ADMIN') NOT NULL DEFAULT 'EMPLOYEE',
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------

-- 2. EMPLOYEE MANAGEMENT SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_employee_db;
USE revworkforce_employee_db;

CREATE TABLE departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE designations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE employees (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id VARCHAR(20) NOT NULL UNIQUE,       -- EMP001
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  phone VARCHAR(20),
  department_id BIGINT,
  designation_id BIGINT,
  manager_id BIGINT,                             -- self-reference (logical)
  joining_date DATE,
  salary DECIMAL(12,2),
  is_active BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (department_id) REFERENCES departments(id),
  FOREIGN KEY (designation_id) REFERENCES designations(id)
);

-- ----------------------------------------------------------

-- 3. LEAVE SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_leaves_db;
USE revworkforce_leaves_db;

CREATE TABLE leave_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  default_quota INT NOT NULL DEFAULT 0,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE leave_balances (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,                  -- Logical Reference to Employee Service
  leave_type_id BIGINT NOT NULL,
  year INT NOT NULL,
  total_days INT NOT NULL DEFAULT 0,
  used_days INT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_emp_type_year (employee_id, leave_type_id, year),
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

CREATE TABLE leave_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,                  -- Logical Reference
  leave_type_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT,
  status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
  manager_comment TEXT,
  applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

CREATE TABLE company_holidays (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  year INT NOT NULL
);

-- ----------------------------------------------------------

-- 4. PERFORMANCE SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_performance_db;
USE revworkforce_performance_db;

CREATE TABLE performance_reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,                  -- Logical Reference
  review_year INT NOT NULL,
  key_deliverables TEXT,
  accomplishments TEXT,
  areas_of_improvement TEXT,
  self_assessment_rating INT CHECK (self_assessment_rating BETWEEN 1 AND 5),
  status ENUM('DRAFT','SUBMITTED','REVIEWED') DEFAULT 'DRAFT',
  manager_feedback TEXT,
  manager_rating INT CHECK (manager_rating BETWEEN 1 AND 5),
  submitted_at TIMESTAMP,
  reviewed_at TIMESTAMP
);

CREATE TABLE goals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,                  -- Logical Reference
  description TEXT NOT NULL,
  deadline DATE,
  priority ENUM('HIGH','MEDIUM','LOW') DEFAULT 'MEDIUM',
  status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
  progress_percentage INT DEFAULT 0,
  manager_comment TEXT
);

-- ----------------------------------------------------------

-- 5. NOTIFICATION SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_notifications_db;
USE revworkforce_notifications_db;

CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  recipient_id BIGINT NOT NULL,                 -- Logical Reference
  message TEXT NOT NULL,
  type ENUM('LEAVE_APPLIED','LEAVE_APPROVED','LEAVE_REJECTED','REVIEW_SUBMITTED','REVIEW_FEEDBACK') NOT NULL,
  is_read BOOLEAN DEFAULT FALSE,
  reference_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------

-- 6. REPORTING SERVICE DB
CREATE DATABASE IF NOT EXISTS revworkforce_reporting_db;
USE revworkforce_reporting_db;

-- This service primarily aggregates data from other services via OpenFeign.
-- Persistence is used for historical snapshotting or cached analytics.

-- ----------------------------------------------------------

-- GLOBAL SEED DATA (For Local Microservices Setup)

-- Seed Departments/Designations (Employee Service)
USE revworkforce_employee_db;
INSERT INTO departments (name) VALUES ('Engineering'), ('HR'), ('Finance'), ('Operations');
INSERT INTO designations (title) VALUES ('Software Engineer'), ('HR Manager'), ('Team Lead'), ('Admin');

-- Seed Leave Types (Leave Service)
USE revworkforce_leaves_db;
INSERT INTO leave_types (name, default_quota) VALUES ('Casual Leave', 12), ('Sick Leave', 10), ('Paid Leave', 15);

-- Seed Initial Admin (User Service & Employee Service)
USE revworkforce_user_db;
INSERT INTO users (email, password, role) 
VALUES ('admin@revworkforce.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');

USE revworkforce_employee_db;
INSERT INTO employees (employee_id, first_name, last_name, email, role, department_id, designation_id, joining_date)
VALUES ('ADMIN001', 'System', 'Admin', 'admin@revworkforce.com', 'ADMIN', 1, 4, '2024-01-01');
