package com.pidu.auth.interceptor;

import com.pidu.auth.annotation.RequireLogin;
import com.pidu.auth.annotation.RequirePermission;
import com.pidu.auth.annotation.RequireRole;
import com.pidu.auth.service.TokenService;
import com.pidu.common.entity.LoginUser;
import com.pidu.common.exception.BusinessException;
import com.pidu.common.result.ResultCode;
import com.pidu.common.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Set;

/**
 * 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 非Controller方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 获取注解
        RequireLogin requireLogin = getAnnotation(handlerMethod, RequireLogin.class);
        RequireRole requireRole = getAnnotation(handlerMethod, RequireRole.class);
        RequirePermission requirePermission = getAnnotation(handlerMethod, RequirePermission.class);

        // 无需认证的接口直接放行
        if (requireLogin == null && requireRole == null && requirePermission == null) {
            // 尝试解析token，但不强制要求
            tryParseToken(request);
            return true;
        }

        // 需要认证，解析token
        LoginUser loginUser = parseAndValidateToken(request);
        SecurityContextUtil.setCurrentUser(loginUser);

        // 校验角色
        if (requireRole != null) {
            checkRole(loginUser, requireRole);
        }

        // 校验权限
        if (requirePermission != null) {
            checkPermission(loginUser, requirePermission);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 清理ThreadLocal，防止内存泄漏
        SecurityContextUtil.clear();
    }

    /**
     * 尝试解析token（不强制）
     */
    private void tryParseToken(HttpServletRequest request) {
        String token = extractToken(request);
        if (StringUtils.hasText(token)) {
            try {
                LoginUser loginUser = tokenService.getLoginUser(token);
                if (loginUser != null) {
                    SecurityContextUtil.setCurrentUser(loginUser);
                }
            } catch (Exception ignored) {
                // 忽略解析失败
            }
        }
    }

    /**
     * 解析并验证token
     */
    private LoginUser parseAndValidateToken(HttpServletRequest request) {
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        LoginUser loginUser = tokenService.getLoginUser(token);
        if (loginUser == null) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 刷新token有效期
        tokenService.refreshToken(token);

        return loginUser;
    }

    /**
     * 提取token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 校验角色
     */
    private void checkRole(LoginUser loginUser, RequireRole requireRole) {
        String[] requiredRoles = requireRole.value();
        Set<String> userRoles = loginUser.getRoles();

        if (userRoles == null || userRoles.isEmpty()) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }

        boolean hasRole;
        if (requireRole.logical() == RequireRole.Logical.AND) {
            // 需要拥有所有角色
            hasRole = Arrays.stream(requiredRoles).allMatch(userRoles::contains);
        } else {
            // 拥有任一角色即可
            hasRole = Arrays.stream(requiredRoles).anyMatch(userRoles::contains);
        }

        if (!hasRole) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }
    }

    /**
     * 校验权限
     */
    private void checkPermission(LoginUser loginUser, RequirePermission requirePermission) {
        String[] requiredPermissions = requirePermission.value();
        Set<String> userPermissions = loginUser.getPermissions();

        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }

        // 管理员拥有所有权限
        if (userPermissions.contains("*:*:*")) {
            return;
        }

        boolean hasPermission;
        if (requirePermission.logical() == RequirePermission.Logical.AND) {
            hasPermission = Arrays.stream(requiredPermissions).allMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new BusinessException(ResultCode.ACCESS_DENIED);
        }
    }

    /**
     * 获取注解（方法优先，其次类）
     */
    private <T extends java.lang.annotation.Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationClass) {
        T annotation = handlerMethod.getMethodAnnotation(annotationClass);
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(annotationClass);
        }
        return annotation;
    }
}
