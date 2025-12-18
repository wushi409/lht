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
  license_url VARCHAR(255),
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
  job_intent VARCHAR(200),
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
  status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED',
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
  checkin_code VARCHAR(8),
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
  status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
  notes VARCHAR(500),
  withdrawn_at DATETIME(6),
  tag VARCHAR(50),
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

-- Notifications and interviews
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
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  feedback VARCHAR(500),
  KEY idx_interview_job (job_id),
  KEY idx_interview_student (student_id),
  CONSTRAINT fk_interview_job FOREIGN KEY (job_id) REFERENCES jobs(id),
  CONSTRAINT fk_interview_student FOREIGN KEY (student_id) REFERENCES students(id)
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

-- Export tasks
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

-- Verification codes for login and password reset
CREATE TABLE IF NOT EXISTS verification_codes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  username VARCHAR(100) NOT NULL,
  scene VARCHAR(30) NOT NULL,
  code VARCHAR(10) NOT NULL,
  expire_at DATETIME(6) NOT NULL,
  used TINYINT(1) NOT NULL DEFAULT 0,
  KEY idx_verification_user_scene (username, scene)
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

INSERT INTO companies (name, credit_code, scale, industry, description, logo_url, contact_name, contact_phone, contact_email, status) VALUES
('演示科技（SQL）有限公司','91310000MA1K12345X','200-500人','互联网','SQL 脚本插入的示例企业',NULL,'HR','13800000000','hr@example.com','APPROVED'),
('未来信息技术有限公司','91310000MA1K67890Y','50-200人','软件','专注校园招聘与培养',NULL,'王敏','13900000000','hr2@example.com','APPROVED'),
('阿里云计算有限公司','91330000MA27XYZ123','1000人以上','云计算','领先的云计算服务提供商',NULL,'张华','13800111111','recruit@alicloud.com','APPROVED'),
('腾讯科技（深圳）有限公司','91440300708461136T','1000人以上','互联网','连接一切的互联网公司',NULL,'李娜','13800222222','hr@tencent.com','APPROVED'),
('字节跳动科技有限公司','91110108MA01ABCD12','1000人以上','互联网','激发创造，丰富生活',NULL,'刘强','13800333333','campus@bytedance.com','APPROVED'),
('华为技术有限公司','91440300618520432Y','1000人以上','通信设备','构建万物互联的智能世界',NULL,'陈敏','13800444444','hr@huawei.com','APPROVED'),
('美团科技有限公司','91110108MA00EFGH34','1000人以上','互联网','帮大家吃得更好，生活更好',NULL,'赵丽','13800555555','campus@meituan.com','APPROVED'),
('京东集团','91110000633574694Y','1000人以上','电商','多快好省的购物体验',NULL,'孙伟','13800666666','hr@jd.com','APPROVED'),
('网易杭州网络有限公司','91330100MA27IJKL56','500-1000人','互联网游戏','网聚人的力量',NULL,'周芳','13800777777','recruit@netease.com','APPROVED'),
('小米科技有限责任公司','91110108MA00MNOP78','1000人以上','智能硬件','让每个人都能享受科技的乐趣',NULL,'吴刚','13800888888','hr@xiaomi.com','APPROVED');

INSERT INTO students (student_no, name, college, phone, email, default_resume_id) VALUES
('20259999','示例同学SQL','计算机学院','13800000001','student@example.com',NULL),
('20250002','李四','信息工程学院','13800000002','stu2@example.com',NULL),
('20250003','王小明','计算机学院','13900000001','wangxm@edu.cn',NULL),
('20250004','张丽丽','软件学院','13900000002','zhangli@edu.cn',NULL),
('20250005','刘建国','信息工程学院','13900000003','liujg@edu.cn',NULL),
('20250006','陈思思','计算机学院','13900000004','chenss@edu.cn',NULL),
('20250007','赵云飞','软件学院','13900000005','zhaoyf@edu.cn',NULL),
('20250008','孙美美','数据科学学院','13900000006','sunmm@edu.cn',NULL),
('20250009','周杰','计算机学院','13900000007','zhouj@edu.cn',NULL),
('20250010','吴倩倩','软件学院','13900000008','wuqq@edu.cn',NULL),
('20250011','郑浩','信息工程学院','13900000009','zhengh@edu.cn',NULL),
('20250012','冯婷婷','计算机学院','13900000010','fengtt@edu.cn',NULL),
('20250013','何伟','数据科学学院','13900000011','hew@edu.cn',NULL),
('20250014','许静','软件学院','13900000012','xuj@edu.cn',NULL),
('20250015','韩磊','计算机学院','13900000013','hanl@edu.cn',NULL);

