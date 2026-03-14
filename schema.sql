CREATE DATABASE IF NOT EXISTS revworkforce_db;
USE revworkforce_db;

-- DEPARTMENTS
CREATE TABLE departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- DESIGNATIONS
CREATE TABLE designations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- EMPLOYEES
CREATE TABLE employees (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id VARCHAR(20) NOT NULL UNIQUE,       -- e.g., EMP001
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,                -- BCrypt hashed
  phone VARCHAR(20),
  address TEXT,
  emergency_contact VARCHAR(100),
  role ENUM('EMPLOYEE','MANAGER','ADMIN') NOT NULL DEFAULT 'EMPLOYEE',
  department_id BIGINT,
  designation_id BIGINT,
  manager_id BIGINT,                             -- self-reference
  joining_date DATE,
  salary DECIMAL(12,2),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (department_id) REFERENCES departments(id),
  FOREIGN KEY (designation_id) REFERENCES designations(id),
  FOREIGN KEY (manager_id) REFERENCES employees(id)
);

-- LEAVE TYPES
CREATE TABLE leave_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,              -- Casual, Sick, Paid
  default_quota INT NOT NULL DEFAULT 0,
  is_active BOOLEAN DEFAULT TRUE
);

-- LEAVE BALANCES
CREATE TABLE leave_balances (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  year INT NOT NULL,
  total_days INT NOT NULL DEFAULT 0,
  used_days INT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_emp_type_year (employee_id, leave_type_id, year),
  FOREIGN KEY (employee_id) REFERENCES employees(id),
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

-- LEAVE APPLICATIONS
CREATE TABLE leave_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT,
  status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
  manager_comment TEXT,
  applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employees(id),
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

-- COMPANY HOLIDAYS
CREATE TABLE company_holidays (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  year INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PERFORMANCE REVIEWS
CREATE TABLE performance_reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  review_year INT NOT NULL,
  key_deliverables TEXT,
  accomplishments TEXT,
  areas_of_improvement TEXT,
  self_assessment_rating INT CHECK (self_assessment_rating BETWEEN 1 AND 5),
  status ENUM('DRAFT','SUBMITTED','REVIEWED') DEFAULT 'DRAFT',
  manager_feedback TEXT,
  manager_rating INT CHECK (manager_rating BETWEEN 1 AND 5),
  submitted_at TIMESTAMP,
  reviewed_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- GOALS
CREATE TABLE goals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  description TEXT NOT NULL,
  deadline DATE,
  priority ENUM('HIGH','MEDIUM','LOW') DEFAULT 'MEDIUM',
  status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
  progress_percentage INT DEFAULT 0,
  manager_comment TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- NOTIFICATIONS
CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  recipient_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  type ENUM('LEAVE_APPLIED','LEAVE_APPROVED','LEAVE_REJECTED','REVIEW_SUBMITTED','REVIEW_FEEDBACK') NOT NULL,
  is_read BOOLEAN DEFAULT FALSE,
  reference_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (recipient_id) REFERENCES employees(id)
);

-- ANNOUNCEMENTS
CREATE TABLE announcements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  created_by BIGINT NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (created_by) REFERENCES employees(id)
);

-- ACTIVITY LOGS
CREATE TABLE activity_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT,
  action VARCHAR(200) NOT NULL,
  details TEXT,
  ip_address VARCHAR(45),
  performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SEED DATA
INSERT INTO departments (name) VALUES ('Engineering'), ('HR'), ('Finance'), ('Operations');
INSERT INTO designations (title) VALUES ('Software Engineer'), ('HR Manager'), ('Team Lead'), ('Admin');
INSERT INTO leave_types (name, default_quota) VALUES ('Casual Leave', 12), ('Sick Leave', 10), ('Paid Leave', 15);

-- Admin user (password: password)
INSERT INTO employees (employee_id, first_name, last_name, email, password, role, department_id, designation_id, joining_date)
VALUES ('ADMIN001', 'System', 'Admin', 'admin@revworkforce.com',
        '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
        'ADMIN', 1, 4, '2024-01-01');
