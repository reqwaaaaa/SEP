package com.pidu.auth.service;

import com.pidu.common.entity.LoginUser;
import com.pidu.common.util.JwtUtil;
import com.pidu.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_TOKEN_PREFIX = "auth:user_token:";

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    /**
     * 创建Token
     */
    public String createToken(LoginUser loginUser) {
        // 生成JWT
        String token = jwtUtil.generateToken(
                loginUser.getUserId(),
                loginUser.getUsername(),
                loginUser.getUserType()
        );

        // 设置登录时间和过期时间
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + tokenExpiration);

        // 存储到Redis
        String tokenKey = TOKEN_PREFIX + token;
        redisUtil.set(tokenKey, loginUser, tokenExpiration, TimeUnit.MILLISECONDS);

        // 记录用户当前token（用于单点登录控制）
        String userTokenKey = USER_TOKEN_PREFIX + loginUser.getUserId();
        String oldToken = (String) redisUtil.get(userTokenKey);
        if (oldToken != null) {
            // 删除旧token
            redisUtil.delete(TOKEN_PREFIX + oldToken);
        }
        redisUtil.set(userTokenKey, token, tokenExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 获取登录用户信息
     */
    public LoginUser getLoginUser(String token) {
        if (token == null) {
            return null;
        }
        String tokenKey = TOKEN_PREFIX + token;
        Object obj = redisUtil.get(tokenKey);
        if (obj instanceof LoginUser) {
            return (LoginUser) obj;
        }
        return null;
    }

    /**
     * 刷新Token有效期
     */
    public void refreshToken(String token) {
        LoginUser loginUser = getLoginUser(token);
        if (loginUser != null) {
            long currentTime = System.currentTimeMillis();
            long expireTime = loginUser.getExpireTime();
            // 如果剩余时间不足一半，则刷新
            if (expireTime - currentTime < tokenExpiration / 2) {
                loginUser.setExpireTime(currentTime + tokenExpiration);
                String tokenKey = TOKEN_PREFIX + token;
                redisUtil.set(tokenKey, loginUser, tokenExpiration, TimeUnit.MILLISECONDS);
                
                String userTokenKey = USER_TOKEN_PREFIX + loginUser.getUserId();
                redisUtil.expire(userTokenKey, tokenExpiration, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * 删除Token（登出）
     */
    public void deleteToken(String token) {
        LoginUser loginUser = getLoginUser(token);
        if (loginUser != null) {
            redisUtil.delete(TOKEN_PREFIX + token);
            redisUtil.delete(USER_TOKEN_PREFIX + loginUser.getUserId());
        }
    }

    /**
     * 强制用户下线
     */
    public void forceLogout(Long userId) {
        String userTokenKey = USER_TOKEN_PREFIX + userId;
        String token = (String) redisUtil.get(userTokenKey);
        if (token != null) {
            redisUtil.delete(TOKEN_PREFIX + token);
            redisUtil.delete(userTokenKey);
        }
    }

    /**
     * 更新登录用户信息
     */
    public void updateLoginUser(String token, LoginUser loginUser) {
        String tokenKey = TOKEN_PREFIX + token;
        long remainingTime = redisUtil.getExpire(tokenKey, TimeUnit.MILLISECONDS);
        if (remainingTime > 0) {
            redisUtil.set(tokenKey, loginUser, remainingTime, TimeUnit.MILLISECONDS);
        }
    }
}
