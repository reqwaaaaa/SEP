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
 * 简历控制器
 */
@Slf4j
@Api(tags = "简历管理")
@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取我的简历")
    @GetMapping("/my")
    @RequireLogin
    public Result<Map<String, Object>> getMyResume() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT * FROM rec_resume WHERE user_id = ? AND deleted = 0 ORDER BY is_default DESC LIMIT 1";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list.isEmpty() ? null : list.get(0));
    }

    @ApiOperation("获取简历列表(HR用)")
    @GetMapping("/list")
    @RequireLogin
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer education) {
        StringBuilder sql = new StringBuilder(
            "SELECT r.*, u.username FROM rec_resume r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "WHERE r.deleted = 0 AND r.is_public = 1 ");
        
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (r.real_name LIKE ? OR r.expect_job LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (education != null) {
            sql.append("AND r.education = ? ");
            params.add(education);
        }
        sql.append("ORDER BY r.update_time DESC");
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @ApiOperation("获取简历详情")
    @GetMapping("/{id}")
    @RequireLogin
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        String sql = "SELECT r.*, u.username FROM rec_resume r " +
                "LEFT JOIN sys_user u ON r.user_id = u.id WHERE r.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return Result.success(list.isEmpty() ? null : list.get(0));
    }

    @ApiOperation("保存简历")
    @PostMapping("/save")
    @RequireLogin
    public Result<Long> save(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Long id = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;
        
        if (id != null) {
            // 更新
            String sql = "UPDATE rec_resume SET resume_name=?, real_name=?, gender=?, birthday=?, " +
                    "phone=?, email=?, education=?, school=?, major=?, graduate_year=?, work_years=?, " +
                    "expect_job=?, expect_salary=?, expect_place=?, job_status=?, self_evaluation=?, " +
                    "work_experience=?, project_experience=?, education_experience=?, skills=?, " +
                    "attachment_url=?, is_public=?, update_time=NOW() WHERE id=?";
            jdbcTemplate.update(sql, 
                data.get("resumeName"), data.get("realName"), data.get("gender"), data.get("birthday"),
                data.get("phone"), data.get("email"), data.get("education"), data.get("school"),
                data.get("major"), data.get("graduateYear"), data.get("workYears"), data.get("expectJob"),
                data.get("expectSalary"), data.get("expectPlace"), data.get("jobStatus"), data.get("selfEvaluation"),
                data.get("workExperience"), data.get("projectExperience"), data.get("educationExperience"),
                data.get("skills"), data.get("attachmentUrl"), data.get("isPublic"), id);
        } else {
            // 新增
            id = System.currentTimeMillis();
            String sql = "INSERT INTO rec_resume (id, user_id, resume_name, real_name, gender, birthday, " +
                    "phone, email, education, school, major, graduate_year, work_years, expect_job, " +
                    "expect_salary, expect_place, job_status, self_evaluation, work_experience, " +
                    "project_experience, education_experience, skills, attachment_url, is_public, " +
                    "is_default, create_by, create_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,NOW())";
            jdbcTemplate.update(sql, id, userId,
                data.get("resumeName"), data.get("realName"), data.get("gender"), data.get("birthday"),
                data.get("phone"), data.get("email"), data.get("education"), data.get("school"),
                data.get("major"), data.get("graduateYear"), data.get("workYears"), data.get("expectJob"),
                data.get("expectSalary"), data.get("expectPlace"), data.get("jobStatus"), data.get("selfEvaluation"),
                data.get("workExperience"), data.get("projectExperience"), data.get("educationExperience"),
                data.get("skills"), data.get("attachmentUrl"), data.get("isPublic"), userId);
        }
        return Result.success(id);
    }

    @ApiOperation("删除简历")
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "UPDATE rec_resume SET deleted = 1 WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        return Result.success();
    }
}
