# 设计文档

## 概述

高校毕业生双选会信息管理系统是一个基于 SpringBoot 的三端（学生端、企业端、管理端）招聘平台。系统采用前后端分离架构，后端使用 SpringBoot + MyBatis + MySQL 技术栈，提供 RESTful API 接口。系统核心功能包括用户认证与授权、岗位发布与申请、双选会活动管理、企业入驻审核、简历管理、消息通知、数据统计分析等模块。

系统设计遵循分层架构原则，将业务逻辑、数据访问和表现层分离，确保系统的可维护性和可扩展性。采用基于角色的访问控制（RBAC）模型实现权限管理，使用 JWT 进行用户认证，确保系统安全性。

## 架构

### 系统架构

系统采用经典的三层架构：

1. **表现层（Controller Layer）**：处理 HTTP 请求，参数验证，调用业务层服务
2. **业务层（Service Layer）**：实现核心业务逻辑，事务管理，业务规则验证
3. **数据访问层（DAO/Mapper Layer）**：使用 MyBatis 进行数据库操作，SQL 映射

### 技术栈

- **后端框架**：Spring Boot 2.7+
- **ORM 框架**：MyBatis 3.5+
- **数据库**：MySQL 8.0+
- **安全框架**：Spring Security + JWT
- **文件存储**：本地文件系统或 OSS
- **缓存**：Redis（可选，用于会话管理和热点数据）
- **日志**：SLF4J + Logback
- **API 文档**：Swagger/OpenAPI 3.0

### 部署架构

- **应用服务器**：Tomcat（内嵌）
- **数据库服务器**：MySQL 独立部署
- **文件存储**：独立文件服务器或云存储
- **反向代理**：Nginx（可选，用于负载均衡和静态资源）

## 组件和接口

### 核心组件

#### 1. 用户认证与授权模块（Authentication & Authorization）

**职责**：
- 用户注册、登录、密码管理
- JWT Token 生成与验证
- 基于角色的权限控制
- 会话管理

**主要接口**：
- `POST /api/auth/register/student` - 学生注册
- `POST /api/auth/register/company` - 企业注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/reset-password` - 密码重置
- `GET /api/auth/profile` - 获取当前用户信息
- `PUT /api/auth/profile` - 更新用户信息

#### 2. 学生管理模块（Student Management）

**职责**：
- 学生信息管理
- 简历创建与编辑
- 简历附件上传
- 收藏企业和岗位

**主要接口**：
- `GET /api/students/{id}` - 获取学生详情
- `PUT /api/students/{id}` - 更新学生信息
- `GET /api/students/{id}/resumes` - 获取学生简历列表
- `POST /api/students/{id}/resumes` - 创建简历
- `PUT /api/students/{id}/resumes/{resumeId}` - 更新简历
- `DELETE /api/students/{id}/resumes/{resumeId}` - 删除简历
- `POST /api/students/{id}/resumes/{resumeId}/upload` - 上传简历附件
- `POST /api/students/{id}/favorites/companies` - 收藏企业
- `POST /api/students/{id}/favorites/jobs` - 收藏岗位
- `GET /api/students/{id}/favorites` - 获取收藏列表

#### 3. 企业管理模块（Company Management）

**职责**：
- 企业入驻申请
- 企业信息维护
- 企业资质管理
- 企业审核状态管理

**主要接口**：
- `POST /api/companies` - 企业入驻申请
- `GET /api/companies` - 获取企业列表（支持筛选）
- `GET /api/companies/{id}` - 获取企业详情
- `PUT /api/companies/{id}` - 更新企业信息
- `POST /api/companies/{id}/logo` - 上传企业 LOGO
- `GET /api/companies/{id}/reviews` - 获取企业评价

#### 4. 岗位管理模块（Job Position Management）

**职责**：
- 岗位发布与编辑
- 岗位上下架管理
- 岗位搜索与筛选
- 岗位推荐

**主要接口**：
- `POST /api/jobs` - 发布岗位
- `GET /api/jobs` - 获取岗位列表（支持搜索和筛选）
- `GET /api/jobs/{id}` - 获取岗位详情
- `PUT /api/jobs/{id}` - 更新岗位信息
- `DELETE /api/jobs/{id}` - 下架岗位
- `GET /api/jobs/recommendations` - 获取推荐岗位

#### 5. 投递管理模块（Application Management）

**职责**：
- 简历投递
- 投递记录管理
- 投递状态更新
- 投递撤回

**主要接口**：
- `POST /api/applications` - 投递简历
- `GET /api/applications` - 获取投递记录（学生端/企业端）
- `GET /api/applications/{id}` - 获取投递详情
- `PUT /api/applications/{id}/status` - 更新投递状态
- `DELETE /api/applications/{id}` - 撤回投递
- `GET /api/jobs/{jobId}/applications` - 获取岗位的所有投递

#### 6. 双选会活动模块（Job Fair Management）

**职责**：
- 双选会信息发布
- 活动报名管理
- 展位分配
- 签到管理

**主要接口**：
- `POST /api/job-fairs` - 创建双选会
- `GET /api/job-fairs` - 获取双选会列表
- `GET /api/job-fairs/{id}` - 获取双选会详情
- `POST /api/job-fairs/{id}/registrations` - 报名参加活动
- `GET /api/job-fairs/{id}/registrations` - 获取报名列表
- `POST /api/job-fairs/{id}/check-in` - 签到
- `GET /api/job-fairs/{id}/booths` - 获取展位信息
- `POST /api/job-fairs/{id}/booths` - 分配展位

#### 7. 面试管理模块（Interview Management）

**职责**：
- 面试安排
- 面试邀请通知
- 面试确认与拒绝
- 面试评价

**主要接口**：
- `POST /api/interviews` - 创建面试安排
- `GET /api/interviews` - 获取面试列表
- `GET /api/interviews/{id}` - 获取面试详情
- `PUT /api/interviews/{id}` - 更新面试信息
- `PUT /api/interviews/{id}/confirm` - 确认面试
- `PUT /api/interviews/{id}/reject` - 拒绝面试
- `POST /api/interviews/{id}/evaluation` - 提交面试评价

#### 8. 消息通知模块（Notification Management）

**职责**：
- 站内消息发送
- 消息读取状态管理
- 系统通知推送

**主要接口**：
- `GET /api/notifications` - 获取消息列表
- `GET /api/notifications/unread-count` - 获取未读消息数
- `PUT /api/notifications/{id}/read` - 标记消息已读
- `PUT /api/notifications/read-all` - 标记所有消息已读

#### 9. 评价反馈模块（Review & Feedback）

**职责**：
- 企业评价
- 评价展示
- 评价管理

**主要接口**：
- `POST /api/reviews` - 提交评价
- `GET /api/companies/{id}/reviews` - 获取企业评价列表
- `DELETE /api/reviews/{id}` - 删除评价（管理员）

#### 10. 管理员模块（Admin Management）

**职责**：
- 企业审核
- 数据统计
- 报表导出
- 公告发布
- 操作日志查询

**主要接口**：
- `GET /api/admin/companies/pending` - 获取待审核企业
- `PUT /api/admin/companies/{id}/approve` - 审核通过
- `PUT /api/admin/companies/{id}/reject` - 审核拒绝
- `GET /api/admin/statistics` - 获取统计数据
- `GET /api/admin/statistics/export` - 导出统计报表
- `POST /api/admin/announcements` - 发布公告
- `GET /api/admin/announcements` - 获取公告列表
- `GET /api/admin/logs` - 查询操作日志

#### 11. 文件管理模块（File Management）

**职责**：
- 文件上传
- 文件存储
- 文件下载
- 文件权限控制

**主要接口**：
- `POST /api/files/upload` - 上传文件
- `GET /api/files/{fileId}` - 下载文件
- `DELETE /api/files/{fileId}` - 删除文件

## 数据模型

### 核心实体

#### 1. 用户表（User）

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'COMPANY', 'ADMIN') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_role (role)
);
```

