# SpringBoot Vue RBAC Admin

基于 **SpringBoot 3.2** + **Vue3** 的企业级 RBAC 动态菜单权限管理系统。

## 技术栈

### 后端
| 技术 | 说明 |
|------|------|
| SpringBoot 3.2 | 核心框架 |
| Spring Security 6 | 安全认证（JWT） |
| Spring Data JPA | ORM 持久层 |
| H2 / MySQL | 数据库（开发H2，生产MySQL） |
| jjwt 0.12 | JWT 令牌 |

### 前端
| 技术 | 说明 |
|------|------|
| Vue 3.4 | 渐进式框架 |
| Vite 5 | 构建工具 |
| Element Plus 2.5 | UI 组件库 |
| Pinia | 状态管理 |
| Vue Router 4 | 路由管理 |
| Axios | HTTP 客户端 |

## 项目结构

```
├── backend/                         # SpringBoot 后端
│   └── src/main/java/com/admin/
│       ├── controller/              # REST 接口
│       ├── service/                 # 业务逻辑
│       ├── repository/              # 数据访问层
│       ├── entity/                  # JPA 实体
│       ├── dto/                     # 数据传输对象
│       ├── vo/                      # 视图对象
│       ├── config/                  # 安全配置
│       └── common/                  # 通用工具
│
├── frontend/                        # Vue3 前端
│   └── src/
│       ├── api/                     # 接口封装
│       ├── store/                   # Pinia 状态管理
│       ├── router/                  # 路由配置
│       ├── views/                   # 页面组件
│       ├── layout/                  # 布局组件
│       └── utils/                   # 工具函数
│
└── data/                            # H2 数据库文件
```

## 功能特性

### 权限管理
- ✅ 用户管理：增删改查、分配角色、启用/禁用
- ✅ 角色管理：增删改查、分配菜单权限（树形勾选）
- ✅ 菜单管理：目录/菜单/按钮三级菜单、树形展示
- ✅ 用户组管理：增删改查、分配成员（穿梭框）、分配角色
- ✅ 系统设置：动态系统标题、配置管理

### 系统特性
- ✅ JWT 无状态认证，支持 token 刷新
- ✅ 动态菜单：根据用户角色动态渲染侧边栏
- ✅ 按钮级权限：精确到操作按钮的权限控制
- ✅ 响应式布局：适配不同屏幕尺寸
- ✅ 多环境配置：dev（H2）、prod（MySQL）

## 快速启动

### 前置要求
- JDK 17+
- Node.js 18+
- Maven 3.8+

### 1. 启动后端

```bash
cd backend

# 方式一：Maven 启动（开发环境使用 H2）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 方式二：打包后启动（生产环境使用 MySQL）
mvn clean package -DskipTests
java -jar target/admin-system-1.0.0.jar --spring.profiles.active=prod
```

后端地址：`http://localhost:8080/api`
- H2 控制台：`http://localhost:8080/api/h2-console`

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:3000`

### 3. 数据库初始化（生产环境）

首次启动前，执行 `backend/src/main/resources/init.sql` 初始化 MySQL 数据库。

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |

## API 接口

### 认证接口
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/userInfo` | GET | 获取用户信息 |

### 用户管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/user/page` | GET | 分页查询用户 |
| `/api/system/user/{id}` | GET | 获取用户详情 |
| `/api/system/user` | POST | 新增用户 |
| `/api/system/user/{id}` | PUT | 更新用户 |
| `/api/system/user/{id}` | DELETE | 删除用户 |
| `/api/system/user/{id}/status/{status}` | PUT | 修改用户状态 |
| `/api/system/user/{id}/roles` | PUT | 分配用户角色 |

### 角色管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/role/page` | GET | 分页查询角色 |
| `/api/system/role/{id}` | GET | 获取角色详情 |
| `/api/system/role` | POST | 新增角色 |
| `/api/system/role/{id}` | PUT | 更新角色 |
| `/api/system/role/{id}` | DELETE | 删除角色 |
| `/api/system/role/{id}/menus` | PUT | 分配菜单权限 |

### 菜单管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/menu/tree` | GET | 获取菜单树 |
| `/api/system/menu/{id}` | GET | 获取菜单详情 |
| `/api/system/menu` | POST | 新增菜单 |
| `/api/system/menu/{id}` | PUT | 更新菜单 |
| `/api/system/menu/{id}` | DELETE | 删除菜单 |

### 用户组管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/group/page` | GET | 分页查询用户组 |
| `/api/system/group/{id}` | GET | 获取用户组详情 |
| `/api/system/group` | POST | 新增用户组 |
| `/api/system/group/{id}` | PUT | 更新用户组 |
| `/api/system/group/{id}` | DELETE | 删除用户组 |
| `/api/system/group/{id}/roles` | PUT | 分配用户组角色 |
| `/api/system/group/{id}/users` | PUT | 分配组成员 |

### 系统配置
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/system/config/list` | GET | 获取配置列表 |
| `/api/system/config/title` | GET | 获取系统标题 |
| `/api/system/config/title` | PUT | 设置系统标题 |

## 配置说明

### 开发环境（H2）
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:file:./data/admindb
  jpa:
    hibernate:
      ddl-auto: none
```

### 生产环境（MySQL）
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_system
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
```

## JWT 配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| jwt.secret | 签名密钥 | 自定义密钥 |
| jwt.expiration | token有效期(秒) | 86400 (24h) |
| jwt.refresh-expiration | 刷新token有效期 | 604800 (7d) |

## 项目截图

> 暂无

## License

MIT License
