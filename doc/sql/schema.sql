-- =============================================
-- 校企慧公共服务平台 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS `sep_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sep_platform`;

-- =============================================
-- 系统管理模块
-- =============================================

-- 用户表
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `gender` TINYINT DEFAULT 1 COMMENT '性别 1-男 2-女',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `introduction` VARCHAR(500) COMMENT '个人简介',
    `user_type` TINYINT NOT NULL DEFAULT 1 COMMENT '用户类型 1-求职者 2-在校学生 3-企业HR 4-辅导员 5-培训讲师 6-管理员',
    `org_id` BIGINT COMMENT '所属组织ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `login_fail_count` INT DEFAULT 0 COMMENT '登录失败次数',
    `lock_time` DATETIME COMMENT '锁定时间',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB COMMENT='用户表';

-- 组织机构表
CREATE TABLE `sys_org` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `org_name` VARCHAR(100) NOT NULL COMMENT '组织名称',
    `org_type` TINYINT NOT NULL COMMENT '组织类型 1-高校 2-企业 3-政府部门',
    `org_code` VARCHAR(50) COMMENT '组织编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_email` VARCHAR(100) COMMENT '联系邮箱',
    `address` VARCHAR(255) COMMENT '地址',
    `introduction` TEXT COMMENT '简介',
    `logo` VARCHAR(255) COMMENT 'Logo URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_org_type` (`org_type`)
) ENGINE=InnoDB COMMENT='组织机构表';

-- 角色表
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL COMMENT '角色标识',
    `description` VARCHAR(255) COMMENT '角色描述',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据范围 1-全部 2-本组织 3-本人',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB COMMENT='角色表';

-- 菜单/权限表
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `menu_type` TINYINT NOT NULL COMMENT '菜单类型 1-目录 2-菜单 3-按钮',
    `path` VARCHAR(200) COMMENT '路由地址',
    `component` VARCHAR(200) COMMENT '组件路径',
    `permission` VARCHAR(100) COMMENT '权限标识',
    `icon` VARCHAR(100) COMMENT '图标',
    `visible` TINYINT DEFAULT 1 COMMENT '是否可见 0-隐藏 1-显示',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='菜单权限表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB COMMENT='角色菜单关联表';

-- 操作日志表
CREATE TABLE `sys_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `module` VARCHAR(50) COMMENT '模块',
    `operation` VARCHAR(100) COMMENT '操作',
    `method` VARCHAR(200) COMMENT '方法',
    `params` TEXT COMMENT '请求参数',
    `result` TEXT COMMENT '返回结果',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `execute_time` INT COMMENT '执行时长(ms)',
    `status` TINYINT COMMENT '状态 0-失败 1-成功',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB COMMENT='操作日志表';