#### 2. 学生表（Student）

```sql
CREATE TABLE student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    student_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL,
    college VARCHAR(100),
    major VARCHAR(100),
    grade INT,
    phone VARCHAR(20),
    email VARCHAR(100),
    default_resume_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_student_number (student_number),
    INDEX idx_user_id (user_id)
);
```

#### 3. 企业表（Company）

```sql
CREATE TABLE company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    unified_social_credit_code VARCHAR(50) UNIQUE NOT NULL,
    industry VARCHAR(100),
    scale ENUM('SMALL', 'MEDIUM', 'LARGE', 'GIANT'),
    description TEXT,
    logo_url VARCHAR(500),
    contact_person VARCHAR(50),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    address VARCHAR(500),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reject_reason TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_unified_code (unified_social_credit_code)
);
```

#### 4. 简历表（Resume）

```sql
CREATE TABLE resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    attachment_url VARCHAR(500),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    INDEX idx_student_id (student_id)
);
```

#### 5. 岗位表（Job Position）

```sql
CREATE TABLE job_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    requirements TEXT,
    salary_min DECIMAL(10, 2),
    salary_max DECIMAL(10, 2),
    location VARCHAR(200),
    job_type VARCHAR(50),
    recruitment_count INT,
    status ENUM('PUBLISHED', 'CLOSED', 'DRAFT') DEFAULT 'PUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

#### 6. 投递记录表（Application）

```sql
CREATE TABLE application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    status ENUM('SUBMITTED', 'VIEWED', 'INTERVIEW', 'OFFERED', 'REJECTED') DEFAULT 'SUBMITTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES job_position(id) ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resume(id),
    UNIQUE KEY uk_student_job (student_id, job_id),
    INDEX idx_student_id (student_id),
    INDEX idx_job_id (job_id),
    INDEX idx_status (status)
);
```

#### 7. 双选会表（Job Fair）

```sql
CREATE TABLE job_fair (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    location VARCHAR(500),
    capacity INT,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_start_time (start_time),
    INDEX idx_status (status)
);
```

#### 8. 展位表（Booth）

```sql
CREATE TABLE booth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_fair_id BIGINT NOT NULL,
    booth_number VARCHAR(50) NOT NULL,
    location VARCHAR(200),
    company_id BIGINT NULL,
    checked_in BOOLEAN DEFAULT FALSE,
    check_in_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_fair_id) REFERENCES job_fair(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE SET NULL,
    UNIQUE KEY uk_fair_booth (job_fair_id, booth_number),
    INDEX idx_job_fair_id (job_fair_id),
    INDEX idx_company_id (company_id)
);
```

#### 9. 活动报名表（Registration）

```sql
CREATE TABLE registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_fair_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status ENUM('REGISTERED', 'CHECKED_IN', 'CANCELLED') DEFAULT 'REGISTERED',
    check_in_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (job_fair_id) REFERENCES job_fair(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    UNIQUE KEY uk_fair_student (job_fair_id, student_id),
    INDEX idx_job_fair_id (job_fair_id),
    INDEX idx_student_id (student_id)
);
```

#### 10. 面试表（Interview）

```sql
CREATE TABLE interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    application_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    interview_time TIMESTAMP NOT NULL,
    location VARCHAR(500),
    interviewer VARCHAR(100),
    status ENUM('SCHEDULED', 'CONFIRMED', 'REJECTED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    evaluation TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES application(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    INDEX idx_application_id (application_id),
    INDEX idx_student_id (student_id),
    INDEX idx_company_id (company_id),
    INDEX idx_interview_time (interview_time)
);
```

#### 11. 消息通知表（Notification）

```sql
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type ENUM('SYSTEM', 'APPLICATION', 'INTERVIEW', 'APPROVAL') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);
```

#### 12. 收藏表（Favorite）

```sql
CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    target_type ENUM('COMPANY', 'JOB') NOT NULL,
    target_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_target (student_id, target_type, target_id),
    INDEX idx_student_id (student_id),
    INDEX idx_target (target_type, target_id)
);
```

#### 13. 评价表（Review）

```sql
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    INDEX idx_company_id (company_id),
    INDEX idx_student_id (student_id)
);
```

#### 14. 公告表（Announcement）

```sql
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    target_role ENUM('ALL', 'STUDENT', 'COMPANY') DEFAULT 'ALL',
    is_pinned BOOLEAN DEFAULT FALSE,
    publish_time TIMESTAMP NOT NULL,
    expire_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_publish_time (publish_time),
    INDEX idx_target_role (target_role),
    INDEX idx_is_pinned (is_pinned)
);
```

#### 15. 操作日志表（Operation Log）

```sql
CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_desc VARCHAR(500),
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    request_params TEXT,
    response_status INT,
    error_message TEXT NULL,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
);
```

#### 16. 文件表（File）

```sql
CREATE TABLE file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_name VARCHAR(500) NOT NULL,
    stored_name VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100),
    uploader_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploader_id) REFERENCES user(id),
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_created_at (created_at)
);
```

### 实体关系

- User 1:1 Student/Company（一个用户对应一个学生或企业）
- Student 1:N Resume（一个学生可以有多份简历）
- Student 1:N Application（一个学生可以有多个投递记录）
- Company 1:N Job Position（一个企业可以发布多个岗位）
- Job Position 1:N Application（一个岗位可以收到多个投递）
- Application 1:N Interview（一个投递可以有多次面试）
- Job Fair 1:N Booth（一个双选会有多个展位）
- Job Fair 1:N Registration（一个双选会有多个报名记录）
- Student N:M Company（通过 Favorite 表实现收藏关系）
- Student N:M Job Position（通过 Favorite 表实现收藏关系）
- Company 1:N Review（一个企业可以有多个评价）


## 正确性属性

*属性是指在系统所有有效执行过程中都应该成立的特征或行为——本质上是关于系统应该做什么的形式化陈述。属性是人类可读规范和机器可验证正确性保证之间的桥梁。*

在编写正确性属性之前，我需要分析需求文档中的验收标准，确定哪些可以转化为可测试的属性。

### 验收标准测试预分析

#### 需求 1：学生用户注册与认证

1.1 WHEN 学生用户提交注册信息（学号、姓名、学院、联系方式），THE 系统 SHALL 验证学号的唯一性
- 思考：这是关于所有学号的规则，不是特定示例。我们可以生成随机学生数据，尝试用已存在的学号注册，验证系统是否拒绝
- 可测试性：是 - 属性

1.2 IF 学号已存在，THEN THE 系统 SHALL 拒绝注册并提示错误信息
- 思考：这与 1.1 重复，都在测试学号唯一性约束
- 可测试性：冗余（已被 1.1 覆盖）

1.3 WHEN 学生用户提交有效的登录凭证（学号或手机号加密码），THE 系统 SHALL 验证凭证并授予访问权限
- 思考：这是关于所有有效凭证的规则。我们可以生成随机用户，用正确的凭证登录，验证是否成功
- 可测试性：是 - 属性

1.4 WHEN 学生用户请求密码重置，THE 系统 SHALL 通过验证码验证身份后允许重置密码
- 思考：这是密码重置的往返属性。重置密码后，应该能用新密码登录
- 可测试性：是 - 属性

1.5 WHEN 学生用户更新个人资料，THE 系统 SHALL 保存更新后的信息到数据库
- 思考：这是更新操作的往返属性。更新后查询应该返回新值
- 可测试性：是 - 属性

#### 需求 2：企业与岗位信息浏览

2.1 WHEN 学生用户访问企业列表页面，THE 系统 SHALL 显示所有已审核通过的企业信息
- 思考：这是关于所有企业列表查询的规则。返回的企业状态都应该是"已审核"
- 可测试性：是 - 属性

2.2 WHEN 学生用户点击企业详情，THE 系统 SHALL 显示企业名称、规模、行业、简介等完整信息
- 思考：这是测试渲染函数是否包含所有必需字段
- 可测试性：是 - 属性

2.3 WHEN 学生用户查看岗位详情，THE 系统 SHALL 显示岗位描述、薪资待遇、招聘人数、技能要求等信息
- 思考：这是测试岗位详情是否包含所有必需字段
- 可测试性：是 - 属性

2.4 WHEN 学生用户应用筛选条件（行业、岗位类型、薪资范围、工作地点），THE 系统 SHALL 返回符合所有筛选条件的岗位列表
- 思考：这是关于所有筛选查询的规则。我们可以生成随机筛选条件和数据，验证所有结果都满足条件
- 可测试性：是 - 属性

2.5 WHEN 学生用户输入搜索关键词，THE 系统 SHALL 返回岗位名称或企业名称包含该关键词的结果
- 思考：这是关于所有搜索查询的规则。所有结果都应该包含关键词
- 可测试性：是 - 属性

#### 需求 3：岗位申请与投递管理

3.1 WHEN 学生用户选择岗位并提交申请，THE 系统 SHALL 创建投递记录并关联学生简历
- 思考：这是创建操作的往返属性。创建后查询应该能找到该记录
- 可测试性：是 - 属性

3.2 WHEN 学生用户查看投递记录，THE 系统 SHALL 显示所有投递的岗位及其当前状态
- 思考：这是查询完整性。返回的记录应该包含所有投递
- 可测试性：是 - 属性

3.3 WHEN 学生用户在允许时间内请求撤回投递，THE 系统 SHALL 删除该投递记录并允许重新申请
- 思考：这是删除操作的验证。删除后查询不应该找到该记录
- 可测试性：是 - 属性

3.4 WHEN 学生用户重复投递同一岗位，THE 系统 SHALL 拒绝操作并提示已投递
- 思考：这是唯一性约束。同一学生不能对同一岗位投递两次
- 可测试性：是 - 属性

3.5 WHEN 企业用户更新投递状态，THE 系统 SHALL 立即反映在学生用户的投递记录中
- 思考：这是数据一致性。更新后查询应该返回新状态
- 可测试性：是 - 属性

#### 需求 4：双选会活动管理

4.1 WHEN 学生用户访问活动日程页面，THE 系统 SHALL 显示双选会的举办时间、地点、参与企业和展位信息
- 思考：这是测试返回数据是否包含所有必需字段
- 可测试性：是 - 属性

4.2 WHEN 学生用户报名参加宣讲会或面试会，THE 系统 SHALL 创建活动报名记录
- 思考：这是创建操作的往返属性
- 可测试性：是 - 属性

4.3 WHEN 学生用户在现场扫描二维码，THE 系统 SHALL 记录签到时间和位置
- 思考：这是签到操作的验证。签到后状态应该更新
- 可测试性：是 - 属性

4.4 WHEN 学生用户查看已报名活动，THE 系统 SHALL 显示活动详情和报名状态
- 思考：这是查询完整性
- 可测试性：是 - 属性

4.5 WHEN 活动容量已满，THE 系统 SHALL 拒绝新的报名请求并提示已满
- 思考：这是容量限制的边界条件测试
- 可测试性：是 - 边界情况

#### 需求 5：企业入驻与资质管理

5.1 WHEN 企业用户提交入驻申请，THE 系统 SHALL 创建待审核的企业账号
- 思考：这是创建操作，初始状态应该是"待审核"
- 可测试性：是 - 属性

5.2 WHEN 管理员审核企业申请，THE 系统 SHALL 更新企业状态为已审核或已拒绝
- 思考：这是状态转换的验证
- 可测试性：是 - 属性

5.3 WHEN 企业用户的账号状态为已审核，THE 系统 SHALL 授予岗位发布权限
- 思考：这是权限控制。只有已审核企业能发布岗位
- 可测试性：是 - 属性

5.4 WHEN 企业用户更新企业资料，THE 系统 SHALL 保存更新后的信息
- 思考：这是更新操作的往返属性
- 可测试性：是 - 属性

5.5 WHEN 企业用户尝试使用已存在的统一社会信用代码注册，THE 系统 SHALL 拒绝注册并提示重复
- 思考：这是唯一性约束验证
- 可测试性：是 - 属性

#### 需求 6：岗位发布与应聘管理

6.1 WHEN 企业用户创建新岗位，THE 系统 SHALL 保存岗位信息并设置状态为已发布
- 思考：这是创建操作，初始状态应该是"已发布"
- 可测试性：是 - 属性

6.2 WHEN 企业用户编辑已发布岗位，THE 系统 SHALL 更新岗位信息并保持投递记录不变
- 思考：这是不变性属性。更新岗位不应该影响已有投递
- 可测试性：是 - 属性

6.3 WHEN 企业用户下架岗位，THE 系统 SHALL 将岗位状态设置为已下架并在学生端隐藏该岗位
- 思考：这是状态转换和可见性规则
- 可测试性：是 - 属性

6.4 WHEN 企业用户查看岗位的投递简历，THE 系统 SHALL 显示所有投递该岗位的学生信息和简历
- 思考：这是查询完整性
- 可测试性：是 - 属性

6.5 WHEN 企业用户更新投递状态，THE 系统 SHALL 更新投递记录状态并通知学生用户
- 思考：这是状态更新和通知的联动
- 可测试性：是 - 属性

#### 需求 7-20 的其他验收标准

由于篇幅限制，其他需求的验收标准遵循类似的分析模式：
- 数据创建/更新/删除操作 → 往返属性
- 查询和筛选 → 结果完整性和正确性属性
- 状态转换 → 状态机属性
- 唯一性约束 → 约束验证属性
- 权限控制 → 访问控制属性

### 属性反思

在编写具体属性之前，我需要识别并消除冗余：

1. **学号唯一性**（1.1）和**拒绝重复学号**（1.2）是同一个属性，保留一个
2. **投递记录创建**（3.1）和**投递记录查询**（3.2）可以合并为往返属性
3. **企业状态更新**（5.2）已经包含了审核通过和拒绝两种情况
4. 多个"更新后查询"的属性可以抽象为通用的**数据持久化往返属性**

### 核心正确性属性

#### 属性 1：学号唯一性约束
*对于任意*已注册的学号，尝试使用相同学号注册新账号应该被系统拒绝
**验证需求：1.1, 1.2**

#### 属性 2：统一社会信用代码唯一性约束
*对于任意*已注册的统一社会信用代码，尝试使用相同代码注册新企业应该被系统拒绝
**验证需求：5.5**

#### 属性 3：认证凭证验证
*对于任意*有效的用户账号和正确的密码，登录操作应该成功并返回有效的认证令牌
**验证需求：1.3**

#### 属性 4：数据更新往返一致性
*对于任意*实体（学生、企业、岗位等）的更新操作，更新后立即查询应该返回更新后的值
**验证需求：1.5, 5.4, 6.2**

#### 属性 5：密码重置往返
*对于任意*用户，重置密码后使用新密码登录应该成功，使用旧密码登录应该失败
**验证需求：1.4**

#### 属性 6：企业列表过滤正确性
*对于任意*企业列表查询，返回的所有企业状态都应该是"已审核通过"
**验证需求：2.1**

#### 属性 7：岗位搜索结果相关性
*对于任意*搜索关键词，返回的所有岗位的名称或所属企业名称都应该包含该关键词
**验证需求：2.5**

#### 属性 8：岗位筛选条件完整匹配
*对于任意*筛选条件组合（行业、岗位类型、薪资范围、工作地点），返回的所有岗位都应该满足所有筛选条件
**验证需求：2.4**

#### 属性 9：投递记录唯一性约束
*对于任意*学生和岗位组合，同一学生不能对同一岗位创建多个有效的投递记录
**验证需求：3.4**

#### 属性 10：投递创建往返
*对于任意*有效的投递操作，创建投递后查询该学生的投递列表应该包含该投递记录
**验证需求：3.1, 3.2**

#### 属性 11：投递撤回后可重新申请
*对于任意*投递记录，撤回后该学生应该能够重新投递同一岗位
**验证需求：3.3**

#### 属性 12：投递状态同步一致性
*对于任意*投递记录，企业端更新状态后，学生端查询应该立即反映新状态
**验证需求：3.5, 6.5**

#### 属性 13：活动报名容量限制
*对于任意*已满容量的活动，新的报名请求应该被拒绝
**验证需求：4.5**

#### 属性 14：活动报名往返
*对于任意*有效的活动报名，报名后查询该学生的报名列表应该包含该活动
**验证需求：4.2, 4.4**

#### 属性 15：签到状态更新
*对于任意*已报名的学生，签到操作后该报名记录的状态应该更新为"已签到"并记录签到时间
**验证需求：4.3**

#### 属性 16：企业初始状态正确性
*对于任意*新创建的企业账号，初始状态应该是"待审核"
**验证需求：5.1**

#### 属性 17：企业审核状态转换
*对于任意*待审核企业，管理员审核操作后状态应该变为"已审核"或"已拒绝"
**验证需求：5.2**

#### 属性 18：岗位发布权限控制
*对于任意*企业用户，只有当企业状态为"已审核"时才能成功发布岗位
**验证需求：5.3**

#### 属性 19：岗位初始状态正确性
*对于任意*新创建的岗位，初始状态应该是"已发布"
**验证需求：6.1**

#### 属性 20：岗位编辑不影响投递
*对于任意*已有投递的岗位，编辑岗位信息后原有投递记录应该保持不变
**验证需求：6.2**

#### 属性 21：下架岗位不可见
*对于任意*状态为"已下架"的岗位，学生端的岗位列表查询不应该返回该岗位
**验证需求：6.3**

#### 属性 22：企业详情字段完整性
*对于任意*企业详情查询，返回的数据应该包含企业名称、规模、行业、简介等所有必需字段
**验证需求：2.2**

#### 属性 23：岗位详情字段完整性
*对于任意*岗位详情查询，返回的数据应该包含岗位描述、薪资待遇、招聘人数、技能要求等所有必需字段
**验证需求：2.3**

#### 属性 24：活动详情字段完整性
*对于任意*双选会详情查询，返回的数据应该包含举办时间、地点、参与企业和展位信息
**验证需求：4.1**

#### 属性 25：简历删除前置检查
*对于任意*有关联投递记录的简历，删除操作应该被阻止或需要确认
**验证需求：10.5**

#### 属性 26：默认简历设置
*对于任意*学生，设置默认简历后，投递操作应该优先使用该简历
**验证需求：10.4**

#### 属性 27：消息通知触发
*对于任意*投递状态更新操作，系统应该向对应学生发送通知消息
**验证需求：11.1**

#### 属性 28：未读消息计数准确性
*对于任意*用户，未读消息数量应该等于状态为"未读"的消息记录数
**验证需求：11.3**

#### 属性 29：消息已读状态更新
*对于任意*消息，查看后该消息的状态应该更新为"已读"
**验证需求：11.4**

#### 属性 30：收藏操作往返
*对于任意*企业或岗位，收藏后查询收藏列表应该包含该项，取消收藏后不应该包含
**验证需求：12.1, 12.2, 12.3, 12.4**

#### 属性 31：面试创建通知联动
*对于任意*面试安排，创建后系统应该向候选学生发送面试邀请通知
**验证需求：13.2**

#### 属性 32：面试确认状态更新
*对于任意*面试邀请，学生确认或拒绝后面试状态应该相应更新
**验证需求：13.3**

#### 属性 33：评价关联正确性
*对于任意*企业评价，该评价应该正确关联到对应的企业并在企业详情中显示
**验证需求：14.2, 14.3**

#### 属性 34：文件上传类型和大小验证
*对于任意*文件上传请求，如果文件类型或大小不符合限制，应该被拒绝
**验证需求：20.1**

#### 属性 35：文件存储往返
*对于任意*成功上传的文件，应该能够通过文件标识下载到相同内容的文件
**验证需求：20.2, 20.3**

#### 属性 36：数据库事务一致性
*对于任意*数据操作失败，系统应该回滚事务并保持数据完整性
**验证需求：19.3**

#### 属性 37：外键关系完整性
*对于任意*关联数据查询（学生-投递-岗位-企业），系统应该正确维护外键关系
**验证需求：19.4**

#### 属性 38：级联删除或阻止
*对于任意*有关联数据的实体删除操作，系统应该级联处理或阻止删除以保持数据完整性
**验证需求：19.5**

#### 属性 39：密码加密存储
*对于任意*用户密码，存储到数据库时应该使用哈希算法加密，不应该以明文形式存储
**验证需求：9.5**

#### 属性 40：权限验证
*对于任意*需要特定权限的操作，未授权用户的访问应该被拒绝
**验证需求：9.2**


## 错误处理

### 错误分类

系统采用统一的错误处理机制，将错误分为以下几类：

#### 1. 客户端错误（4xx）

- **400 Bad Request**：请求参数验证失败
  - 缺少必需参数
  - 参数格式不正确
  - 参数值超出范围
  
- **401 Unauthorized**：未认证或认证失败
  - Token 缺失或无效
  - Token 已过期
  - 登录凭证错误

- **403 Forbidden**：权限不足
  - 无权访问资源
  - 角色权限不匹配
  - 企业未审核通过

- **404 Not Found**：资源不存在
  - 请求的实体不存在
  - API 端点不存在

- **409 Conflict**：资源冲突
  - 学号已存在
  - 统一社会信用代码重复
  - 重复投递同一岗位
  - 活动容量已满

- **422 Unprocessable Entity**：业务逻辑错误
  - 简历有关联投递无法删除
  - 岗位已下架无法投递
  - 企业未审核无法发布岗位

#### 2. 服务器错误（5xx）

- **500 Internal Server Error**：服务器内部错误
  - 未捕获的异常
  - 数据库连接失败
  - 第三方服务调用失败

- **503 Service Unavailable**：服务暂时不可用
  - 系统维护中
  - 数据库不可用

### 错误响应格式

所有错误响应遵循统一的 JSON 格式：

```json
{
  "success": false,
  "errorCode": "DUPLICATE_STUDENT_NUMBER",
  "message": "该学号已被注册",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/auth/register/student",
  "details": {
    "field": "studentNumber",
    "value": "2021001234"
  }
}
```

### 错误处理策略

#### 1. 全局异常处理器

使用 Spring 的 `@ControllerAdvice` 和 `@ExceptionHandler` 实现全局异常捕获：

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        // 处理参数验证异常
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        // 处理业务逻辑异常
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // 处理未预期的异常
    }
}
```