INSERT INTO job_fairs (name, location, start_time, end_time, description, capacity) VALUES
('2025春季校园双选会','大学体育馆','2025-03-20 09:00:00','2025-03-20 17:00:00','校园招聘双选会',500),
('2025夏季实习生专场','学生活动中心','2025-05-15 09:00:00','2025-05-15 17:00:00','面向大三学生的实习招聘',300),
('2025秋季校园招聘会','体育场','2025-09-10 09:00:00','2025-09-10 18:00:00','秋季大型校园招聘',800);

-- 使用变量存储学生ID，避免子查询更新冲突
SET @student_id = (SELECT id FROM students WHERE student_no='20259999' LIMIT 1);
INSERT INTO resumes (student_id, title, summary, education, experience, skills, is_default, last_edited_at) 
VALUES (@student_id, '张三-简历', 'Java/前端/校园项目', '计算机科学与技术 本科', '多个校园项目', 'Java, SpringBoot, Vue', 1, NOW(6));

-- 更新默认简历ID，使用子查询替代ORDER BY
UPDATE students s
JOIN (
    SELECT id, student_id 
    FROM resumes 
    WHERE student_id = @student_id
    ORDER BY id DESC 
    LIMIT 1
) r ON r.student_id = s.id
SET s.default_resume_id = r.id 
WHERE s.student_no = '20259999';

-- 为其他学生添加简历
INSERT INTO resumes (student_id, title, summary, education, experience, skills, is_default, last_edited_at) VALUES
((SELECT id FROM students WHERE student_no='20250003'), '王小明-Java开发简历', '熟悉Java后端开发', '计算机科学与技术 本科', '参与学校实验室项目', 'Java, Spring Boot, MySQL, Redis', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250004'), '张丽丽-前端简历', '专注前端开发', '软件工程 本科', '多个前端项目经验', 'Vue, React, TypeScript, CSS', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250005'), '刘建国-全栈简历', '全栈开发工程师', '信息工程 本科', '全栈项目开发', 'Java, Vue, MySQL, Docker', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250006'), '陈思思-算法简历', '算法与数据分析', '计算机科学与技术 本科', '机器学习项目', 'Python, 机器学习, 数据分析', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250007'), '赵云飞-后端简历', 'Go语言后端开发', '软件工程 本科', '微服务项目', 'Go, Docker, Kubernetes, MySQL', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250008'), '孙美美-数据分析简历', '数据科学与分析', '数据科学 本科', '数据挖掘项目', 'Python, SQL, 数据可视化, 机器学习', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250009'), '周杰-移动开发简历', 'Android开发工程师', '计算机科学与技术 本科', 'Android应用开发', 'Java, Kotlin, Android, MVVM', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250010'), '吴倩倩-测试简历', '测试开发工程师', '软件工程 本科', '自动化测试', 'Python, Selenium, 自动化测试', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250011'), '郑浩-C++简历', 'C++开发工程师', '信息工程 本科', '系统开发项目', 'C++, Linux, 网络编程', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250012'), '冯婷婷-前端简历', 'React前端开发', '计算机科学与技术 本科', 'React项目开发', 'React, TypeScript, Next.js, Tailwind', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250013'), '何伟-大数据简历', '大数据开发工程师', '数据科学 本科', '大数据处理项目', 'Spark, Hadoop, Hive, Python', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250014'), '许静-UI设计简历', 'UI/UX设计师', '软件工程 本科', '多个设计项目', 'Figma, Sketch, 前端基础', 1, NOW(6)),
((SELECT id FROM students WHERE student_no='20250015'), '韩磊-游戏开发简历', '游戏开发工程师', '计算机科学与技术 本科', 'Unity游戏开发', 'Unity, C#, 游戏设计', 1, NOW(6));

