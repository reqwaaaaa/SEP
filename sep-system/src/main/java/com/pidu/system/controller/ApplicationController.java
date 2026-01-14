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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 业务申报控制器
 */
@Slf4j
@Api(tags = "业务申报")
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取申报列表")
    @GetMapping("/list")
    @RequireLogin
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, u.real_name as applicant_name, o.org_name " +
            "FROM app_subsidy_application a " +
            "LEFT JOIN sys_user u ON a.user_id = u.id " +
            "LEFT JOIN sys_org o ON a.enterprise_id = o.id " +
            "WHERE a.deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (type != null) {
            sql.append("AND a.application_type = ? ");
            params.add(type);
        }
        if (status != null) {
            sql.append("AND a.status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY a.create_time DESC");
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @ApiOperation("获取我的申报")
    @GetMapping("/my")
    @RequireLogin
    public Result<List<Map<String, Object>>> myApplications() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT * FROM app_subsidy_application WHERE user_id = ? AND deleted = 0 ORDER BY create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("提交申报")
    @PostMapping("/submit")
    @RequireLogin
    public Result<Void> submit(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        long id = System.currentTimeMillis();
        String applicationNo = "SB" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        String sql = "INSERT INTO app_subsidy_application (id, application_no, application_type, enterprise_id, user_id, title, amount, description, status, create_by, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, ?, NOW())";
        
        jdbcTemplate.update(sql, id, applicationNo, 
            data.get("applicationType"), 
            data.get("enterpriseId"),
            userId,
            data.get("title"),
            data.get("amount"),
            data.get("description"),
            userId);
        
        return Result.success();
    }

    @ApiOperation("审核申报")
    @PostMapping("/audit/{id}")
    @RequireLogin
    public Result<Void> audit(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        Long auditorId = SecurityContextUtil.getCurrentUserId();
        Integer result = (Integer) data.get("result"); // 1-通过 2-不通过 3-候补
        String remark = (String) data.get("remark");
        
        // 更新申报状态
        int newStatus = result == 1 ? 2 : (result == 2 ? 3 : 4); // 2-通过 3-不通过 4-候补
        String updateSql = "UPDATE app_subsidy_application SET status = ?, final_remark = ?, audit_time = NOW(), update_time = NOW() WHERE id = ?";
        jdbcTemplate.update(updateSql, newStatus, remark, id);
        
        // 插入审核记录
        String insertSql = "INSERT INTO app_audit_record (id, application_id, audit_node, auditor_id, audit_result, audit_remark, create_time) " +
                          "VALUES (?, ?, 1, ?, ?, ?, NOW())";
        jdbcTemplate.update(insertSql, System.currentTimeMillis(), id, auditorId, result, remark);
        
        return Result.success();
    }

    @ApiOperation("获取审核记录")
    @GetMapping("/audit-records/{applicationId}")
    @RequireLogin
    public Result<List<Map<String, Object>>> getAuditRecords(@PathVariable Long applicationId) {
        String sql = "SELECT r.*, u.real_name as auditor_name FROM app_audit_record r " +
                    "LEFT JOIN sys_user u ON r.auditor_id = u.id " +
                    "WHERE r.application_id = ? AND r.deleted = 0 ORDER BY r.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, applicationId);
        return Result.success(list);
    }
}