#### 2. 自定义业务异常

定义业务异常类层次结构：

- `BusinessException`：业务异常基类
  - `ResourceNotFoundException`：资源不存在
  - `DuplicateResourceException`：资源重复
  - `UnauthorizedException`：未授权
  - `ForbiddenException`：权限不足
  - `ValidationException`：验证失败
  - `ConflictException`：资源冲突

#### 3. 数据库事务回滚

- 使用 `@Transactional` 注解管理事务
- 运行时异常自动回滚
- 检查异常需要显式配置回滚规则
- 关键操作使用编程式事务管理

#### 4. 日志记录

- **ERROR 级别**：记录所有 5xx 错误和未预期异常
- **WARN 级别**：记录业务逻辑错误（如重复投递）
- **INFO 级别**：记录正常的业务操作
- **DEBUG 级别**：记录详细的调试信息

错误日志应包含：
- 时间戳
- 用户 ID（如果已认证）
- 请求路径和方法
- 请求参数
- 异常堆栈
- 错误上下文信息

#### 5. 用户友好的错误消息

- 避免暴露技术细节和敏感信息
- 提供清晰的错误原因和解决建议
- 支持国际化（中英文）
- 前端根据错误码显示相应提示

### 特定场景错误处理

#### 文件上传错误

- 文件大小超限：返回 413 Payload Too Large
- 文件类型不支持：返回 415 Unsupported Media Type
- 存储空间不足：返回 507 Insufficient Storage