INSERT INTO jobs (company_id, title, description, salary_range, headcount, skills, location, job_type, status, publish_at, deadline) VALUES
((SELECT id FROM companies WHERE credit_code='91310000MA1K12345X'),'Java后端实习生','参与后台开发与接口联调，熟悉Spring Boot','8k-12k',5,'Java,Spring,MySQL','上海','实习','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 30 DAY)),
((SELECT id FROM companies WHERE credit_code='91310000MA1K12345X'),'前端工程师（校招）','负责前端页面开发与组件封装','10k-15k',3,'Vue,TypeScript,Element Plus','上海','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91330000MA27XYZ123'),'云计算开发工程师','负责云平台核心功能开发','15k-25k',10,'Java,Go,Kubernetes,Docker','杭州','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 45 DAY)),
((SELECT id FROM companies WHERE credit_code='91330000MA27XYZ123'),'前端开发实习生','参与云控制台前端开发','6k-10k',5,'React,TypeScript','杭州','实习','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 30 DAY)),
((SELECT id FROM companies WHERE credit_code='91440300708461136T'),'后端开发工程师','负责微信/QQ后台服务开发','18k-30k',15,'C++,Go,分布式系统','深圳','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91440300708461136T'),'算法工程师','推荐算法、广告算法研发','20k-35k',8,'Python,机器学习,深度学习','深圳','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91440300708461136T'),'测试开发工程师','自动化测试平台开发','12k-20k',6,'Python,Java,自动化测试','深圳','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 45 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12'),'抖音后端研发','负责抖音核心业务后端开发','18k-32k',20,'Go,Java,分布式','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12'),'前端开发工程师','负责抖音/今日头条前端开发','16k-28k',12,'React,Vue,TypeScript','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12'),'数据分析师','用户行为分析、数据挖掘','14k-24k',5,'SQL,Python,数据分析','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 45 DAY)),
((SELECT id FROM companies WHERE credit_code='91440300618520432Y'),'5G研发工程师','5G核心网研发','16k-28k',10,'C++,通信协议','深圳','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91440300618520432Y'),'鸿蒙系统开发','鸿蒙操作系统应用开发','15k-26k',8,'Java,C++,Android','深圳','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34'),'后端开发工程师','外卖/到店业务后端开发','16k-28k',15,'Java,Go,MySQL,Redis','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34'),'算法工程师','配送调度算法、推荐算法','18k-32k',6,'Python,算法,机器学习','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110000633574694Y'),'Java开发工程师','电商平台后端开发','15k-26k',12,'Java,Spring,微服务','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110000633574694Y'),'前端开发工程师','电商前端页面开发','14k-24k',8,'Vue,React,小程序','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91330100MA27IJKL56'),'游戏客户端开发','手游客户端开发','16k-28k',10,'Unity,C#,Cocos','杭州','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91330100MA27IJKL56'),'游戏服务端开发','游戏后端逻辑开发','15k-26k',8,'Java,Go,游戏架构','杭州','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA00MNOP78'),'Android开发工程师','MIUI系统应用开发','14k-24k',10,'Java,Kotlin,Android','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY)),
((SELECT id FROM companies WHERE credit_code='91110108MA00MNOP78'),'IoT开发工程师','智能家居设备开发','13k-22k',6,'C,嵌入式,IoT','北京','全职','PUBLISHED',NOW(6),DATE_ADD(NOW(6), INTERVAL 60 DAY));

