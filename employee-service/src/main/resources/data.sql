INSERT INTO departments (name) VALUES ('Engineering'), ('HR'), ('Finance'), ('Operations')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO designations (title) VALUES ('Software Engineer'), ('HR Manager'), ('Team Lead'), ('Admin')
ON DUPLICATE KEY UPDATE title=title;

INSERT INTO employees (employee_id, first_name, last_name, email, role, department_id, designation_id, joining_date)
VALUES ('ADMIN001', 'System', 'Admin', 'admin@revworkforce.com', 'ADMIN', 1, 4, '2024-01-01')
ON DUPLICATE KEY UPDATE email=email;