#### 并发冲突

- 使用乐观锁处理并发更新
- 版本号不匹配时返回 409 Conflict
- 提示用户刷新后重试

#### 外部依赖失败

- 实现重试机制（指数退避）
- 设置超时时间
- 提供降级方案
- 记录详细错误日志供排查

## 测试策略

### 测试层次

系统采用多层次测试策略，确保代码质量和功能正确性：

#### 1. 单元测试（Unit Tests）

**目标**：测试单个类或方法的功能

**工具**：JUnit 5 + Mockito

**覆盖范围**：
- Service 层业务逻辑
- Util 工具类方法
- 数据验证逻辑
- 异常处理逻辑

**示例**：
```java
@Test
void testStudentRegistration_WithDuplicateStudentNumber_ShouldThrowException() {
    // Given
    when(studentRepository.existsByStudentNumber("2021001234")).thenReturn(true);
    
    // When & Then
    assertThrows(DuplicateResourceException.class, () -> {
        studentService.register(studentDTO);
    });
}
```

#### 2. 基于属性的测试（Property-Based Tests）

**目标**：验证系统在大量随机输入下的正确性属性

**工具**：jqwik（Java 的属性测试库）

**配置**：每个属性测试至少运行 100 次迭代

**标注格式**：每个属性测试必须使用注释明确引用设计文档中的正确性属性
```java
/**
 * Feature: campus-job-fair, Property 1: 学号唯一性约束
 * 验证需求：1.1, 1.2
 */
@Property
void studentNumberUniqueness(@ForAll String studentNumber) {
    // 测试实现
}
```

