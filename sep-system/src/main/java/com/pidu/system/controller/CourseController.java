package com.pidu.system.controller;

import com.pidu.auth.annotation.RequireLogin;
import com.pidu.common.result.Result;
import com.pidu.common.util.SecurityContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 课程控制器
 */
@Slf4j
@Api(tags = "课程管理")
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取课程列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer courseType,
            @RequestParam(required = false) Integer status) {
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, u.real_name as teacher_name, o.org_name " +
            "FROM learn_course c " +
            "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
            "LEFT JOIN sys_org o ON c.org_id = o.id " +
            "WHERE c.deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (c.course_name LIKE ? OR c.introduction LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (courseType != null) {
            sql.append("AND c.course_type = ? ");
            params.add(courseType);
        }
        if (status != null) {
            sql.append("AND c.status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY c.sort DESC, c.create_time DESC");
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @ApiOperation("获取课程详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        String sql = "SELECT c.*, u.real_name as teacher_name, o.org_name " +
                "FROM learn_course c " +
                "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
                "LEFT JOIN sys_org o ON c.org_id = o.id WHERE c.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return Result.success(list.isEmpty() ? null : list.get(0));
    }

    @ApiOperation("创建/更新课程(仅讲师/管理员)")
    @PostMapping("/save")
    @RequireLogin
    public Result<Long> save(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        // 权限检查：只有讲师(5)和管理员(6)可以创建课程
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限创建课程");
        }
        
        Long id = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;
        
        if (id != null) {
            String sql = "UPDATE learn_course SET course_name=?, cover_url=?, introduction=?, description=?, " +
                    "course_type=?, difficulty=?, duration=?, is_free=?, price=?, status=?, " +
                    "update_by=?, update_time=NOW() WHERE id=?";
            jdbcTemplate.update(sql,
                data.get("courseName"), data.get("coverUrl"), data.get("introduction"), data.get("description"),
                data.get("courseType"), data.get("difficulty"), data.get("duration"),
                data.get("isFree"), data.get("price"), data.get("status"), userId, id);
        } else {
            id = System.currentTimeMillis();
            // 获取用户所属组织
            String orgSql = "SELECT org_id FROM sys_user WHERE id = ?";
            List<Map<String, Object>> orgList = jdbcTemplate.queryForList(orgSql, userId);
            Long orgId = orgList.isEmpty() || orgList.get(0).get("org_id") == null ? 
                    null : Long.valueOf(orgList.get(0).get("org_id").toString());
            
            String sql = "INSERT INTO learn_course (id, course_name, cover_url, introduction, description, " +
                    "teacher_id, org_id, course_type, difficulty, duration, is_free, price, status, " +
                    "create_by, create_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())";
            jdbcTemplate.update(sql, id,
                data.get("courseName"), data.get("coverUrl"), data.get("introduction"), data.get("description"),
                userId, orgId, data.get("courseType"), data.get("difficulty"), data.get("duration"),
                data.get("isFree"), data.get("price"), data.get("status"), userId);
        }
        return Result.success(id);
    }

    @ApiOperation("记录课程学习(学生浏览课程时调用)")
    @PostMapping("/learn/{courseId}")
    @RequireLogin
    public Result<Void> recordLearning(@PathVariable Long courseId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // 检查是否已有学习记录
        String checkSql = "SELECT id, total_learn_time FROM learn_user_course WHERE user_id = ? AND course_id = ? AND deleted = 0";
        List<Map<String, Object>> existing = jdbcTemplate.queryForList(checkSql, userId, courseId);
        
        if (existing.isEmpty()) {
            // 创建新的学习记录
            long id = System.currentTimeMillis();
            String insertSql = "INSERT INTO learn_user_course (id, user_id, course_id, progress, total_learn_time, " +
                    "last_learn_time, create_by, create_time) VALUES (?,?,?,0,0,NOW(),?,NOW())";
            jdbcTemplate.update(insertSql, id, userId, courseId, userId);
            
            // 更新课程学习人数
            jdbcTemplate.update("UPDATE learn_course SET student_count = student_count + 1 WHERE id = ?", courseId);
        } else {
            // 更新学习时间
            Long recordId = Long.valueOf(existing.get(0).get("id").toString());
            String updateSql = "UPDATE learn_user_course SET last_learn_time = NOW(), " +
                    "total_learn_time = total_learn_time + 60 WHERE id = ?"; // 每次调用增加60秒
            jdbcTemplate.update(updateSql, recordId);
        }
        
        return Result.success();
    }

    @ApiOperation("更新学习进度")
    @PostMapping("/progress/{courseId}")
    @RequireLogin
    public Result<Void> updateProgress(@PathVariable Long courseId, @RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer progress = Integer.valueOf(data.get("progress").toString());
        Integer learnTime = data.get("learnTime") != null ? Integer.valueOf(data.get("learnTime").toString()) : 0;
        
        String sql = "UPDATE learn_user_course SET progress = ?, total_learn_time = total_learn_time + ?, " +
                "last_learn_time = NOW(), is_completed = ?, completed_time = IF(? = 100, NOW(), completed_time) " +
                "WHERE user_id = ? AND course_id = ?";
        jdbcTemplate.update(sql, progress, learnTime, progress >= 100 ? 1 : 0, progress, userId, courseId);
        
        return Result.success();
    }

    @ApiOperation("获取我的学习记录")
    @GetMapping("/my-learning")
    @RequireLogin
    public Result<List<Map<String, Object>>> getMyLearning() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT uc.*, c.course_name, c.cover_url, c.introduction, c.difficulty, " +
                "u.real_name as teacher_name " +
                "FROM learn_user_course uc " +
                "LEFT JOIN learn_course c ON uc.course_id = c.id " +
                "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
                "WHERE uc.user_id = ? AND uc.deleted = 0 ORDER BY uc.last_learn_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("获取学习统计(讲师/管理员)")
    @GetMapping("/statistics")
    @RequireLogin
    public Result<Map<String, Object>> getStatistics() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        Map<String, Object> stats = new HashMap<>();
        
        if (userType == 5) { // 讲师只看自己的课程
            String courseSql = "SELECT COUNT(*) as total FROM learn_course WHERE teacher_id = ? AND deleted = 0";
            stats.put("courseCount", jdbcTemplate.queryForMap(courseSql, userId).get("total"));
            
            String studentSql = "SELECT COALESCE(SUM(student_count), 0) as total FROM learn_course WHERE teacher_id = ? AND deleted = 0";
            stats.put("studentCount", jdbcTemplate.queryForMap(studentSql, userId).get("total"));
        } else { // 管理员看全部
            String courseSql = "SELECT COUNT(*) as total FROM learn_course WHERE deleted = 0";
            stats.put("courseCount", jdbcTemplate.queryForMap(courseSql).get("total"));
            
            String studentSql = "SELECT COUNT(DISTINCT user_id) as total FROM learn_user_course WHERE deleted = 0";
            stats.put("studentCount", jdbcTemplate.queryForMap(studentSql).get("total"));
        }
        
        return Result.success(stats);
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<Void> delete(@PathVariable Long id) {
        Integer userType = SecurityContextUtil.getCurrentUserType();
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限删除课程");
        }
        jdbcTemplate.update("UPDATE learn_course SET deleted = 1 WHERE id = ?", id);
        return Result.success();
    }
}
