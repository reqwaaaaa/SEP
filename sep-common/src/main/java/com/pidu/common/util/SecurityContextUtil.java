package com.pidu.common.util;

import com.pidu.common.entity.LoginUser;

/**
 * 安全上下文工具类
 * 用于存储和获取当前登录用户信息
 */
public class SecurityContextUtil {

    private static final ThreadLocal<LoginUser> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     */
    public static void setCurrentUser(LoginUser user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getCurrentUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        LoginUser user = USER_HOLDER.get();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        LoginUser user = USER_HOLDER.get();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户类型
     */
    public static Integer getCurrentUserType() {
        LoginUser user = USER_HOLDER.get();
        return user != null ? user.getUserType() : null;
    }

    /**
     * 获取当前用户所属组织ID
     */
    public static Long getCurrentOrgId() {
        LoginUser user = USER_HOLDER.get();
        return user != null ? user.getOrgId() : null;
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        LoginUser user = USER_HOLDER.get();
        return user != null && user.getUserType() != null && user.getUserType() == 6;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}
