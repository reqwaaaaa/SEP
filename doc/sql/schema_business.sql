-- =============================================
-- 招聘模块
-- =============================================

-- 职位表
CREATE TABLE `rec_job` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `job_name` VARCHAR(100) NOT NULL COMMENT '职位名称',
    `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
    `job_type` TINYINT NOT NULL COMMENT '职位类型 1-全职 2-兼职 3-实习',
    `work_place` VARCHAR(100) COMMENT '工作地点',
    `salary_min` DECIMAL(10,2) COMMENT '最低薪资',
    `salary_max` DECIMAL(10,2) COMMENT '最高薪资',
    `education` TINYINT DEFAULT 1 COMMENT '学历要求 1-不限 2-大专 3-本科 4-硕士 5-博士',
    `experience` TINYINT DEFAULT 0 COMMENT '工作经验 0-不限 1-1年以下 2-1-3年 3-3-5年 4-5-10年 5-10年以上',
    `recruit_num` INT DEFAULT 1 COMMENT '招聘人数',
    `description` TEXT COMMENT '职位描述',
    `requirement` TEXT COMMENT '任职要求',
    `benefits` VARCHAR(500) COMMENT '福利待遇(JSON)',
    `contact_person` VARCHAR(50) COMMENT '联系人',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    `contact_email` VARCHAR(100) COMMENT '联系邮箱',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架 2-已满',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `apply_count` INT DEFAULT 0 COMMENT '投递次数',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_enterprise_id` (`enterprise_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='职位表';

-- 简历表
CREATE TABLE `rec_resume` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `resume_name` VARCHAR(50) COMMENT '简历名称',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `gender` TINYINT COMMENT '性别 1-男 2-女',
    `birthday` DATE COMMENT '出生日期',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `education` TINYINT COMMENT '最高学历',
    `school` VARCHAR(100) COMMENT '毕业院校',
    `major` VARCHAR(100) COMMENT '专业',
    `graduate_year` INT COMMENT '毕业年份',
    `work_years` INT DEFAULT 0 COMMENT '工作年限',
    `expect_job` VARCHAR(100) COMMENT '期望职位',
    `expect_salary` INT COMMENT '期望薪资',
    `expect_place` VARCHAR(100) COMMENT '期望工作地点',
    `job_status` TINYINT COMMENT '求职状态 1-在职看机会 2-离职找工作 3-在校学生',
    `self_evaluation` TEXT COMMENT '自我评价',
    `work_experience` TEXT COMMENT '工作经历(JSON)',
    `project_experience` TEXT COMMENT '项目经历(JSON)',
    `education_experience` TEXT COMMENT '教育经历(JSON)',
    `skills` VARCHAR(500) COMMENT '技能特长(JSON)',
    `attachment_url` VARCHAR(255) COMMENT '附件简历URL',
    `is_public` TINYINT DEFAULT 1 COMMENT '是否公开',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认简历',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='简历表';

-- 职位申请表
CREATE TABLE `rec_job_application` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `job_id` BIGINT NOT NULL COMMENT '职位ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `user_id` BIGINT NOT NULL COMMENT '求职者用户ID',
    `enterprise_id` BIGINT NOT NULL COMMENT '企业ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1-待查看 2-已查看 3-通过筛选 4-面试邀请 5-已录用 6-不合适',
    `hr_remark` VARCHAR(500) COMMENT 'HR备注',
    `interview_time` DATETIME COMMENT '面试时间',
    `interview_place` VARCHAR(200) COMMENT '面试地点',
    `interview_remark` VARCHAR(500) COMMENT '面试备注',
    `user_read` TINYINT DEFAULT 0 COMMENT '求职者是否已读',
    `hr_read` TINYINT DEFAULT 0 COMMENT 'HR是否已读',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_job_id` (`job_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_enterprise_id` (`enterprise_id`)
) ENGINE=InnoDB COMMENT='职位申请表';

-- =============================================
-- 在线学习模块
-- =============================================

-- 课程表
CREATE TABLE `learn_course` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `cover_url` VARCHAR(255) COMMENT '课程封面',
    `introduction` VARCHAR(500) COMMENT '课程简介',
    `description` TEXT COMMENT '课程详情',
    `category_id` BIGINT COMMENT '分类ID',
    `teacher_id` BIGINT COMMENT '讲师ID',
    `org_id` BIGINT COMMENT '所属组织ID',
    `course_type` TINYINT DEFAULT 1 COMMENT '课程类型 1-公共课程 2-本校课程',
    `difficulty` TINYINT DEFAULT 1 COMMENT '难度 1-入门 2-初级 3-中级 4-高级',
    `duration` INT DEFAULT 0 COMMENT '课程时长(分钟)',
    `chapter_count` INT DEFAULT 0 COMMENT '章节数',
    `lesson_count` INT DEFAULT 0 COMMENT '课时数',
    `student_count` INT DEFAULT 0 COMMENT '学习人数',
    `rating` DECIMAL(2,1) DEFAULT 0 COMMENT '评分',
    `rating_count` INT DEFAULT 0 COMMENT '评价人数',
    `is_free` TINYINT DEFAULT 1 COMMENT '是否免费',
    `price` DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-待审核 2-已发布 3-已下架',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB COMMENT='课程表';

-- 课程章节表
CREATE TABLE `learn_course_chapter` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_name` VARCHAR(100) NOT NULL COMMENT '章节名称',
    `introduction` VARCHAR(500) COMMENT '章节简介',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB COMMENT='课程章节表';

-- 课程课时表
CREATE TABLE `learn_course_lesson` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `chapter_id` BIGINT NOT NULL COMMENT '章节ID',
    `lesson_name` VARCHAR(100) NOT NULL COMMENT '课时名称',
    `lesson_type` TINYINT DEFAULT 1 COMMENT '课时类型 1-视频 2-文档 3-音频',
    `resource_url` VARCHAR(255) COMMENT '资源URL',
    `duration` INT DEFAULT 0 COMMENT '时长(秒)',
    `is_free` TINYINT DEFAULT 0 COMMENT '是否可试看',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_chapter_id` (`chapter_id`)
) ENGINE=InnoDB COMMENT='课程课时表';

