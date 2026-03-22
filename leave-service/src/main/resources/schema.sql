CREATE TABLE IF NOT EXISTS leave_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE,
  default_quota INT NOT NULL DEFAULT 0,
  is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS leave_balances (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  year INT NOT NULL,
  total_days INT NOT NULL DEFAULT 0,
  used_days INT NOT NULL DEFAULT 0,
  UNIQUE KEY uq_emp_type_year (employee_id, leave_type_id, year),
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

CREATE TABLE IF NOT EXISTS leave_applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  reason TEXT,
  status ENUM('PENDING','APPROVED','REJECTED','CANCELLED') DEFAULT 'PENDING',
  manager_comment TEXT,
  applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (leave_type_id) REFERENCES leave_types(id)
);

CREATE TABLE IF NOT EXISTS company_holidays (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  year INT NOT NULL
);
