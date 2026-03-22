INSERT INTO leave_types (name, default_quota) VALUES ('Casual Leave', 12), ('Sick Leave', 10), ('Paid Leave', 15)
ON DUPLICATE KEY UPDATE name=name;