-- 用户课程表
CREATE TABLE `learn_user_course` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `progress` INT DEFAULT 0 COMMENT '学习进度(%)',
    `learned_lesson_count` INT DEFAULT 0 COMMENT '已学课时数',
    `total_learn_time` INT DEFAULT 0 COMMENT '总学习时长(秒)',
    `last_lesson_id` BIGINT COMMENT '最后学习课时ID',
    `last_learn_time` DATETIME COMMENT '最后学习时间',
    `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成',
    `completed_time` DATETIME COMMENT '完成时间',
    `certificate_url` VARCHAR(255) COMMENT '证书URL',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_course` (`user_id`, `course_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB COMMENT='用户课程表';

-- 考试表
CREATE TABLE `learn_exam` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `exam_name` VARCHAR(100) NOT NULL COMMENT '考试名称',
    `course_id` BIGINT COMMENT '关联课程ID',
    `description` TEXT COMMENT '考试说明',
    `duration` INT DEFAULT 60 COMMENT '考试时长(分钟)',
    `total_score` INT DEFAULT 100 COMMENT '总分',
    `pass_score` INT DEFAULT 60 COMMENT '及格分数',
    `question_count` INT DEFAULT 0 COMMENT '题目数量',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `allow_times` INT DEFAULT 0 COMMENT '允许考试次数 0-不限',
    `show_answer` TINYINT DEFAULT 0 COMMENT '是否显示答案',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-已发布 2-已结束',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB COMMENT='考试表';

-- =============================================
-- 业务申报模块
-- =============================================

-- 补贴申报表
CREATE TABLE `app_subsidy_application` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `application_no` VARCHAR(50) NOT NULL COMMENT '申报编号',
    `application_type` TINYINT NOT NULL COMMENT '申报类型 1-高技能人才培训补贴 2-获奖项目启动资金补贴 3-创业项目启动资金补贴 4-技能大师工作室',
    `enterprise_id` BIGINT NOT NULL COMMENT '申报企业ID',
    `user_id` BIGINT NOT NULL COMMENT '申报人用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '申报标题',
    `amount` DECIMAL(12,2) COMMENT '申报金额',
    `description` TEXT COMMENT '申报说明',
    `attachments` TEXT COMMENT '附件材料(JSON)',
    `current_node` TINYINT DEFAULT 1 COMMENT '当前审核节点',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-待审核 1-审核中 2-审核通过 3-审核不通过 4-候补',
    `final_remark` VARCHAR(500) COMMENT '最终审核意见',
    `audit_time` DATETIME COMMENT '审核完成时间',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_application_no` (`application_no`),
    KEY `idx_enterprise_id` (`enterprise_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='补贴申报表';

-- 审核记录表
CREATE TABLE `app_audit_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `application_id` BIGINT NOT NULL COMMENT '申报ID',
    `audit_node` TINYINT NOT NULL COMMENT '审核节点 1-初审 2-复审 3-终审',
    `auditor_id` BIGINT NOT NULL COMMENT '审核人ID',
    `auditor_name` VARCHAR(50) COMMENT '审核人姓名',
    `audit_result` TINYINT NOT NULL COMMENT '审核结果 1-通过 2-不通过 3-候补 4-退回修改',
    `audit_remark` VARCHAR(500) COMMENT '审核意见',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_application_id` (`application_id`)
) ENGINE=InnoDB COMMENT='审核记录表';

-- =============================================
-- CMS内容管理模块
-- =============================================

-- 站点表
CREATE TABLE `cms_site` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `site_name` VARCHAR(100) NOT NULL COMMENT '站点名称',
    `site_type` TINYINT NOT NULL COMMENT '站点类型 1-主站 2-高校子站 3-企业子站',
    `org_id` BIGINT COMMENT '关联组织ID',
    `domain` VARCHAR(100) COMMENT '站点域名',
    `logo` VARCHAR(255) COMMENT '站点Logo',
    `description` VARCHAR(500) COMMENT '站点描述',
    `keywords` VARCHAR(255) COMMENT 'SEO关键词',
    `template_id` BIGINT COMMENT '模板ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_org_id` (`org_id`)
) ENGINE=InnoDB COMMENT='站点表';

-- 栏目表
CREATE TABLE `cms_channel` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `site_id` BIGINT NOT NULL COMMENT '所属站点ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `channel_name` VARCHAR(50) NOT NULL COMMENT '栏目名称',
    `channel_code` VARCHAR(50) COMMENT '栏目编码',
    `channel_type` TINYINT DEFAULT 1 COMMENT '栏目类型 1-列表栏目 2-单页栏目 3-外链栏目',
    `link_url` VARCHAR(255) COMMENT '外链地址',
    `icon` VARCHAR(100) COMMENT '栏目图标',
    `description` VARCHAR(255) COMMENT '栏目描述',
    `template_id` BIGINT COMMENT '模板ID',
    `show_in_nav` TINYINT DEFAULT 1 COMMENT '是否在导航显示',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_site_id` (`site_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB COMMENT='栏目表';

-- 文章表
CREATE TABLE `cms_article` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `site_id` BIGINT NOT NULL COMMENT '所属站点ID',
    `channel_id` BIGINT NOT NULL COMMENT '所属栏目ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `sub_title` VARCHAR(200) COMMENT '副标题',
    `summary` VARCHAR(500) COMMENT '摘要',
    `cover_url` VARCHAR(255) COMMENT '封面图URL',
    `content` LONGTEXT COMMENT '文章内容',
    `author` VARCHAR(50) COMMENT '作者',
    `source` VARCHAR(100) COMMENT '来源',
    `keywords` VARCHAR(255) COMMENT '关键词',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `publish_time` DATETIME COMMENT '发布时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-草稿 1-待审核 2-已发布 3-已下架',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_site_id` (`site_id`),
    KEY `idx_channel_id` (`channel_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB COMMENT='文章表';


-- =============================================
-- 考试题目相关表
-- =============================================

-- 考试题目表
CREATE TABLE `learn_exam_question` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `question_type` TINYINT NOT NULL COMMENT '题目类型 1-单选 2-多选 3-判断 4-填空 5-简答',
    `question_content` TEXT NOT NULL COMMENT '题目内容',
    `options` TEXT COMMENT '选项(JSON格式)',
    `correct_answer` TEXT COMMENT '正确答案',
    `analysis` TEXT COMMENT '答案解析',
    `score` INT DEFAULT 0 COMMENT '分值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB COMMENT='考试题目表';

-- 用户考试记录表
CREATE TABLE `learn_user_exam` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `start_time` DATETIME COMMENT '开始时间',
    `submit_time` DATETIME COMMENT '提交时间',
    `score` INT COMMENT '得分',
    `is_pass` TINYINT COMMENT '是否及格',
    `answers` TEXT COMMENT '答题记录(JSON)',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-进行中 1-已提交 2-已批改',
    `grader_id` BIGINT COMMENT '批改人ID',
    `grade_time` DATETIME COMMENT '批改时间',
    `grade_remark` VARCHAR(500) COMMENT '批改备注',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB COMMENT='用户考试记录表';

-- =============================================
-- 消息模块
-- =============================================

-- 私信会话表
CREATE TABLE `msg_conversation` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_a_id` BIGINT NOT NULL COMMENT '用户A ID',
    `user_b_id` BIGINT NOT NULL COMMENT '用户B ID',
    `last_message_id` BIGINT COMMENT '最后一条消息ID',
    `last_message_time` DATETIME COMMENT '最后消息时间',
    `user_a_unread` INT DEFAULT 0 COMMENT '用户A未读数',
    `user_b_unread` INT DEFAULT 0 COMMENT '用户B未读数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users` (`user_a_id`, `user_b_id`),
    KEY `idx_user_a` (`user_a_id`),
    KEY `idx_user_b` (`user_b_id`)
) ENGINE=InnoDB COMMENT='私信会话表';

