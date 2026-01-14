-- =============================================
-- 校企慧公共服务平台 数据库更新脚本 V2
-- 添加用户表新字段、岗位推荐表
-- =============================================

USE `sep_platform`;

-- 用户表添加新字段
ALTER TABLE `sys_user` 
ADD COLUMN `gender` TINYINT DEFAULT 1 COMMENT '性别 1-男 2-女' AFTER `real_name`,
ADD COLUMN `introduction` VARCHAR(500) COMMENT '个人简介' AFTER `avatar`;

-- 岗位推荐表
CREATE TABLE IF NOT EXISTS `job_recommendation` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `counselor_id` BIGINT NOT NULL COMMENT '辅导员ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `job_id` BIGINT NOT NULL COMMENT '职位ID',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-待查看 1-已查看 2-已投递 3-已忽略',
    `remark` VARCHAR(500) COMMENT '推荐理由',
    `view_time` DATETIME COMMENT '查看时间',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_counselor_id` (`counselor_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB COMMENT='岗位推荐表';

-- 通知表
CREATE TABLE IF NOT EXISTS `sys_notification` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(20) DEFAULT 'system' COMMENT '通知类型 system-系统 business-业务 audit-审核',
    `target_user_types` VARCHAR(100) COMMENT '目标用户类型，逗号分隔',
    `target_user_id` BIGINT COMMENT '目标用户ID(为空则按类型发送)',
    `publisher_id` BIGINT COMMENT '发布人ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-草稿 1-已发布',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB COMMENT='系统通知表';

-- 用户通知关联表(记录已读状态)
CREATE TABLE IF NOT EXISTS `sys_user_notification` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `notification_id` BIGINT NOT NULL COMMENT '通知ID',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 0-未读 1-已读',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_notification` (`user_id`, `notification_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_notification_id` (`notification_id`)
) ENGINE=InnoDB COMMENT='用户通知关联表';
