-- 动态菜单权限管理系统 - 数据库初始化脚本
-- 使用 JPA 时，实体类会通过 ddl-auto=update 自动建表
-- 此脚本仅用于初始化菜单/角色数据

-- 创建数据库
CREATE DATABASE IF NOT EXISTS admin_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE admin_system;

-- ==============================================================
-- 1. 用户表（由 JPA 自动创建，可手动补充初始用户）
-- ==============================================================
INSERT INTO sys_user (id, username, password, nickname, email, status, create_time, update_time)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 'admin@yourcompany.com', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE username=username;

-- ==============================================================
-- 2. 角色表
-- ==============================================================
INSERT INTO sys_role (id, code, name, remark, status, create_time) VALUES
(1, 'super_admin', '超级管理员', '拥有所有权限', 1, NOW()),
(2, 'admin', '系统管理员', '系统管理权限', 1, NOW()),
(3, 'user', '普通用户', '普通用户权限', 1, NOW())
ON DUPLICATE KEY UPDATE code=code;

-- ==============================================================
-- 3. 用户角色关联表
-- ==============================================================
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- ==============================================================
-- 4. 菜单表（目录/菜单/按钮）
-- ==============================================================
INSERT INTO sys_menu (id, name, permission, path, component, icon, type, parent_id, order_num, status, visible, is_cache, create_time) VALUES
-- 一级目录
(1,  '系统管理', NULL,        '/system',        NULL,                   'Setting',      0, 0, 0, 1, 1, 0, NOW()),
(2,  '用户管理', NULL,        '/system/user',   'system/user/index',    'User',         1, 1, 1, 1, 1, 0, NOW()),
(3,  '角色管理', NULL,        '/system/role',   'system/role/index',    'Role',         1, 1, 2, 1, 1, 0, NOW()),
(4,  '菜单管理', NULL,        '/system/menu',   'system/menu/index',    'Menu',         1, 1, 3, 1, 1, 0, NOW()),
(5,  '用户组管理', NULL,      '/system/group',  'system/group/index',   'Group',        1, 1, 4, 1, 1, 0, NOW()),
(6,  '系统设置', NULL,        '/system/config', 'system/config/index',  'Tools',        1, 1, 5, 1, 1, 0, NOW()),
-- 按钮
(10, '查看用户', 'user:list',   NULL,            NULL,                   NULL,           2, 2, 0, 1, 1, 0, NOW()),
(11, '新增用户', 'user:add',    NULL,            NULL,                   NULL,           2, 2, 0, 1, 1, 0, NOW()),
(12, '编辑用户', 'user:edit',   NULL,            NULL,                   NULL,           2, 2, 0, 1, 1, 0, NOW()),
(13, '删除用户', 'user:delete', NULL,            NULL,                   NULL,           2, 2, 0, 1, 1, 0, NOW()),
(14, '重置密码', 'user:reset',  NULL,            NULL,                   NULL,           2, 2, 0, 1, 1, 0, NOW()),
(15, '查看角色', 'role:list',   NULL,            NULL,                   NULL,           2, 3, 0, 1, 1, 0, NOW()),
(16, '新增角色', 'role:add',    NULL,            NULL,                   NULL,           2, 3, 0, 1, 1, 0, NOW()),
(17, '编辑角色', 'role:edit',   NULL,            NULL,                   NULL,           2, 3, 0, 1, 1, 0, NOW()),
(18, '删除角色', 'role:delete', NULL,            NULL,                   NULL,           2, 3, 0, 1, 1, 0, NOW()),
(19, '查看菜单', 'menu:list',   NULL,            NULL,                   NULL,           2, 4, 0, 1, 1, 0, NOW()),
(20, '新增菜单', 'menu:add',    NULL,            NULL,                   NULL,           2, 4, 0, 1, 1, 0, NOW()),
(21, '编辑菜单', 'menu:edit',   NULL,            NULL,                   NULL,           2, 4, 0, 1, 1, 0, NOW()),
(22, '删除菜单', 'menu:delete', NULL,            NULL,                   NULL,           2, 4, 0, 1, 1, 0, NOW()),
(23, '查看用户组', 'group:list',  NULL,            NULL,                   NULL,           2, 5, 0, 1, 1, 0, NOW()),
(24, '新增用户组', 'group:add',  NULL,            NULL,                   NULL,           2, 5, 0, 1, 1, 0, NOW()),
(25, '编辑用户组', 'group:edit', NULL,            NULL,                   NULL,           2, 5, 0, 1, 1, 0, NOW()),
(26, '删除用户组', 'group:delete', NULL,           NULL,                   NULL,           2, 5, 0, 1, 1, 0, NOW()),
(27, '查看系统设置', 'config:list',  NULL,           NULL,                   NULL,           2, 6, 0, 1, 1, 0, NOW()),
(28, '编辑系统设置', 'config:edit',  NULL,           NULL,                   NULL,           2, 6, 0, 1, 1, 0, NOW())
ON DUPLICATE KEY UPDATE name=name;

-- ==============================================================
-- 5. 角色菜单关联表（给超级管理员赋所有菜单）
-- ==============================================================
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE id > 0
ON DUPLICATE KEY UPDATE role_id=role_id;

-- ==============================================================
-- 6. 用户组表
-- ==============================================================
INSERT INTO sys_group (id, code, name, remark, status, create_time) VALUES
(1, 'tech_team', '技术部', '技术研发团队', 1, NOW()),
(2, 'product_team', '产品部', '产品设计团队', 1, NOW())
ON DUPLICATE KEY UPDATE code=code;

-- ==============================================================
-- 7. 用户组角色关联表
-- ==============================================================
INSERT INTO sys_group_role (group_id, role_id) VALUES (1, 2)
ON DUPLICATE KEY UPDATE group_id=group_id;

-- ==============================================================
-- 8. 系统配置表
-- ==============================================================
INSERT INTO sys_config (id, config_key, config_value, remark, status, create_time, update_time) VALUES
(1, 'system_title', '后台管理系统', '系统标题', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE config_key=config_key;

