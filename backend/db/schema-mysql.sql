-- MySQL schema for Campus JobFair
SET NAMES utf8mb4;
SET foreign_key_checks = 0;

CREATE DATABASE IF NOT EXISTS jobfair
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE jobfair;

-- Companies and students
CREATE TABLE IF NOT EXISTS companies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  name VARCHAR(200) NOT NULL,
  credit_code VARCHAR(50) NOT NULL,
  scale VARCHAR(50),
  industry VARCHAR(50),
  description VARCHAR(500),
  logo_url VARCHAR(255),
  contact_name VARCHAR(100),
  contact_phone VARCHAR(50),
  contact_email VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  rejection_reason VARCHAR(200),
  CONSTRAINT uk_companies_name UNIQUE (name),
  CONSTRAINT uk_companies_credit UNIQUE (credit_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS students (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  student_no VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  college VARCHAR(100),
  phone VARCHAR(50),
  email VARCHAR(100),
  default_resume_id BIGINT,
  CONSTRAINT uk_students_no UNIQUE (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Job fairs and booths
CREATE TABLE IF NOT EXISTS job_fairs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  name VARCHAR(150) NOT NULL,
  location VARCHAR(300),
  start_time DATETIME(6),
  end_time DATETIME(6),
  description VARCHAR(500),
  capacity INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS resumes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  student_id BIGINT NOT NULL,
  title VARCHAR(150) NOT NULL,
  summary VARCHAR(500),
  education VARCHAR(500),
  experience VARCHAR(500),
  skills VARCHAR(200),
  is_default TINYINT(1) NOT NULL DEFAULT 0,
  last_edited_at DATETIME(6),
  KEY idx_resume_student (student_id),
  CONSTRAINT fk_resume_student FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Jobs and events
CREATE TABLE IF NOT EXISTS jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  title VARCHAR(150) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  salary_range VARCHAR(100),
  headcount INT,
  skills VARCHAR(200),
  location VARCHAR(100),
  job_type VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
  publish_at DATETIME(6),
  deadline DATETIME(6),
  company_id BIGINT,
  KEY idx_job_company (company_id),
  CONSTRAINT fk_job_company FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS job_fair_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  job_fair_id BIGINT,
  name VARCHAR(150) NOT NULL,
  type VARCHAR(30) NOT NULL,
  location VARCHAR(300),
  start_time DATETIME(6),
  end_time DATETIME(6),
  capacity INT,
  description VARCHAR(300),
  KEY idx_event_jobfair (job_fair_id),
  CONSTRAINT fk_event_jobfair FOREIGN KEY (job_fair_id) REFERENCES job_fairs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS booths (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  job_fair_id BIGINT,
  company_id BIGINT,
  booth_no VARCHAR(50) NOT NULL,
  location VARCHAR(100),
  checked_in TINYINT(1) NOT NULL DEFAULT 0,
  checkin_time DATETIME(6),
  KEY idx_booth_jobfair (job_fair_id),
  KEY idx_booth_company (company_id),
  CONSTRAINT fk_booth_jobfair FOREIGN KEY (job_fair_id) REFERENCES job_fairs(id),
  CONSTRAINT fk_booth_company FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Resume attachments and files
CREATE TABLE IF NOT EXISTS resume_files (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  resume_id BIGINT NOT NULL,
  file_name VARCHAR(200) NOT NULL,
  url VARCHAR(255) NOT NULL,
  content_type VARCHAR(100),
  size BIGINT,
  KEY idx_resume_file_resume (resume_id),
  CONSTRAINT fk_resume_file_resume FOREIGN KEY (resume_id) REFERENCES resumes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS file_resources (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  owner_type VARCHAR(50) NOT NULL,
  owner_id BIGINT,
  file_name VARCHAR(200) NOT NULL,
  url VARCHAR(255) NOT NULL,
  content_type VARCHAR(100),
  size BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Applications and favorites
CREATE TABLE IF NOT EXISTS applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  job_id BIGINT,
  student_id BIGINT,
  resume_id BIGINT,
  status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED',
  notes VARCHAR(500),
  withdrawn_at DATETIME(6),
  KEY idx_app_job (job_id),
  KEY idx_app_student (student_id),
  KEY idx_app_resume (resume_id),
  CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_app_resume FOREIGN KEY (resume_id) REFERENCES resumes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorite_companies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  student_id BIGINT,
  company_id BIGINT,
  KEY idx_fav_company_student (student_id),
  KEY idx_fav_company_company (company_id),
  CONSTRAINT fk_fav_company_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_fav_company_company FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorite_jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  student_id BIGINT,
  job_id BIGINT,
  job_status_snapshot VARCHAR(50),
  KEY idx_fav_job_student (student_id),
  KEY idx_fav_job_job (job_id),
  CONSTRAINT fk_fav_job_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_fav_job_job FOREIGN KEY (job_id) REFERENCES jobs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notifications, interviews, reviews
CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  recipient_role VARCHAR(20) NOT NULL,
  recipient_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content VARCHAR(1000) NOT NULL,
  type VARCHAR(30) NOT NULL DEFAULT 'SYSTEM',
  read_flag TINYINT(1) NOT NULL DEFAULT 0,
  read_at DATETIME(6),
  KEY idx_notification_recipient (recipient_role, recipient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS interviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  job_id BIGINT,
  student_id BIGINT,
  scheduled_at DATETIME(6),
  location VARCHAR(200),
  interviewer VARCHAR(100),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  feedback VARCHAR(500),
  KEY idx_interview_job (job_id),
  KEY idx_interview_student (student_id),
  CONSTRAINT fk_interview_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_interview_student FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  company_id BIGINT,
  student_id BIGINT,
  rating INT,
  comment VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE',
  KEY idx_review_company (company_id),
  KEY idx_review_student (student_id),
  CONSTRAINT fk_review_company FOREIGN KEY (company_id) REFERENCES companies(id),
  CONSTRAINT fk_review_student FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Announcements and registrations
CREATE TABLE IF NOT EXISTS announcements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  title VARCHAR(200) NOT NULL,
  content VARCHAR(2000) NOT NULL,
  target VARCHAR(20) NOT NULL DEFAULT 'ALL',
  publish_at DATETIME(6),
  expire_at DATETIME(6),
  pinned TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS event_registrations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  event_id BIGINT,
  student_id BIGINT,
  status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
  checkin_time DATETIME(6),
  seat_no VARCHAR(50),
  KEY idx_event_reg_event (event_id),
  KEY idx_event_reg_student (student_id),
  CONSTRAINT fk_event_reg_event FOREIGN KEY (event_id) REFERENCES job_fair_events(id),
  CONSTRAINT fk_event_reg_student FOREIGN KEY (student_id) REFERENCES students(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Export tasks and audit logs
CREATE TABLE IF NOT EXISTS export_tasks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  type VARCHAR(30) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  requested_by_role VARCHAR(20) NOT NULL,
  requested_by_id BIGINT NOT NULL,
  from_date DATETIME(6),
  to_date DATETIME(6),
  file_url VARCHAR(255),
  completed_at DATETIME(6),
  error_message VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  actor_role VARCHAR(20) NOT NULL,
  actor_id BIGINT NOT NULL,
  action VARCHAR(200) NOT NULL,
  detail VARCHAR(1000),
  success TINYINT(1) NOT NULL DEFAULT 0,
  ip VARCHAR(50),
  KEY idx_audit_actor (actor_role, actor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User accounts (after students/companies exist)
CREATE TABLE IF NOT EXISTS user_accounts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  username VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  student_id BIGINT,
  company_id BIGINT,
  CONSTRAINT uk_user_accounts_username UNIQUE (username),
  KEY idx_user_student (student_id),
  KEY idx_user_company (company_id),
  CONSTRAINT fk_user_student FOREIGN KEY (student_id) REFERENCES students(id),
  CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES companies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Cross-table constraint for students -> default resume (circular with resumes)
ALTER TABLE students
  ADD CONSTRAINT fk_students_default_resume
  FOREIGN KEY (default_resume_id) REFERENCES resumes(id)
  ON DELETE SET NULL;

SET foreign_key_checks = 1;
