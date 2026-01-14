package com.pidu.common.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户类型 1-求职者 2-在校学生 3-企业HR 4-辅导员 5-培训讲师 6-管理员
     */
    private Integer userType;

    /**
     * 所属组织ID（高校ID/企业ID）
     */
    private Long orgId;

    /**
     * 所属组织名称
     */
    private String orgName;

    /**
     * 权限标识集合
     */
    private Set<String> permissions;

    /**
     * 角色标识集合
     */
    private Set<String> roles;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * Token过期时间
     */
    private Long expireTime;
}
