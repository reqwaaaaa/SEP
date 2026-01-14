package com.pidu.system.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息处理器
 */
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储用户ID与WebSocket会话的映射
    private static final Map<Long, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            USER_SESSIONS.put(userId, session);
            log.info("用户 {} 连接WebSocket成功, 当前在线人数: {}", userId, USER_SESSIONS.size());
            
            // 发送连接成功消息
            sendMessage(session, createMessage("connected", "连接成功", null));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = getUserId(session);
        if (senderId == null) {
            sendMessage(session, createMessage("error", "未登录", null));
            return;
        }

        try {
            Map<String, Object> data = MAPPER.readValue(message.getPayload(), Map.class);
            String type = (String) data.get("type");
            
            switch (type) {
                case "chat":
                    handleChatMessage(senderId, data);
                    break;
                case "ping":
                    sendMessage(session, createMessage("pong", null, null));
                    break;
                default:
                    log.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理消息失败", e);
            sendMessage(session, createMessage("error", "消息处理失败", null));
        }
    }

    private void handleChatMessage(Long senderId, Map<String, Object> data) {
        Long receiverId = Long.valueOf(data.get("receiverId").toString());
        String content = (String) data.get("content");
        Long conversationId = data.get("conversationId") != null ? 
                Long.valueOf(data.get("conversationId").toString()) : null;
        
        // 构建消息
        Map<String, Object> msgData = new ConcurrentHashMap<>();
        msgData.put("senderId", senderId);
        msgData.put("receiverId", receiverId);
        msgData.put("content", content);
        msgData.put("conversationId", conversationId);
        msgData.put("timestamp", System.currentTimeMillis());
        
        String msgJson = createMessage("chat", null, msgData);
        
        // 发送给接收者
        WebSocketSession receiverSession = USER_SESSIONS.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            sendMessage(receiverSession, msgJson);
            log.info("消息已发送给用户 {}", receiverId);
        } else {
            log.info("用户 {} 不在线，消息已存储", receiverId);
        }
        
        // 也发送给发送者确认
        WebSocketSession senderSession = USER_SESSIONS.get(senderId);
        if (senderSession != null && senderSession.isOpen()) {
            sendMessage(senderSession, createMessage("sent", "消息已发送", msgData));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            USER_SESSIONS.remove(userId);
            log.info("用户 {} 断开WebSocket连接, 当前在线人数: {}", userId, USER_SESSIONS.size());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
        Long userId = getUserId(session);
        if (userId != null) {
            USER_SESSIONS.remove(userId);
        }
        if (session.isOpen()) {
            session.close();
        }
    }

    private Long getUserId(WebSocketSession session) {
        // 从URL参数中获取userId
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return Long.valueOf(param.substring(7));
                }
            }
        }
        return null;
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    private String createMessage(String type, String message, Map<String, Object> data) {
        try {
            Map<String, Object> msg = new ConcurrentHashMap<>();
            msg.put("type", type);
            if (message != null) msg.put("message", message);
            if (data != null) msg.put("data", data);
            return MAPPER.writeValueAsString(msg);
        } catch (Exception e) {
            return "{\"type\":\"error\",\"message\":\"消息序列化失败\"}";
        }
    }

    /**
     * 发送消息给指定用户(供其他服务调用)
     */
    public static void sendToUser(Long userId, String message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        }
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }
}
