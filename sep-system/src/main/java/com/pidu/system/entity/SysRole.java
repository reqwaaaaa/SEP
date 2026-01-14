package com.pidu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 数据范围 1-全部 2-本组织 3-本人
     */
    private Integer dataScope;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