**覆盖的核心属性**：
- 唯一性约束（学号、统一社会信用代码、投递记录）
- 数据往返一致性（创建-查询、更新-查询）
- 状态转换正确性（企业审核、投递状态）
- 权限控制（岗位发布权限、资源访问权限）
- 数据完整性（外键关系、级联操作）

#### 3. 集成测试（Integration Tests）

**目标**：测试多个组件协同工作

**工具**：Spring Boot Test + TestContainers（用于数据库）

**覆盖范围**：
- Controller + Service + Repository 完整流程
- 数据库事务和回滚
- 文件上传和存储
- 消息通知发送

**示例**：
```java
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationIntegrationTest {
    
    @Test
    void testJobApplicationFlow() {
        // 1. 学生投递岗位
        // 2. 企业查看投递
        // 3. 企业更新投递状态
        // 4. 学生收到通知
        // 5. 验证整个流程的数据一致性
    }
}
```

#### 4. API 测试（API Tests）

**目标**：测试 RESTful API 的正确性

**工具**：MockMvc / RestAssured

**覆盖范围**：
- HTTP 状态码正确性
- 请求参数验证
- 响应格式正确性
- 认证和授权
- 错误处理

#### 5. 性能测试（Performance Tests）

**目标**：验证系统性能指标

