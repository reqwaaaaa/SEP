package com.pidu.system.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户信息VO
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户类型名称
     */
    private String userTypeName;

    /**
     * 所属组织ID
     */
    private Long orgId;

    /**
     * 所属组织名称
     */
    private String orgName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 角色标识集合
     */
    private Set<String> roles;

    /**
     * 权限标识集合
     */
    private Set<String> permissions;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
