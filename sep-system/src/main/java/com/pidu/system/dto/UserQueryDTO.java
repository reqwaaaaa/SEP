package com.pidu.system.dto;

import com.pidu.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQuery {

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
     * 用户类型
     */
    private Integer userType;

    /**
     * 所属组织ID
     */
    private Long orgId;

    /**
     * 状态
     */
    private Integer status;
}
