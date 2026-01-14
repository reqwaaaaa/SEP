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
 * 通知公告控制器
 */
@Slf4j
@Api(tags = "通知公告")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取我的通知")
    @GetMapping("/my")
    @RequireLogin
    public Result<List<Map<String, Object>>> getMyNotifications() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT * FROM msg_notification WHERE user_id = ? ORDER BY create_time DESC LIMIT 50";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("获取未读通知数")
    @GetMapping("/unread-count")
    @RequireLogin
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT COUNT(*) FROM msg_notification WHERE user_id = ? AND is_read = 0";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return Result.success(count != null ? count : 0);
    }

    @ApiOperation("标记通知为已读")
    @PostMapping("/read/{id}")
    @RequireLogin
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "UPDATE msg_notification SET is_read = 1, read_time = NOW() WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        return Result.success();
    }

    @ApiOperation("标记全部已读")
    @PostMapping("/read-all")
    @RequireLogin
    public Result<Void> markAllAsRead() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "UPDATE msg_notification SET is_read = 1, read_time = NOW() WHERE user_id = ? AND is_read = 0";
        jdbcTemplate.update(sql, userId);
        return Result.success();
    }

    @ApiOperation("发布通知(管理员/讲师)")
    @PostMapping("/publish")
    @RequireLogin
    public Result<Void> publish(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        // 权限检查
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限发布通知");
        }
        
        String title = (String) data.get("title");
        String content = (String) data.get("content");
        Integer notifyType = data.get("notifyType") != null ? Integer.valueOf(data.get("notifyType").toString()) : 1;
        List<Integer> targetUserTypes = (List<Integer>) data.get("targetUserTypes"); // 目标用户类型列表
        
        // 获取目标用户
        StringBuilder userSql = new StringBuilder("SELECT id FROM sys_user WHERE status = 1 AND deleted = 0");
        if (targetUserTypes != null && !targetUserTypes.isEmpty()) {
            userSql.append(" AND user_type IN (");
            for (int i = 0; i < targetUserTypes.size(); i++) {
                userSql.append(i > 0 ? "," : "").append(targetUserTypes.get(i));
            }
            userSql.append(")");
        }
        
        List<Map<String, Object>> users = jdbcTemplate.queryForList(userSql.toString());
        
        // 批量插入通知
        String insertSql = "INSERT INTO msg_notification (id, user_id, title, content, notify_type, create_time) VALUES (?, ?, ?, ?, ?, NOW())";
        for (Map<String, Object> user : users) {
            Long targetUserId = Long.valueOf(user.get("id").toString());
            long notifyId = System.currentTimeMillis() + targetUserId;
            jdbcTemplate.update(insertSql, notifyId, targetUserId, title, content, notifyType);
        }
        
        log.info("发布通知成功，标题: {}, 目标用户数: {}", title, users.size());
        return Result.success();
    }

    @ApiOperation("获取公告列表(首页用)")
    @GetMapping("/announcements")
    public Result<List<Map<String, Object>>> getAnnouncements() {
        // 获取系统公告(notify_type=1)
        String sql = "SELECT DISTINCT title, content, notify_type, create_time FROM msg_notification " +
                "WHERE notify_type = 1 ORDER BY create_time DESC LIMIT 10";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return Result.success(list);
    }
}
