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
 * 岗位推荐控制器(辅导员功能)
 */
@Slf4j
@Api(tags = "岗位推荐")
@RestController
@RequestMapping("/job-recommend")
@RequiredArgsConstructor
public class JobRecommendController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取本校学生列表(辅导员用)")
    @GetMapping("/students")
    @RequireLogin
    public Result<List<Map<String, Object>>> getStudents() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        // 只有辅导员(4)可以使用
        if (userType != 4 && userType != 6) {
            return Result.fail("您没有权限使用此功能");
        }
        
        // 获取辅导员所属学校
        String orgSql = "SELECT org_id FROM sys_user WHERE id = ?";
        List<Map<String, Object>> orgList = jdbcTemplate.queryForList(orgSql, userId);
        if (orgList.isEmpty() || orgList.get(0).get("org_id") == null) {
            return Result.success(new ArrayList<>());
        }
        Long orgId = Long.valueOf(orgList.get(0).get("org_id").toString());
        
        // 获取本校学生
        String sql = "SELECT u.id, u.username, u.real_name, u.phone, u.email, " +
                "r.expect_job, r.education, r.major " +
                "FROM sys_user u " +
                "LEFT JOIN rec_resume r ON u.id = r.user_id AND r.deleted = 0 " +
                "WHERE u.org_id = ? AND u.user_type = 2 AND u.status = 1 AND u.deleted = 0";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, orgId);
        return Result.success(list);
    }

    @ApiOperation("推荐岗位给学生")
    @PostMapping("/recommend")
    @RequireLogin
    public Result<Void> recommend(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        if (userType != 4 && userType != 6) {
            return Result.fail("您没有权限使用此功能");
        }
        
        Long studentId = Long.valueOf(data.get("studentId").toString());
        Long jobId = Long.valueOf(data.get("jobId").toString());
        String remark = (String) data.get("remark");
        
        // 获取职位信息
        String jobSql = "SELECT j.job_name, o.org_name FROM rec_job j " +
                "LEFT JOIN sys_org o ON j.enterprise_id = o.id WHERE j.id = ?";
        List<Map<String, Object>> jobList = jdbcTemplate.queryForList(jobSql, jobId);
        if (jobList.isEmpty()) {
            return Result.fail("职位不存在");
        }
        String jobName = (String) jobList.get(0).get("job_name");
        String orgName = (String) jobList.get(0).get("org_name");
        
        // 发送通知给学生
        long notifyId = System.currentTimeMillis();
        String title = "辅导员为您推荐了一个岗位";
        String content = String.format("辅导员推荐您关注【%s】的【%s】岗位。%s", 
                orgName, jobName, remark != null ? "推荐理由：" + remark : "");
        
        String insertSql = "INSERT INTO msg_notification (id, user_id, title, content, notify_type, biz_type, biz_id, create_time) " +
                "VALUES (?, ?, ?, ?, 2, 'JOB_RECOMMEND', ?, NOW())";
        jdbcTemplate.update(insertSql, notifyId, studentId, title, content, jobId);
        
        log.info("辅导员 {} 推荐岗位 {} 给学生 {}", userId, jobId, studentId);
        return Result.success();
    }

    @ApiOperation("批量推荐岗位")
    @PostMapping("/batch-recommend")
    @RequireLogin
    public Result<Integer> batchRecommend(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        if (userType != 4 && userType != 6) {
            return Result.fail("您没有权限使用此功能");
        }
        
        List<Integer> studentIds = (List<Integer>) data.get("studentIds");
        Long jobId = Long.valueOf(data.get("jobId").toString());
        String remark = (String) data.get("remark");
        
        // 获取职位信息
        String jobSql = "SELECT j.job_name, o.org_name FROM rec_job j " +
                "LEFT JOIN sys_org o ON j.enterprise_id = o.id WHERE j.id = ?";
        List<Map<String, Object>> jobList = jdbcTemplate.queryForList(jobSql, jobId);
        if (jobList.isEmpty()) {
            return Result.fail("职位不存在");
        }
        String jobName = (String) jobList.get(0).get("job_name");
        String orgName = (String) jobList.get(0).get("org_name");
        
        String title = "辅导员为您推荐了一个岗位";
        String content = String.format("辅导员推荐您关注【%s】的【%s】岗位。%s", 
                orgName, jobName, remark != null ? "推荐理由：" + remark : "");
        
        String insertSql = "INSERT INTO msg_notification (id, user_id, title, content, notify_type, biz_type, biz_id, create_by, create_time) " +
                "VALUES (?, ?, ?, ?, 2, 'JOB_RECOMMEND', ?, ?, NOW())";
        
        int count = 0;
        for (Integer studentId : studentIds) {
            long notifyId = System.currentTimeMillis() + studentId;
            jdbcTemplate.update(insertSql, notifyId, studentId.longValue(), title, content, jobId, userId);
            count++;
        }
        
        return Result.success(count);
    }

    @ApiOperation("获取我收到的推荐(学生用)")
    @GetMapping("/my-recommendations")
    @RequireLogin
    public Result<List<Map<String, Object>>> getMyRecommendations() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        // 辅导员查看自己推荐的记录
        if (userType == 4 || userType == 6) {
            String sql = "SELECT n.id, n.title, n.content, n.create_time, n.is_read, " +
                    "u.real_name as studentName, u.username as studentUsername, " +
                    "j.job_name as jobTitle, o.org_name as companyName, n.biz_id as jobId " +
                    "FROM msg_notification n " +
                    "LEFT JOIN sys_user u ON n.user_id = u.id " +
                    "LEFT JOIN rec_job j ON n.biz_id = j.id " +
                    "LEFT JOIN sys_org o ON j.enterprise_id = o.id " +
                    "WHERE n.biz_type = 'JOB_RECOMMEND' AND n.create_by = ? " +
                    "ORDER BY n.create_time DESC LIMIT 100";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
            return Result.success(list);
        }
        
        // 学生查看收到的推荐
        String sql = "SELECT n.*, j.job_name, j.salary_min, j.salary_max, j.work_place, o.org_name " +
                "FROM msg_notification n " +
                "LEFT JOIN rec_job j ON n.biz_id = j.id " +
                "LEFT JOIN sys_org o ON j.enterprise_id = o.id " +
                "WHERE n.user_id = ? AND n.biz_type = 'JOB_RECOMMEND' " +
                "ORDER BY n.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }
}