-- 私信消息表
CREATE TABLE `msg_private_message` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` TINYINT DEFAULT 1 COMMENT '消息类型 1-文本 2-图片 3-文件',
    `file_url` VARCHAR(255) COMMENT '文件URL',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB COMMENT='私信消息表';

-- 系统通知表
CREATE TABLE `msg_notification` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `notify_type` TINYINT DEFAULT 1 COMMENT '通知类型 1-系统通知 2-业务通知 3-审核通知',
    `biz_type` VARCHAR(50) COMMENT '业务类型',
    `biz_id` BIGINT COMMENT '业务ID',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB COMMENT='系统通知表';

-- 课件资源表
CREATE TABLE `learn_resource` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `course_id` BIGINT COMMENT '关联课程ID',
    `lesson_id` BIGINT COMMENT '关联课时ID',
    `resource_name` VARCHAR(200) NOT NULL COMMENT '资源名称',
    `resource_type` TINYINT NOT NULL COMMENT '资源类型 1-视频 2-文档 3-PPT 4-PDF 5-音频 6-其他',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件URL',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_ext` VARCHAR(20) COMMENT '文件扩展名',
    `duration` INT COMMENT '时长(秒,视频/音频)',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `uploader_id` BIGINT COMMENT '上传者ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 0-处理中 1-正常 2-失败',
    `create_by` BIGINT COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_lesson_id` (`lesson_id`)
) ENGINE=InnoDB COMMENT='课件资源表';


-- 学习记录表(用于统计)
CREATE TABLE IF NOT EXISTS `learning_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `lesson_id` BIGINT COMMENT '课时ID',
    `learn_duration` INT DEFAULT 0 COMMENT '学习时长(秒)',
    `progress` INT DEFAULT 0 COMMENT '学习进度(%)',
    `last_position` INT DEFAULT 0 COMMENT '上次播放位置(秒)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB COMMENT='学习记录表';

-- 考试记录表(用于统计)
CREATE TABLE IF NOT EXISTS `exam_record` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `score` INT COMMENT '得分',
    `is_pass` TINYINT COMMENT '是否及格',
    `start_time` DATETIME COMMENT '开始时间',
    `submit_time` DATETIME COMMENT '提交时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态 0-进行中 1-已提交',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB COMMENT='考试记录表';

-- 职位申请表(简化版,用于统计)
CREATE TABLE IF NOT EXISTS `job_application` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `job_id` BIGINT NOT NULL COMMENT '职位ID',
    `resume_id` BIGINT COMMENT '简历ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态 1-待查看 2-已查看 3-面试 4-录用 5-不合适',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_job_id` (`job_id`)
) ENGINE=InnoDB COMMENT='职位申请表';

-- 添加通知表的create_by字段(如果不存在)
ALTER TABLE `msg_notification` ADD COLUMN IF NOT EXISTS `create_by` BIGINT COMMENT '创建人';
