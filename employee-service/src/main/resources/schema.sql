CREATE TABLE IF NOT EXISTS departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS designations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL UNIQUE,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS employees (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id VARCHAR(20) NOT NULL UNIQUE,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  phone VARCHAR(20),
  department_id BIGINT,
  designation_id BIGINT,
  manager_id BIGINT,
  joining_date DATE,
  salary DECIMAL(12,2),
  is_active BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (department_id) REFERENCES departments(id),
  FOREIGN KEY (designation_id) REFERENCES designations(id)
);