INSERT INTO job_fair_events (job_fair_id, name, type, location, start_time, end_time, capacity, description) VALUES
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'),'企业宣讲会','PRESENTATION','报告厅A','2025-03-20 10:00:00','2025-03-20 12:00:00',300,'示例科技宣讲'),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'),'面试专场','INTERVIEW_DAY','面试区B','2025-03-20 13:30:00','2025-03-20 16:30:00',100,'现场面试'),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'),'阿里云专场宣讲','PRESENTATION','报告厅B','2025-03-20 14:00:00','2025-03-20 16:00:00',300,'阿里云技术分享'),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'),'腾讯技术分享会','PRESENTATION','报告厅C','2025-03-20 10:30:00','2025-03-20 12:30:00',400,'腾讯技术与文化'),
((SELECT id FROM job_fairs WHERE name='2025夏季实习生专场'),'字节跳动实习生招聘','INTERVIEW_DAY','面试区A','2025-05-15 09:00:00','2025-05-15 17:00:00',150,'实习生现场面试'),
((SELECT id FROM job_fairs WHERE name='2025夏季实习生专场'),'华为实习生宣讲','PRESENTATION','报告厅A','2025-05-15 10:00:00','2025-05-15 12:00:00',200,'华为实习生项目介绍');

-- 插入申请记录，分布在过去6个月
INSERT INTO applications (job_id, student_id, resume_id, status, notes, created_at) VALUES
-- 当前月份（12月）
((SELECT id FROM jobs WHERE title='Java后端实习生' AND company_id=(SELECT id FROM companies WHERE credit_code='91310000MA1K12345X') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20259999'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20259999') ORDER BY id LIMIT 1), 'SUBMITTED', '期待面试机会', NOW(6)),
((SELECT id FROM jobs WHERE title='云计算开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250003'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250003') ORDER BY id LIMIT 1), 'SUBMITTED', '对云计算很感兴趣', NOW(6)),
((SELECT id FROM jobs WHERE title='前端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250004'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250004') ORDER BY id LIMIT 1), 'INTERVIEW', '已通过初试', NOW(6)),
-- 11月
((SELECT id FROM jobs WHERE title='后端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91440300708461136T') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250005'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250005') ORDER BY id LIMIT 1), 'SUBMITTED', '希望加入腾讯', DATE_SUB(NOW(6), INTERVAL 1 MONTH)),
((SELECT id FROM jobs WHERE title='算法工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91440300708461136T') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250006'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250006') ORDER BY id LIMIT 1), 'INTERVIEW', '算法基础扎实', DATE_SUB(NOW(6), INTERVAL 1 MONTH)),
((SELECT id FROM jobs WHERE title='抖音后端研发' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250007'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250007') ORDER BY id LIMIT 1), 'SUBMITTED', '熟悉Go语言', DATE_SUB(NOW(6), INTERVAL 1 MONTH)),
-- 10月
((SELECT id FROM jobs WHERE title='数据分析师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250008'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250008') ORDER BY id LIMIT 1), 'HIRED', '已获得offer', DATE_SUB(NOW(6), INTERVAL 2 MONTH)),
((SELECT id FROM jobs WHERE title='Android开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250009'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250009') ORDER BY id LIMIT 1), 'SUBMITTED', 'Android开发经验丰富', DATE_SUB(NOW(6), INTERVAL 2 MONTH)),
((SELECT id FROM jobs WHERE title='测试开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250010'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250010') ORDER BY id LIMIT 1), 'INTERVIEW', '自动化测试经验', DATE_SUB(NOW(6), INTERVAL 2 MONTH)),
-- 9月
((SELECT id FROM jobs WHERE title='5G研发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250011'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250011') ORDER BY id LIMIT 1), 'SUBMITTED', 'C++基础好', DATE_SUB(NOW(6), INTERVAL 3 MONTH)),
((SELECT id FROM jobs WHERE title='前端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110000633574694Y') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250012'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250012') ORDER BY id LIMIT 1), 'SUBMITTED', 'React技术栈', DATE_SUB(NOW(6), INTERVAL 3 MONTH)),
-- 8月
((SELECT id FROM jobs WHERE title='算法工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250013'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250013') ORDER BY id LIMIT 1), 'REJECTED', '经验不足', DATE_SUB(NOW(6), INTERVAL 4 MONTH)),
((SELECT id FROM jobs WHERE title='游戏客户端开发' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250015'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250015') ORDER BY id LIMIT 1), 'INTERVIEW', 'Unity项目经验', DATE_SUB(NOW(6), INTERVAL 4 MONTH)),
-- 7月
((SELECT id FROM jobs WHERE title='Java开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250003'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250003') ORDER BY id LIMIT 1), 'SUBMITTED', '多投几家公司', DATE_SUB(NOW(6), INTERVAL 5 MONTH)),
((SELECT id FROM jobs WHERE title='后端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250007'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250007') ORDER BY id LIMIT 1), 'HIRED', '已获得美团offer', DATE_SUB(NOW(6), INTERVAL 5 MONTH)),
-- 额外添加更多历史数据让趋势更明显
((SELECT id FROM jobs WHERE title='IoT开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250011'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250011') ORDER BY id LIMIT 1), 'SUBMITTED', '对IoT感兴趣', DATE_SUB(NOW(6), INTERVAL 5 MONTH)),
((SELECT id FROM jobs WHERE title='游戏服务端开发' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250015'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250015') ORDER BY id LIMIT 1), 'SUBMITTED', '游戏开发经验', DATE_SUB(NOW(6), INTERVAL 4 MONTH)),
((SELECT id FROM jobs WHERE title='鸿蒙系统开发' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250009'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250009') ORDER BY id LIMIT 1), 'SUBMITTED', '对鸿蒙感兴趣', DATE_SUB(NOW(6), INTERVAL 3 MONTH)),
((SELECT id FROM jobs WHERE title='前端开发实习生' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250012'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250012') ORDER BY id LIMIT 1), 'INTERVIEW', '前端基础扎实', DATE_SUB(NOW(6), INTERVAL 2 MONTH)),
((SELECT id FROM jobs WHERE title='Java开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250005'), (SELECT id FROM resumes WHERE student_id=(SELECT id FROM students WHERE student_no='20250005') ORDER BY id LIMIT 1), 'REJECTED', '竞争激烈', DATE_SUB(NOW(6), INTERVAL 1 MONTH));

INSERT INTO favorite_companies (student_id, company_id) VALUES
((SELECT id FROM students WHERE student_no='20259999'),(SELECT id FROM companies WHERE credit_code='91310000MA1K12345X')),
((SELECT id FROM students WHERE student_no='20250003'),(SELECT id FROM companies WHERE credit_code='91330000MA27XYZ123')),
((SELECT id FROM students WHERE student_no='20250004'),(SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12')),
((SELECT id FROM students WHERE student_no='20250005'),(SELECT id FROM companies WHERE credit_code='91440300708461136T')),
((SELECT id FROM students WHERE student_no='20250006'),(SELECT id FROM companies WHERE credit_code='91440300708461136T')),
((SELECT id FROM students WHERE student_no='20250007'),(SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12')),
((SELECT id FROM students WHERE student_no='20250008'),(SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34')),
((SELECT id FROM students WHERE student_no='20250009'),(SELECT id FROM companies WHERE credit_code='91110108MA00MNOP78')),
((SELECT id FROM students WHERE student_no='20250010'),(SELECT id FROM companies WHERE credit_code='91440300708461136T')),
((SELECT id FROM students WHERE student_no='20250012'),(SELECT id FROM companies WHERE credit_code='91110000633574694Y')),
((SELECT id FROM students WHERE student_no='20250015'),(SELECT id FROM companies WHERE credit_code='91330100MA27IJKL56'));

INSERT INTO favorite_jobs (student_id, job_id, job_status_snapshot) VALUES
((SELECT id FROM students WHERE student_no='20259999'), (SELECT id FROM jobs WHERE title='前端工程师（校招）' AND company_id=(SELECT id FROM companies WHERE credit_code='91310000MA1K12345X') ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250003'), (SELECT id FROM jobs WHERE title='云计算开发工程师' ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250004'), (SELECT id FROM jobs WHERE title='前端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12') ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250005'), (SELECT id FROM jobs WHERE title='后端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91440300708461136T') ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250006'), (SELECT id FROM jobs WHERE title='算法工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91440300708461136T') ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250007'), (SELECT id FROM jobs WHERE title='抖音后端研发' ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250009'), (SELECT id FROM jobs WHERE title='Android开发工程师' ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250012'), (SELECT id FROM jobs WHERE title='前端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110000633574694Y') ORDER BY id LIMIT 1), 'PUBLISHED'),
((SELECT id FROM students WHERE student_no='20250015'), (SELECT id FROM jobs WHERE title='游戏客户端开发' ORDER BY id LIMIT 1), 'PUBLISHED');

INSERT INTO event_registrations (event_id, student_id, status, seat_no, checkin_time) VALUES
((SELECT id FROM job_fair_events WHERE name='企业宣讲会' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20259999'), 'CHECKED_IN','A-001', '2025-12-17 09:45:00'),
((SELECT id FROM job_fair_events WHERE name='阿里云专场宣讲' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250003'), 'CHECKED_IN','B-015', '2025-12-17 13:50:00'),
((SELECT id FROM job_fair_events WHERE name='腾讯技术分享会' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250004'), 'CHECKED_IN','C-023', '2025-12-17 10:20:00'),
((SELECT id FROM job_fair_events WHERE name='腾讯技术分享会' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250005'), 'REGISTERED','C-024', NULL),
((SELECT id FROM job_fair_events WHERE name='面试专场' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250006'), 'CHECKED_IN','B-005', '2025-12-17 13:25:00'),
((SELECT id FROM job_fair_events WHERE name='字节跳动实习生招聘' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250007'), 'REGISTERED','A-010', NULL),
((SELECT id FROM job_fair_events WHERE name='华为实习生宣讲' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250011'), 'REGISTERED','A-020', NULL),
((SELECT id FROM job_fair_events WHERE name='企业宣讲会' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250008'), 'CHECKED_IN','A-015', '2025-12-17 09:50:00'),
((SELECT id FROM job_fair_events WHERE name='面试专场' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250010'), 'CHECKED_IN','B-012', '2025-12-17 13:20:00');

INSERT INTO booths (job_fair_id, company_id, booth_no, location, checked_in) VALUES
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91310000MA1K12345X'),'A12','一区',0),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91330000MA27XYZ123'),'A01','一区',1),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91440300708461136T'),'A02','一区',1),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12'),'A03','一区',1),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91440300618520432Y'),'B01','二区',0),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34'),'B02','二区',1),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91110000633574694Y'),'B03','二区',1),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91330100MA27IJKL56'),'C01','三区',0),
((SELECT id FROM job_fairs WHERE name='2025春季校园双选会'), (SELECT id FROM companies WHERE credit_code='91110108MA00MNOP78'),'C02','三区',1);

INSERT INTO announcements (title, content, target, publish_at, pinned) VALUES
('欢迎使用校园招聘平台','这是 SQL 脚本插入的示例公告。','ALL',NOW(6),0),
('2025春季校园双选会即将开始','2025年3月20日，大学体育馆，诚邀各位同学参加！','STUDENT',NOW(6),1),
('企业入驻优惠政策','现在入驻平台，享受前100家企业免费推广服务！','COMPANY',NOW(6),0),
('简历优化指导讲座通知','本周五下午2点，就业指导中心，简历撰写技巧分享。','STUDENT',NOW(6),1),
('平台维护通知','本周日凌晨2:00-4:00进行系统维护，请提前安排。','ALL',NOW(6),0);

INSERT INTO interviews (job_id, student_id, scheduled_at, location, interviewer, status, feedback) VALUES
((SELECT id FROM jobs WHERE title='Java后端实习生' AND company_id=(SELECT id FROM companies WHERE credit_code='91310000MA1K12345X') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20259999'), DATE_ADD(NOW(6), INTERVAL 3 DAY), '会议室1', '王工', 'PENDING', NULL),
((SELECT id FROM jobs WHERE title='前端开发工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250004'), DATE_ADD(NOW(6), INTERVAL 5 DAY), '字节大厦A座', '李经理', 'CONFIRMED', NULL),
((SELECT id FROM jobs WHERE title='算法工程师' AND company_id=(SELECT id FROM companies WHERE credit_code='91440300708461136T') ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250006'), DATE_ADD(NOW(6), INTERVAL 2 DAY), '腾讯大厦', '张总监', 'CONFIRMED', NULL),
((SELECT id FROM jobs WHERE title='测试开发工程师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250010'), DATE_ADD(NOW(6), INTERVAL 7 DAY), '腾讯大厦', '陈工', 'PENDING', NULL),
((SELECT id FROM jobs WHERE title='游戏客户端开发' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250015'), DATE_ADD(NOW(6), INTERVAL 4 DAY), '网易大厦', '赵主管', 'CONFIRMED', NULL),
((SELECT id FROM jobs WHERE title='数据分析师' ORDER BY id LIMIT 1), (SELECT id FROM students WHERE student_no='20250008'), DATE_ADD(NOW(6), INTERVAL -2 DAY), '字节大厦', '孙经理', 'COMPLETED', '表现优秀，已发offer');

INSERT INTO notifications (recipient_role, recipient_id, title, content, type, read_flag) VALUES
('STUDENT',(SELECT id FROM students WHERE student_no='20259999'),'投递状态更新','您的投递已提交','APPLICATION_STATUS',0);

SET foreign_key_checks = 1;

INSERT INTO notifications (recipient_role, recipient_id, title, content, type, read_flag) VALUES
('STUDENT',(SELECT id FROM students WHERE student_no='20259999'),'投递状态更新','您的投递已提交','APPLICATION_STATUS',0),
('STUDENT',(SELECT id FROM students WHERE student_no='20250004'),'面试通知','字节跳动邀请您参加面试','INTERVIEW',0),
('STUDENT',(SELECT id FROM students WHERE student_no='20250006'),'面试通知','腾讯算法岗位面试邀请','INTERVIEW',0),
('STUDENT',(SELECT id FROM students WHERE student_no='20250008'),'Offer通知','恭喜！您已获得字节跳动数据分析师offer','OFFER',0),
('STUDENT',(SELECT id FROM students WHERE student_no='20250007'),'Offer通知','恭喜！您已获得美团后端开发offer','OFFER',1),
('STUDENT',(SELECT id FROM students WHERE student_no='20250003'),'系统通知','2025春季校园双选会即将开始','SYSTEM',1),
('COMPANY',(SELECT id FROM companies WHERE credit_code='91310000MA1K12345X'),'新投递通知','您有新的简历投递','APPLICATION',0),
('COMPANY',(SELECT id FROM companies WHERE credit_code='91440300708461136T'),'新投递通知','您有3份新的简历投递','APPLICATION',1);

-- 插入用户账号（密码使用 BCrypt 加密）
-- 管理员账号: admin / 123456
-- 学生账号: 20259999 / 123456  
-- 企业账号: CRED-010 / 123456 (用户名是企业的统一社会信用代码)
INSERT INTO user_accounts (username, password_hash, role, active, student_id, company_id) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN', 1, NULL, NULL),
('20259999', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20259999'), NULL),
('20250003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250003'), NULL),
('20250004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250004'), NULL),
('20250005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250005'), NULL),
('20250006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250006'), NULL),
('20250007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250007'), NULL),
('20250008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250008'), NULL),
('20250009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250009'), NULL),
('20250010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250010'), NULL),
('20250011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250011'), NULL),
('20250012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250012'), NULL),
('20250013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250013'), NULL),
('20250014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250014'), NULL),
('20250015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 1, (SELECT id FROM students WHERE student_no='20250015'), NULL),
('91310000MA1K12345X', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91310000MA1K12345X')),
('91310000MA1K67890Y', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91310000MA1K67890Y')),
('91330000MA27XYZ123', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91330000MA27XYZ123')),
('91440300708461136T', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91440300708461136T')),
('91110108MA01ABCD12', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91110108MA01ABCD12')),
('91440300618520432Y', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91440300618520432Y')),
('91110108MA00EFGH34', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91110108MA00EFGH34')),
('91110000633574694Y', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91110000633574694Y')),
('91330100MA27IJKL56', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91330100MA27IJKL56')),
('91110108MA00MNOP78', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'COMPANY', 1, NULL, (SELECT id FROM companies WHERE credit_code='91110108MA00MNOP78'));

SET foreign_key_checks = 1;
