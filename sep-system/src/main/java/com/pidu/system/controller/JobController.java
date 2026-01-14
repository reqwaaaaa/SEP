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
 * 职位控制器
 */
@Slf4j
@Api(tags = "职位管理")
@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取职位列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer jobType,
            @RequestParam(required = false) Integer education,
            @RequestParam(required = false) Integer status) {
        StringBuilder sql = new StringBuilder(
            "SELECT j.*, o.org_name as enterprise_name FROM rec_job j " +
            "LEFT JOIN sys_org o ON j.enterprise_id = o.id " +
            "WHERE j.deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (j.job_name LIKE ? OR j.description LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (jobType != null) {
            sql.append("AND j.job_type = ? ");
            params.add(jobType);
        }
        if (education != null) {
            sql.append("AND j.education = ? ");
            params.add(education);
        }
        if (status != null) {
            sql.append("AND j.status = ? ");
            params.add(status);
        } else {
            sql.append("AND j.status = 1 "); // 默认只显示上架的
        }
        sql.append("ORDER BY j.create_time DESC");
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @ApiOperation("获取企业发布的职位")
    @GetMapping("/my")
    @RequireLogin
    public Result<List<Map<String, Object>>> myJobs() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        // 获取用户所属企业
        String orgSql = "SELECT org_id FROM sys_user WHERE id = ?";
        List<Map<String, Object>> orgList = jdbcTemplate.queryForList(orgSql, userId);
        if (orgList.isEmpty() || orgList.get(0).get("org_id") == null) {
            return Result.success(new ArrayList<>());
        }
        Long orgId = Long.valueOf(orgList.get(0).get("org_id").toString());
        
        String sql = "SELECT j.*, o.org_name as enterprise_name, " +
                "(SELECT COUNT(*) FROM rec_job_application WHERE job_id = j.id AND deleted = 0) as apply_count " +
                "FROM rec_job j " +
                "LEFT JOIN sys_org o ON j.enterprise_id = o.id " +
                "WHERE j.enterprise_id = ? AND j.deleted = 0 ORDER BY j.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, orgId);
        return Result.success(list);
    }

    @ApiOperation("获取职位详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        String sql = "SELECT j.*, o.org_name as enterprise_name FROM rec_job j " +
                "LEFT JOIN sys_org o ON j.enterprise_id = o.id WHERE j.id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, id);
        if (!list.isEmpty()) {
            // 增加浏览次数
            jdbcTemplate.update("UPDATE rec_job SET view_count = view_count + 1 WHERE id = ?", id);
        }
        return Result.success(list.isEmpty() ? null : list.get(0));
    }

    @ApiOperation("发布职位")
    @PostMapping("/save")
    @RequireLogin
    public Result<Long> save(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        // 获取用户所属企业
        String orgSql = "SELECT org_id FROM sys_user WHERE id = ?";
        List<Map<String, Object>> orgList = jdbcTemplate.queryForList(orgSql, userId);
        Long orgId = orgList.isEmpty() || orgList.get(0).get("org_id") == null ? 
                null : Long.valueOf(orgList.get(0).get("org_id").toString());
        
        Long id = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;
        
        if (id != null) {
            String sql = "UPDATE rec_job SET job_name=?, job_type=?, work_place=?, salary_min=?, salary_max=?, " +
                    "education=?, experience=?, recruit_num=?, description=?, requirement=?, benefits=?, " +
                    "contact_person=?, contact_phone=?, contact_email=?, status=?, update_by=?, update_time=NOW() WHERE id=?";
            jdbcTemplate.update(sql,
                data.get("jobName"), data.get("jobType"), data.get("workPlace"), 
                data.get("salaryMin"), data.get("salaryMax"), data.get("education"), data.get("experience"),
                data.get("recruitNum"), data.get("description"), data.get("requirement"), data.get("benefits"),
                data.get("contactPerson"), data.get("contactPhone"), data.get("contactEmail"), 
                data.get("status"), userId, id);
        } else {
            id = System.currentTimeMillis();
            String sql = "INSERT INTO rec_job (id, job_name, enterprise_id, job_type, work_place, salary_min, salary_max, " +
                    "education, experience, recruit_num, description, requirement, benefits, contact_person, " +
                    "contact_phone, contact_email, status, create_by, create_time) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,?,NOW())";
            jdbcTemplate.update(sql, id,
                data.get("jobName"), orgId, data.get("jobType"), data.get("workPlace"),
                data.get("salaryMin"), data.get("salaryMax"), data.get("education"), data.get("experience"),
                data.get("recruitNum"), data.get("description"), data.get("requirement"), data.get("benefits"),
                data.get("contactPerson"), data.get("contactPhone"), data.get("contactEmail"), userId);
        }
        return Result.success(id);
    }

    @ApiOperation("删除职位")
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<Void> delete(@PathVariable Long id) {
        jdbcTemplate.update("UPDATE rec_job SET deleted = 1 WHERE id = ?", id);
        return Result.success();
    }

    @ApiOperation("更新职位状态")
    @PutMapping("/{id}/status")
    @RequireLogin
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        jdbcTemplate.update("UPDATE rec_job SET status = ? WHERE id = ?", status, id);
        return Result.success();
    }
}
