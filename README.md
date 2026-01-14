# `实训课设`校企慧公共服务平台

郫都区校企人力资源合作暨高技能人才培训联盟工作平台

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

## 本地配置

### 1. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE sep_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 执行SQL脚本
mysql -uroot -p sep_platform < doc/sql/schema.sql
mysql -uroot -p sep_platform < doc/sql/schema_business.sql
mysql -uroot -p sep_platform < doc/sql/init_data.sql
```

### 2. 修改配置

- 检查idea：jdk和mvn设置，重新编译各pom文件下依赖；
- 编辑 `sep-system/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sep_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的数据库密码  # 修改为你的密码
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码则填写
  cos:
  client:
    secretId: ${TENCENT_COS_SECRET_ID}   
    secretKey: ${TENCENT_COS_SECRET_KEY}
    region: ap-shanghai
    bucket: 
    url:
```

### 3. 启动后端

```bash
# 编译项目
mvn clean install -DskipTests

# 启动服务
mvn spring-boot:run -pl sep-system
```

后端启动成功后访问：
- API服务：http://localhost:8081
- API文档：http://localhost:8081/doc.html

### 4. 初始化管理员密码

首次启动后，访问以下地址初始化管理员密码：
```
http://localhost:8081/auth/init-admin
```

### 5. 启动前端

```bash
rm -rf node_modules
rm -f package-lock.json   

npm cache clean --force

npm install

npm run dev
```

前端访问地址：http://localhost:7777

## 测试账号

所有账号密码均为：`admin123`

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 系统管理员 | 拥有所有权限 |
| jobseeker | admin123 | 求职者 | 社会个人用户 |
| student | admin123 | 在校学生 | 高校学生用户 |
| hr | admin123 | 企业HR | 企业招聘负责人 |
| counselor | admin123 | 辅导员 | 高校辅导员 |
| teacher | admin123 | 培训讲师 | 课程讲师 |
| auditor | admin123 | 政府审核员 | 审核补贴申报 |

## 后端服务结构

```
├── sep-common/        # 公共模块（工具类、实体、异常等）
├── sep-auth/          # 认证授权模块
├── sep-system/        # 系统管理模块（用户、组织、角色、菜单）
├── sep-recruitment/   # 招聘系统模块
├── sep-learning/      # 在线学习模块
├── sep-application/   # 业务申报模块
├── sep-cms/           # 内容管理模块
├── sep-gateway/       # API网关模块
├── sep-web/           # 前端项目（Vue3 + Element Plus）
└── doc/sql/           # 数据库脚本（建表sql、测试数据用例）
```

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis Plus 3.5.3
- MySQL 8.0
- Redis
- JWT
- Knife4j (API文档)
- COS
- ELK

### 前端
- Vue 3
- Element Plus
- Vite
- Pinia
- Vue Router

## 生产部署

### 后端打包
```bash
mvn clean package -DskipTests
java -jar sep-system/target/sep-system-1.0.0.jar
```

### 前端打包
```bash
npm run build
# 将 dist 目录部署到 Nginx
```

## 常见问题

### 1. 登录提示密码错误
访问 `http://localhost:8081/auth/init-admin` 重置管理员密码

### 2. 前端请求404
确保后端服务已启动在8081端口

### 3. [后端链接](https://github.com/reqwaaaaa/SEP_Vue)