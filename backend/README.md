# 校园招聘管理系统 - 后端

## 项目概述

基于 Spring Boot 3.2.5 的校园招聘管理系统后端服务，支持学生、企业、管理员三种角色。

## 技术栈

- **框架**: Spring Boot 3.2.5
- **安全**: Spring Security + JWT
- **数据库**: H2 (开发) / MySQL (生产)
- **ORM**: Spring Data JPA
- **构建工具**: Maven
- **Java版本**: 17

## 核心功能模块

### 1. 用户认证与授权
- JWT Token 认证
- 三种角色：学生(STUDENT)、企业(COMPANY)、管理员(ADMIN)
- 学生注册、企业注册（需审核）
- 密码重置

### 2. 企业管理
- 企业信息管理
- 企业审核（管理员）
- 企业状态：待审核、已通过、已拒绝

### 3. 职位管理
- 职位发布与编辑
- 职位搜索（按行业、类型、地点、关键词）
- 职位状态管理

### 4. 简历管理
- 在线简历创建与编辑
- 简历文件上传
- 默认简历设置

### 5. 申请管理
- 学生投递简历
- 企业查看申请
- 申请状态更新

### 6. 面试管理
- 企业安排面试
- 学生响应面试邀请
- 面试状态跟踪

### 7. 招聘会管理
- 招聘会创建
- 活动报名
- 展位管理
- 签到功能

### 8. 通知系统
- 系统通知推送
- 已读/未读状态

### 9. 评价系统
- 学生评价企业
- 评价可见性管理

### 10. 数据统计与导出
- 统计数据汇总
- 数据导出功能
- 审计日志

## 最近修复的问题

### ✅ 已修复

1. **JWT配置统一**
   - 修复了 `application.yml` 中的JWT配置键
   - 从 `jwt.secret` 改为 `security.jwt.secret`
   - 从 `jwt.expiration` 改为 `security.jwt.expiration-ms`
   - 添加了Base64编码的默认密钥

2. **全局异常处理**
   - 新增 `GlobalExceptionHandler` 类
   - 统一处理 `ResponseStatusException`
   - 处理认证异常 `BadCredentialsException`
   - 处理权限异常 `AccessDeniedException`
   - 处理参数校验异常 `MethodArgumentNotValidException`
   - 统一返回 `ApiResponse` 格式

3. **跨域配置**
   - 新增 `CorsConfig` 类
   - 支持所有源（开发环境）
   - 配置允许的HTTP方法和请求头
   - 支持携带凭证

4. **缺失的Repository**
   - 创建 `FileResourceRepository` 接口

## 项目结构

```
backend/
├── src/main/java/com/campus/jobfair/
│   ├── config/              # 配置类
│   │   ├── SecurityConfig.java
│   │   ├── CorsConfig.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── DataInitializer.java
│   ├── controller/          # 控制器层
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   │   └── enums/          # 枚举类型
│   ├── repository/          # 数据访问层
│   ├── security/            # 安全相关
│   │   ├── JwtService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── CustomUserDetails.java
│   │   └── CustomUserDetailsService.java
│   └── service/             # 业务逻辑层
└── src/main/resources/
    └── application.yml      # 应用配置
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+

### 2. 运行项目

```bash
# 进入项目目录
cd backend

# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

### 3. 访问应用

- **应用地址**: http://localhost:8080
- **H2控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:jobfair`
  - 用户名: `sa`
  - 密码: (留空)

### 4. 默认测试账号

系统启动时会自动初始化以下测试账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员 |
| 学生 | 20250001 | student123 | 示例学生 |
| 企业 | CRED-001 | company123 | 示例企业（已审核） |

## API 端点

### 公开接口（无需认证）

- `POST /auth/register/student` - 学生注册
- `POST /auth/register/company` - 企业注册
- `POST /auth/login` - 登录
- `POST /auth/reset-password` - 重置密码
- `GET /announcements` - 公告列表
- `GET /jobs` - 职位列表
- `GET /companies` - 企业列表
- `GET /job-fairs` - 招聘会列表

### 学生接口（需要STUDENT角色）

- `GET /students/me` - 获取个人信息
- `PUT /students/me` - 更新个人信息
- `GET /students/me/applications` - 我的申请
- `POST /students/me/applications` - 投递简历
- `GET /resumes` - 我的简历列表
- `POST /resumes` - 创建简历

### 企业接口（需要COMPANY角色）

- `GET /companies/me` - 获取企业信息
- `PUT /companies/me` - 更新企业信息
- `POST /jobs` - 发布职位
- `PUT /jobs/{id}` - 更新职位
- `POST /interviews` - 安排面试

### 管理员接口（需要ADMIN角色）

- `GET /admin/companies/pending` - 待审核企业
- `POST /admin/companies/{id}/approve` - 审核企业
- `GET /admin/stats/summary` - 统计数据
- `GET /admin/audit-logs` - 审计日志
- `POST /admin/exports` - 数据导出

## 配置说明

### 数据库配置

开发环境默认使用H2内存数据库，生产环境可切换到MySQL：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jobfair?useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### JWT配置

```yaml
security:
  jwt:
    secret: your-base64-encoded-secret-key
    expiration-ms: 604800000  # 7天
```

### 文件存储配置

```yaml
storage:
  upload-dir: uploads  # 文件上传目录
```

## 开发建议

1. **生产环境部署前**：
   - 修改JWT密钥为强密钥
   - 配置生产数据库
   - 调整CORS配置，限制允许的源
   - 关闭H2控制台
   - 配置日志级别

2. **安全建议**：
   - 定期更新依赖版本
   - 使用HTTPS
   - 实施API限流
   - 添加验证码功能

3. **性能优化**：
   - 添加Redis缓存
   - 配置数据库连接池
   - 实施分页查询
   - 添加索引优化

## 许可证

MIT License
