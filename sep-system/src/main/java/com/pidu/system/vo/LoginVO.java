package com.pidu.system.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginVO {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserVO userInfo;
}