**工具**：JMeter / Gatling

**测试场景**：
- 并发用户登录
- 大量岗位搜索和筛选
- 批量投递操作
- 数据统计和报表生成

**性能指标**：
- 响应时间：95% 请求 < 500ms
- 吞吐量：支持 1000 并发用户
- 数据库查询：单次查询 < 100ms

### 测试数据管理

#### 测试数据生成

- 使用 Faker 库生成随机测试数据
- 为属性测试编写智能生成器，约束输入空间
- 准备典型场景的测试数据集

#### 测试数据隔离

- 每个测试使用独立的数据库事务
- 测试结束后自动回滚
- 使用 TestContainers 提供隔离的数据库环境

### 测试覆盖率目标

- **代码覆盖率**：≥ 80%
- **分支覆盖率**：≥ 70%
- **核心业务逻辑**：100%

### 持续集成

- 每次代码提交自动运行单元测试和属性测试
- 每日运行完整的集成测试套件
- 定期运行性能测试
- 测试失败阻止代码合并

### 测试优先级

**P0（必须通过）**：
- 所有属性测试
- 核心业务流程的集成测试
- 安全相关测试（认证、授权、数据加密）

**P1（高优先级）**：
- Service 层单元测试
- API 测试
- 数据完整性测试

**P2（中优先级）**：
- Util 工具类测试
- 边界条件测试
- 性能测试

