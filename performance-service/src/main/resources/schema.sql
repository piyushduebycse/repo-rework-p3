CREATE TABLE IF NOT EXISTS performance_reviews (
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
  reviewed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS goals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  description TEXT NOT NULL,
  deadline DATE,
  priority ENUM('HIGH','MEDIUM','LOW') DEFAULT 'MEDIUM',
  status ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
  progress_percentage INT DEFAULT 0,
  manager_comment TEXT
);
