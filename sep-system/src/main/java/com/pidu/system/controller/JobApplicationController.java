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
 * 职位申请控制器
 */
@Slf4j
@Api(tags = "职位申请")
@RestController
@RequestMapping("/job-application")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("投递简历")
    @PostMapping("/apply")
    @RequireLogin
    public Result<Void> apply(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Long jobId = Long.valueOf(data.get("jobId").toString());
        Long resumeId = Long.valueOf(data.get("resumeId").toString());
        
        // 检查是否已投递
        String checkSql = "SELECT COUNT(*) FROM rec_job_application WHERE job_id = ? AND user_id = ? AND deleted = 0";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, jobId, userId);
        if (count != null && count > 0) {
            return Result.fail("您已投递过该职位");
        }
        
        // 获取职位的企业ID
        String jobSql = "SELECT enterprise_id FROM rec_job WHERE id = ?";
        List<Map<String, Object>> jobList = jdbcTemplate.queryForList(jobSql, jobId);
        if (jobList.isEmpty()) {
            return Result.fail("职位不存在");
        }
        Long enterpriseId = Long.valueOf(jobList.get(0).get("enterprise_id").toString());
        
        // 插入申请记录
        long id = System.currentTimeMillis();
        String sql = "INSERT INTO rec_job_application (id, job_id, resume_id, user_id, enterprise_id, status, create_by, create_time) " +
                "VALUES (?, ?, ?, ?, ?, 1, ?, NOW())";
        jdbcTemplate.update(sql, id, jobId, resumeId, userId, enterpriseId, userId);
        
        // 更新职位投递数
        jdbcTemplate.update("UPDATE rec_job SET apply_count = apply_count + 1 WHERE id = ?", jobId);
        
        return Result.success();
    }

    @ApiOperation("获取我的投递记录")
    @GetMapping("/my")
    @RequireLogin
    public Result<List<Map<String, Object>>> myApplications() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT a.*, j.job_name, j.work_place, j.salary_min, j.salary_max, " +
                "o.org_name as enterprise_name " +
                "FROM rec_job_application a " +
                "LEFT JOIN rec_job j ON a.job_id = j.id " +
                "LEFT JOIN sys_org o ON a.enterprise_id = o.id " +
                "WHERE a.user_id = ? AND a.deleted = 0 ORDER BY a.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("获取职位的投递记录(HR用)")
    @GetMapping("/job/{jobId}")
    @RequireLogin
    public Result<List<Map<String, Object>>> getByJob(@PathVariable Long jobId) {
        String sql = "SELECT a.*, r.real_name, r.phone, r.email, r.education, r.school, r.major, " +
                "r.work_years, r.expect_salary, r.attachment_url, j.job_name " +
                "FROM rec_job_application a " +
                "LEFT JOIN rec_resume r ON a.resume_id = r.id " +
                "LEFT JOIN rec_job j ON a.job_id = j.id " +
                "WHERE a.job_id = ? AND a.deleted = 0 ORDER BY a.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, jobId);
        return Result.success(list);
    }

    @ApiOperation("获取企业收到的所有投递")
    @GetMapping("/enterprise")
    @RequireLogin
    public Result<List<Map<String, Object>>> getByEnterprise() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        // 获取用户所属企业
        String orgSql = "SELECT org_id FROM sys_user WHERE id = ?";
        List<Map<String, Object>> orgList = jdbcTemplate.queryForList(orgSql, userId);
        if (orgList.isEmpty() || orgList.get(0).get("org_id") == null) {
            return Result.success(new ArrayList<>());
        }
        Long orgId = Long.valueOf(orgList.get(0).get("org_id").toString());
        
        String sql = "SELECT a.*, r.real_name, r.phone, r.email, r.education, r.school, r.major, " +
                "r.work_years, r.expect_salary, r.attachment_url, r.self_evaluation, j.job_name " +
                "FROM rec_job_application a " +
                "LEFT JOIN rec_resume r ON a.resume_id = r.id " +
                "LEFT JOIN rec_job j ON a.job_id = j.id " +
                "WHERE a.enterprise_id = ? AND a.deleted = 0 ORDER BY a.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, orgId);
        return Result.success(list);
    }

    @ApiOperation("处理投递(HR)")
    @PostMapping("/process/{id}")
    @RequireLogin
    public Result<Void> process(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        Integer status = Integer.valueOf(data.get("status").toString());
        String hrRemark = (String) data.get("hrRemark");
        String interviewTime = (String) data.get("interviewTime");
        String interviewPlace = (String) data.get("interviewPlace");
        String interviewRemark = (String) data.get("interviewRemark");
        
        String sql = "UPDATE rec_job_application SET status=?, hr_remark=?, hr_read=1, " +
                "interview_time=?, interview_place=?, interview_remark=?, update_time=NOW() WHERE id=?";
        jdbcTemplate.update(sql, status, hrRemark, interviewTime, interviewPlace, interviewRemark, id);
        return Result.success();
    }

    @ApiOperation("获取投递详情")
    @GetMapping("/{id}")
    @RequireLogin
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        String sql = "SELECT a.*, r.*, j.job_name, j.work_place, j.salary_min, j.salary_max, " +
                "o.org_name as enterprise_name " +
                "FROM rec_job_application a " +
                "LEFT JOIN rec_resume r ON a.resume_id = r.id " +
                "LEFT JOIN rec_job j ON a.job_id = j.id " +
                "LEFT JOIN sys_org o ON a.enterprise_id = o.id " +
                "WHERE a.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        return Result.success(list.isEmpty() ? null : list.get(0));
    }
}