## 安全设计

### 认证机制

#### JWT Token 认证

**Token 结构**：
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": 12345,
    "username": "2021001234",
    "role": "STUDENT",
    "iat": 1642234567,
    "exp": 1642320967
  },
  "signature": "..."
}
```

**Token 生命周期**：
- Access Token：24 小时
- Refresh Token：7 天（可选实现）

**Token 存储**：
- 客户端：存储在 localStorage 或 sessionStorage
- 服务端：可选使用 Redis 缓存 Token 黑名单（用于登出）

#### 密码安全

- 使用 BCrypt 算法加密密码（工作因子 ≥ 10）
- 密码强度要求：至少 8 位，包含字母和数字
- 密码重置需要验证码验证
- 限制密码错误尝试次数（5 次后锁定账号 30 分钟）

### 授权机制

#### 基于角色的访问控制（RBAC）

**角色定义**：
- **STUDENT**：学生用户
  - 浏览企业和岗位
  - 投递简历
  - 管理个人信息和简历
  - 报名参加活动
  - 收藏企业和岗位

- **COMPANY**：企业用户
  - 发布和管理岗位
  - 查看投递简历
  - 更新投递状态
  - 安排面试
  - 管理企业信息

- **ADMIN**：管理员
  - 审核企业入驻
  - 管理双选会活动
  - 查看统计数据
  - 发布公告
  - 查询操作日志

**权限注解**：
```java
@PreAuthorize("hasRole('COMPANY')")
public void publishJob(JobDTO jobDTO) {
    // 只有企业用户可以发布岗位
}

