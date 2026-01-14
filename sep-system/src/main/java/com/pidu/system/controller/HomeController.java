package com.pidu.system.controller;

import com.pidu.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 首页数据控制器
 */
@Slf4j
@Api(tags = "首页数据")
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取首页统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 用户总数
            String userSql = "SELECT COUNT(*) FROM sys_user WHERE status = 1 AND deleted = 0";
            Integer userCount = jdbcTemplate.queryForObject(userSql, Integer.class);
            stats.put("userCount", userCount != null ? userCount : 0);
            
            // 职位数量
            String jobSql = "SELECT COUNT(*) FROM rec_job WHERE status = 1 AND deleted = 0";
            Integer jobCount = jdbcTemplate.queryForObject(jobSql, Integer.class);
            stats.put("jobCount", jobCount != null ? jobCount : 0);
            
            // 课程数量
            String courseSql = "SELECT COUNT(*) FROM learn_course WHERE status = 2 AND deleted = 0";
            Integer courseCount = jdbcTemplate.queryForObject(courseSql, Integer.class);
            stats.put("courseCount", courseCount != null ? courseCount : 0);
            
            // 申报数量
            String appSql = "SELECT COUNT(*) FROM app_subsidy_application WHERE deleted = 0";
            Integer applicationCount = jdbcTemplate.queryForObject(appSql, Integer.class);
            stats.put("applicationCount", applicationCount != null ? applicationCount : 0);
            
            // 投递数量
            String jobAppSql = "SELECT COUNT(*) FROM job_application";
            Integer jobAppCount = jdbcTemplate.queryForObject(jobAppSql, Integer.class);
            stats.put("jobApplicationCount", jobAppCount != null ? jobAppCount : 0);
            
            // 考试数量
            String examSql = "SELECT COUNT(*) FROM learn_exam WHERE status = 2 AND deleted = 0";
            Integer examCount = jdbcTemplate.queryForObject(examSql, Integer.class);
            stats.put("examCount", examCount != null ? examCount : 0);
            
            // 热门职位
            String hotJobSql = "SELECT j.job_name as name, COUNT(a.id) as count " +
                    "FROM rec_job j LEFT JOIN job_application a ON j.id = a.job_id " +
                    "WHERE j.deleted = 0 GROUP BY j.id, j.job_name " +
                    "ORDER BY count DESC LIMIT 5";
            List<Map<String, Object>> hotJobs = jdbcTemplate.queryForList(hotJobSql);
            stats.put("hotJobs", hotJobs);
            
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            // 返回默认值
            stats.put("userCount", 0);
            stats.put("jobCount", 0);
            stats.put("courseCount", 0);
            stats.put("applicationCount", 0);
        }
        
        return Result.success(stats);
    }

    @ApiOperation("获取最新职位")
    @GetMapping("/latest-jobs")
    public Result<List<Map<String, Object>>> getLatestJobs() {
        try {
            String sql = "SELECT j.id, j.job_name as title, o.org_name as company, " +
                    "CONCAT(j.salary_min, 'K-', j.salary_max, 'K') as salary " +
                    "FROM rec_job j " +
                    "LEFT JOIN sys_org o ON j.enterprise_id = o.id " +
                    "WHERE j.status = 1 AND j.deleted = 0 " +
                    "ORDER BY j.create_time DESC LIMIT 5";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取最新职位失败", e);
            return Result.success(new ArrayList<>());
        }
    }

    @ApiOperation("获取热门课程")
    @GetMapping("/hot-courses")
    public Result<List<Map<String, Object>>> getHotCourses() {
        try {
            String sql = "SELECT c.id, c.course_name as title, u.real_name as teacher, " +
                    "c.student_count as viewCount, c.cover_url as cover " +
                    "FROM learn_course c " +
                    "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
                    "WHERE c.status = 2 AND c.deleted = 0 " +
                    "ORDER BY c.student_count DESC LIMIT 5";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取热门课程失败", e);
            return Result.success(new ArrayList<>());
        }
    }

    @ApiOperation("获取公告列表")
    @GetMapping("/announcements")
    public Result<List<Map<String, Object>>> getAnnouncements() {
        try {
            String sql = "SELECT a.id, a.title, DATE_FORMAT(a.create_time, '%Y-%m-%d') as createTime, " +
                    "CASE WHEN a.is_top = 1 THEN 'important' ELSE 'normal' END as type " +
                    "FROM cms_article a " +
                    "WHERE a.status = 2 AND a.deleted = 0 " +
                    "ORDER BY a.is_top DESC, a.create_time DESC LIMIT 10";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.success(new ArrayList<>());
        }
    }
}
