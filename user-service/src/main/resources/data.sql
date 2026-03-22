INSERT INTO users (email, password, role) 
VALUES ('admin@revworkforce.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN')
ON DUPLICATE KEY UPDATE email=email;
