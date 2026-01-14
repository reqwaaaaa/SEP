package com.pidu.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统用户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别 1-男 2-女
     */
    private Integer gender;

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
     * 个人简介
     */
    private String introduction;

    /**
     * 用户类型 1-求职者 2-在校学生 3-企业HR 4-辅导员 5-培训讲师 6-管理员
     */
    private Integer userType;

    /**
     * 所属组织ID（高校ID/企业ID）
     */
    private Long orgId;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录失败次数
     */
    private Integer loginFailCount;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属组织名称（非数据库字段）
     */
    @TableField(exist = false)
    private String orgName;
}
