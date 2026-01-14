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
 * 消息控制器
 */
@Slf4j
@Api(tags = "消息管理")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取会话列表")
    @GetMapping("/conversations")
    @RequireLogin
    public Result<List<Map<String, Object>>> getConversations() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT c.*, " +
                "CASE WHEN c.user_a_id = ? THEN c.user_b_id ELSE c.user_a_id END as target_id, " +
                "CASE WHEN c.user_a_id = ? THEN c.user_a_unread ELSE c.user_b_unread END as unread_count, " +
                "u.real_name as target_name, u.avatar as target_avatar, " +
                "m.content as last_content " +
                "FROM msg_conversation c " +
                "LEFT JOIN sys_user u ON (CASE WHEN c.user_a_id = ? THEN c.user_b_id ELSE c.user_a_id END) = u.id " +
                "LEFT JOIN msg_private_message m ON c.last_message_id = m.id " +
                "WHERE c.user_a_id = ? OR c.user_b_id = ? " +
                "ORDER BY c.last_message_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, userId, userId, userId, userId);
        return Result.success(list);
    }

    @ApiOperation("获取消息列表")
    @GetMapping("/list/{conversationId}")
    @RequireLogin
    public Result<List<Map<String, Object>>> getMessages(@PathVariable Long conversationId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT m.*, u.real_name as sender_name, u.avatar as sender_avatar, " +
                "(m.sender_id = ?) as is_self " +
                "FROM msg_private_message m " +
                "LEFT JOIN sys_user u ON m.sender_id = u.id " +
                "WHERE m.conversation_id = ? AND m.deleted = 0 " +
                "ORDER BY m.create_time ASC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId, conversationId);
        
        // 标记消息为已读
        String updateSql = "UPDATE msg_private_message SET is_read = 1, read_time = NOW() " +
                "WHERE conversation_id = ? AND receiver_id = ? AND is_read = 0";
        jdbcTemplate.update(updateSql, conversationId, userId);
        
        // 更新会话未读数
        String updateConvSql = "UPDATE msg_conversation SET " +
                "user_a_unread = CASE WHEN user_a_id = ? THEN 0 ELSE user_a_unread END, " +
                "user_b_unread = CASE WHEN user_b_id = ? THEN 0 ELSE user_b_unread END " +
                "WHERE id = ?";
        jdbcTemplate.update(updateConvSql, userId, userId, conversationId);
        
        return Result.success(list);
    }

    @ApiOperation("发送消息")
    @PostMapping("/send")
    @RequireLogin
    public Result<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> data) {
        Long senderId = SecurityContextUtil.getCurrentUserId();
        Long receiverId = Long.valueOf(data.get("receiverId").toString());
        String content = (String) data.get("content");
        Integer messageType = data.get("messageType") != null ? 
                Integer.valueOf(data.get("messageType").toString()) : 1;
        String fileUrl = (String) data.get("fileUrl");
        
        // 查找或创建会话
        Long conversationId = findOrCreateConversation(senderId, receiverId);
        
        // 插入消息
        long messageId = System.currentTimeMillis();
        String insertSql = "INSERT INTO msg_private_message (id, conversation_id, sender_id, receiver_id, content, message_type, file_url, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(insertSql, messageId, conversationId, senderId, receiverId, content, messageType, fileUrl);
        
        // 更新会话
        String updateSql = "UPDATE msg_conversation SET last_message_id = ?, last_message_time = NOW(), " +
                "user_a_unread = CASE WHEN user_a_id = ? THEN user_a_unread ELSE user_a_unread + 1 END, " +
                "user_b_unread = CASE WHEN user_b_id = ? THEN user_b_unread ELSE user_b_unread + 1 END " +
                "WHERE id = ?";
        jdbcTemplate.update(updateSql, messageId, senderId, senderId, conversationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("conversationId", conversationId);
        return Result.success(result);
    }


    private Long findOrCreateConversation(Long userA, Long userB) {
        // 确保userA < userB以保持唯一性
        Long smallId = Math.min(userA, userB);
        Long largeId = Math.max(userA, userB);
        
        String querySql = "SELECT id FROM msg_conversation WHERE user_a_id = ? AND user_b_id = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql, smallId, largeId);
        
        if (!list.isEmpty()) {
            return Long.valueOf(list.get(0).get("id").toString());
        }
        
        // 创建新会话
        long convId = System.currentTimeMillis();
        String insertSql = "INSERT INTO msg_conversation (id, user_a_id, user_b_id, create_time) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(insertSql, convId, smallId, largeId);
        return convId;
    }

    @ApiOperation("获取用户列表(用于发起私信)")
    @GetMapping("/users")
    @RequireLogin
    public Result<List<Map<String, Object>>> getUsers() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT id, username, real_name, avatar FROM sys_user WHERE id != ? AND status = 1 AND deleted = 0";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("获取未读消息数")
    @GetMapping("/unread-count")
    @RequireLogin
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT COALESCE(SUM(CASE WHEN user_a_id = ? THEN user_a_unread ELSE user_b_unread END), 0) as total " +
                "FROM msg_conversation WHERE user_a_id = ? OR user_b_id = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, userId, userId, userId);
        return Result.success(((Number) result.get("total")).intValue());
    }
}
