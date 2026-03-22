-- initialize all isolated microservice schemas
CREATE DATABASE IF NOT EXISTS revworkforce_user_db;
CREATE DATABASE IF NOT EXISTS revworkforce_employee_db;
CREATE DATABASE IF NOT EXISTS revworkforce_leaves_db;
CREATE DATABASE IF NOT EXISTS revworkforce_performance_db;
CREATE DATABASE IF NOT EXISTS revworkforce_notifications_db;
CREATE DATABASE IF NOT EXISTS revworkforce_reporting_db;

-- Grand privileges to root (default local dev behavior)
GRANT ALL PRIVILEGES ON revworkforce_user_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revworkforce_employee_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revworkforce_leaves_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revworkforce_performance_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revworkforce_notifications_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON revworkforce_reporting_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
