package com.pidu.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织机构（高校/企业）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_org")
public class SysOrg extends BaseEntity {

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 组织类型 1-高校 2-企业 3-政府部门
     */
    private Integer orgType;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 地址
     */
    private String address;

    /**
     * 简介
     */
    private String introduction;

    /**
     * Logo URL
     */
    private String logo;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