@PreAuthorize("hasRole('ADMIN')")
public void approveCompany(Long companyId) {
    // 只有管理员可以审核企业
}
```

#### 资源级权限控制

- 学生只能查看和修改自己的简历和投递
- 企业只能管理自己发布的岗位
- 企业只能查看投递到自己岗位的简历
- 使用 Spring Security 的 `@PostAuthorize` 进行资源级鉴权

### 数据安全

#### 敏感数据加密

- 密码：BCrypt 哈希
- 身份证号、手机号：AES 加密存储（如果需要）
- HTTPS 传输加密

#### SQL 注入防护

- 使用 MyBatis 的参数化查询（`#{}`）
- 避免字符串拼接 SQL
- 对用户输入进行验证和转义

#### XSS 防护

- 对用户输入的 HTML 内容进行过滤
- 使用内容安全策略（CSP）
- 前端框架自动转义输出

#### CSRF 防护

- 使用 CSRF Token
- 验证 Referer 头
- SameSite Cookie 属性

### 接口安全

#### 请求频率限制

- 使用 Redis + 注解实现接口限流
- 登录接口：每分钟最多 5 次
- 普通接口：每秒最多 10 次
- 文件上传：每分钟最多 3 次

#### 参数验证

- 使用 JSR-303 Bean Validation
- 自定义验证注解
- 统一的参数验证异常处理

#### 日志脱敏

- 密码字段不记录到日志
- 手机号、身份证号打码显示
- 敏感操作记录审计日志

### 会话安全

- 会话超时：30 分钟无操作自动登出
- 异地登录检测和提醒
- 支持强制登出（管理员功能）

## 性能优化

### 数据库优化

#### 索引设计

- 为高频查询字段创建索引（如 student_number, unified_social_credit_code）
- 为外键字段创建索引
- 为状态字段创建索引（如 company.status, job_position.status）
- 避免过度索引影响写入性能

#### 查询优化

- 避免 N+1 查询问题，使用 JOIN 或批量查询
- 分页查询使用游标分页（性能优于 OFFSET）
- 复杂统计查询使用物化视图或定时任务预计算
- 使用 MyBatis 的延迟加载

#### 连接池配置

- 使用 HikariCP 连接池
- 合理配置最大连接数和最小空闲连接数
- 设置连接超时和空闲超时

### 缓存策略

#### Redis 缓存

**缓存内容**：
- 热门岗位列表（TTL: 5 分钟）
- 企业详情（TTL: 30 分钟）
- 用户会话信息（TTL: 24 小时）
- 统计数据（TTL: 1 小时）

**缓存更新策略**：
- 写入时更新缓存（Write-Through）
- 定时刷新缓存（Scheduled Refresh）
- 缓存失效时重新加载（Cache-Aside）

**缓存穿透防护**：
- 布隆过滤器
- 空值缓存

**缓存雪崩防护**：
- 随机 TTL
- 缓存预热

### 文件存储优化

- 大文件分片上传
- 使用 CDN 加速文件下载
- 图片压缩和缩略图生成
- 定期清理过期文件

### 异步处理

使用消息队列（如 RabbitMQ）处理耗时操作：
- 发送邮件通知
- 生成统计报表
- 批量数据导出
- 文件处理

### 接口响应优化

- 使用 DTO 减少数据传输量
- 分页加载大数据集
- 懒加载关联数据
- 压缩响应内容（Gzip）

## 部署架构

### 开发环境

- 本地开发使用内嵌 Tomcat
- 使用 Docker Compose 启动 MySQL 和 Redis
- 使用 H2 内存数据库进行快速测试

### 生产环境

#### 应用服务器

- 使用 Nginx 作为反向代理和负载均衡
- 部署多个应用实例实现高可用
- 使用 Docker 容器化部署

#### 数据库

- MySQL 主从复制（读写分离）
- 定期备份数据库
- 监控数据库性能指标

#### 文件存储

- 使用对象存储服务（如阿里云 OSS）
- 或使用独立的文件服务器 + NFS

#### 监控和日志

- 使用 Prometheus + Grafana 监控系统指标
- 使用 ELK Stack 收集和分析日志
- 配置告警规则

### 扩展性考虑

- 无状态应用设计，支持水平扩展
- 使用 Redis 共享会话
- 数据库分库分表（如果数据量大）
- 微服务拆分（如果系统复杂度增加）

