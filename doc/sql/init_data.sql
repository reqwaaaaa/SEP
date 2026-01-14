-- =============================================
-- 初始化数据
-- =============================================

USE `sep_platform`;

-- =============================================
-- 初始化用户账号 (所有密码均为: 123456)
-- BCrypt hash for '123456': $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
-- =============================================

-- 用户类型: 1-求职者 2-在校学生 3-企业HR 4-辅导员 5-培训讲师 6-管理员
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `user_type`, `org_id`, `status`) VALUES
-- 管理员
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800000001', 'admin@pidu.com', 6, 1, 1),
-- 求职者
(2, 'jobseeker', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三(求职者)', '13800000002', 'jobseeker@test.com', 1, NULL, 1),
-- 在校学生
(3, 'student', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四(学生)', '13800000003', 'student@cdut.edu.cn', 2, 2, 1),
-- 企业HR
(4, 'hr', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五(HR)', '13800000004', 'hr@company.com', 3, 4, 1),
-- 辅导员
(5, 'counselor', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六(辅导员)', '13800000005', 'counselor@cdut.edu.cn', 4, 2, 1),
-- 培训讲师
(6, 'teacher', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '钱七(讲师)', '13800000006', 'teacher@cdut.edu.cn', 5, 2, 1),
-- 政府审核员
(7, 'auditor', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '孙八(审核员)', '13800000007', 'auditor@pidu.gov.cn', 6, 1, 1);

-- 初始化组织机构
INSERT INTO `sys_org` (`id`, `org_name`, `org_type`, `org_code`, `status`) VALUES
(1, '郫都区人力资源和社会保障局', 3, 'GOV001', 1),
(2, '成都工业学院', 1, 'SCH001', 1),
(3, '四川传媒学院', 1, 'SCH002', 1),
(4, '成都XX科技有限公司', 2, 'ENT001', 1),
(5, '成都YY网络有限公司', 2, 'ENT002', 1);

-- 初始化角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `data_scope`, `status`) VALUES
(1, '超级管理员', 'admin', '拥有所有权限', 1, 1),
(2, '求职者', 'job_seeker', '社会个人用户', 3, 1),
(3, '在校学生', 'student', '高校学生用户', 3, 1),
(4, '企业HR', 'enterprise_hr', '企业招聘负责人', 2, 1),
(5, '辅导员', 'counselor', '高校辅导员', 2, 1),
(6, '培训讲师', 'trainer', '高校培训讲师', 2, 1),
(7, '政府审核员', 'auditor', '政府审核人员', 1, 1);

-- 初始化菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `menu_type`, `path`, `component`, `permission`, `icon`, `sort`) VALUES
-- 系统管理
(100, '系统管理', 0, 1, '/system', NULL, NULL, 'setting', 1),
(101, '用户管理', 100, 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1),
(102, '角色管理', 100, 2, '/system/role', 'system/role/index', 'system:role:list', 'peoples', 2),
(103, '菜单管理', 100, 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 3),
(104, '组织管理', 100, 2, '/system/org', 'system/org/index', 'system:org:list', 'tree', 4),
(105, '日志管理', 100, 2, '/system/log', 'system/log/index', 'system:log:list', 'log', 5),

-- 招聘管理
(200, '招聘管理', 0, 1, '/recruitment', NULL, NULL, 'job', 2),
(201, '职位管理', 200, 2, '/recruitment/job', 'recruitment/job/index', 'recruitment:job:list', 'list', 1),
(202, '简历管理', 200, 2, '/recruitment/resume', 'recruitment/resume/index', 'recruitment:resume:list', 'documentation', 2),
(203, '投递管理', 200, 2, '/recruitment/application', 'recruitment/application/index', 'recruitment:application:list', 'form', 3),

-- 学习中心
(300, '学习中心', 0, 1, '/learning', NULL, NULL, 'education', 3),
(301, '课程管理', 300, 2, '/learning/course', 'learning/course/index', 'learning:course:list', 'skill', 1),
(302, '考试管理', 300, 2, '/learning/exam', 'learning/exam/index', 'learning:exam:list', 'edit', 2),
(303, '学习统计', 300, 2, '/learning/statistics', 'learning/statistics/index', 'learning:statistics:list', 'chart', 3),

-- 业务申报
(400, '业务申报', 0, 1, '/application', NULL, NULL, 'form', 4),
(401, '我的申报', 400, 2, '/application/my', 'application/my/index', 'application:my:list', 'list', 1),
(402, '申报审核', 400, 2, '/application/audit', 'application/audit/index', 'application:audit:list', 'checkbox', 2),
(403, '申报统计', 400, 2, '/application/statistics', 'application/statistics/index', 'application:statistics:list', 'chart', 3),

-- 内容管理
(500, '内容管理', 0, 1, '/cms', NULL, NULL, 'documentation', 5),
(501, '站点管理', 500, 2, '/cms/site', 'cms/site/index', 'cms:site:list', 'international', 1),
(502, '栏目管理', 500, 2, '/cms/channel', 'cms/channel/index', 'cms:channel:list', 'tree', 2),
(503, '文章管理', 500, 2, '/cms/article', 'cms/article/index', 'cms:article:list', 'edit', 3);

-- 用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES 
(1, 1),  -- admin -> 超级管理员
(2, 2),  -- jobseeker -> 求职者
(3, 3),  -- student -> 在校学生
(4, 4),  -- hr -> 企业HR
(5, 5),  -- counselor -> 辅导员
(6, 6),  -- teacher -> 培训讲师
(7, 7);  -- auditor -> 政府审核员

-- 角色菜单关联（管理员拥有所有菜单）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, id FROM `sys_menu`;

-- 初始化CMS站点
INSERT INTO `cms_site` (`id`, `site_name`, `site_type`, `org_id`, `domain`, `status`) VALUES
(1, '郫都区校企人力资源联盟平台', 1, 1, 'www.pidu-hr.com', 1),
(2, '成都工业学院子站', 2, 2, 'cdut.pidu-hr.com', 1),
(3, '四川传媒学院子站', 2, 3, 'scmc.pidu-hr.com', 1);

-- 初始化栏目
INSERT INTO `cms_channel` (`id`, `site_id`, `parent_id`, `channel_name`, `channel_code`, `channel_type`, `sort`) VALUES
(1, 1, 0, '首页', 'home', 2, 1),
(2, 1, 0, '新闻动态', 'news', 1, 2),
(3, 1, 0, '政策法规', 'policy', 1, 3),
(4, 1, 0, '招聘信息', 'jobs', 1, 4),
(5, 1, 0, '培训课程', 'courses', 1, 5),
(6, 1, 0, '补贴申报', 'subsidy', 1, 6),
(7, 1, 0, '关于我们', 'about', 2, 7);

-- =============================================
-- 初始化职位数据
-- =============================================
INSERT INTO `rec_job` (`id`, `job_name`, `enterprise_id`, `job_type`, `work_place`, `salary_min`, `salary_max`, `education`, `experience`, `recruit_num`, `description`, `requirement`, `status`, `create_by`, `create_time`) VALUES
(1, 'Java高级开发工程师', 4, 1, '成都市郫都区', 15000, 25000, 3, 3, 3, '负责公司核心业务系统的开发和维护，参与系统架构设计', '1. 本科及以上学历，计算机相关专业\n2. 3年以上Java开发经验\n3. 熟悉Spring Boot、MyBatis等框架', 1, 4, NOW()),
(2, '前端开发工程师', 4, 1, '成都市郫都区', 12000, 20000, 3, 2, 2, '负责公司Web前端开发，与后端工程师协作完成产品功能', '1. 本科及以上学历\n2. 熟悉Vue.js或React\n3. 良好的沟通能力', 1, 4, NOW()),
(3, '产品经理', 5, 1, '成都市高新区', 15000, 30000, 3, 3, 1, '负责产品规划、需求分析、产品设计', '1. 本科及以上学历\n2. 3年以上产品经验\n3. 有教育行业经验优先', 1, 4, NOW()),
(4, '实习生-软件开发', 4, 3, '成都市郫都区', 3000, 5000, 3, 0, 5, '参与公司项目开发，学习企业级开发流程', '1. 在校大三/大四学生\n2. 计算机相关专业\n3. 有一定编程基础', 1, 4, NOW());

-- =============================================
-- 初始化简历数据
-- =============================================
INSERT INTO `rec_resume` (`id`, `user_id`, `resume_name`, `real_name`, `gender`, `phone`, `email`, `education`, `school`, `major`, `work_years`, `expect_job`, `expect_salary`, `expect_place`, `job_status`, `self_evaluation`, `is_public`, `is_default`, `create_by`, `create_time`) VALUES
(1, 2, '张三的简历', '张三', 1, '13800000002', 'jobseeker@test.com', 3, '成都理工大学', '软件工程', 3, 'Java开发工程师', 15, '成都', 1, '3年Java开发经验，熟悉Spring Boot、MyBatis等主流框架，有良好的编码习惯和团队协作能力。', 1, 1, 2, NOW()),
(2, 3, '李四的简历', '李四', 2, '13800000003', 'student@cdut.edu.cn', 3, '成都工业学院', '计算机科学与技术', 0, '前端开发实习生', 5, '成都', 3, '在校大四学生，熟悉Vue.js和React，有多个课程项目经验，学习能力强。', 1, 1, 3, NOW());

-- =============================================
-- 初始化申报数据
-- =============================================
INSERT INTO `app_subsidy_application` (`id`, `application_no`, `application_type`, `enterprise_id`, `user_id`, `title`, `amount`, `description`, `status`, `create_by`, `create_time`) VALUES
(1, 'SB202601001', 1, 4, 4, '2026年度高技能人才培训补贴申请', 50000, '申请高技能人才培训补贴，用于员工技能提升培训', 0, 4, NOW()),
(2, 'SB202601002', 2, 4, 4, '省级技能大赛一等奖启动资金申请', 100000, '公司员工获得省级技能大赛一等奖，申请启动资金补贴', 0, 4, NOW()),
(3, 'SB202601003', 3, 5, 4, '智能制造创业项目启动资金申请', 200000, '智能制造领域创业项目，申请创业启动资金支持', 2, 4, DATE_SUB(NOW(), INTERVAL 10 DAY));

-- =============================================
-- 初始化课程数据
-- =============================================
INSERT INTO `learn_course` (`id`, `course_name`, `cover_url`, `introduction`, `category_id`, `teacher_id`, `org_id`, `course_type`, `difficulty`, `duration`, `chapter_count`, `lesson_count`, `student_count`, `is_free`, `status`, `create_by`, `create_time`) VALUES
(1, 'Java企业级开发实战', NULL, '从零开始学习Java企业级开发，包括Spring Boot、MyBatis、Redis等技术栈', NULL, 6, 2, 1, 3, 3600, 10, 50, 128, 1, 2, 6, NOW()),
(2, 'Vue.js前端开发入门', NULL, '学习Vue.js框架，掌握现代前端开发技能', NULL, 6, 2, 1, 2, 2400, 8, 32, 256, 1, 2, 6, NOW()),
(3, '数据库设计与优化', NULL, '学习MySQL数据库设计原则和性能优化技巧', NULL, 6, 2, 1, 3, 1800, 6, 24, 89, 1, 2, 6, NOW());
