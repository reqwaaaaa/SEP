package com.pidu.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 简历信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_resume")
public class Resume extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 简历名称
     */
    private String resumeName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别 1-男 2-女
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthday;

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
     * 最高学历 1-高中 2-大专 3-本科 4-硕士 5-博士
     */
    private Integer education;

    /**
     * 毕业院校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 毕业年份
     */
    private Integer graduateYear;

    /**
     * 工作年限
     */
    private Integer workYears;

    /**
     * 期望职位
     */
    private String expectJob;

    /**
     * 期望薪资（元/月）
     */
    private Integer expectSalary;

    /**
     * 期望工作地点
     */
    private String expectPlace;

    /**
     * 求职状态 1-在职看机会 2-离职找工作 3-在校学生
     */
    private Integer jobStatus;

    /**
     * 个人优势/自我评价
     */
    private String selfEvaluation;

    /**
     * 工作经历（JSON）
     */
    private String workExperience;

    /**
     * 项目经历（JSON）
     */
    private String projectExperience;

    /**
     * 教育经历（JSON）
     */
    private String educationExperience;

    /**
     * 技能特长（JSON数组）
     */
    private String skills;

    /**
     * 附件简历URL
     */
    private String attachmentUrl;

    /**
     * 是否公开 0-不公开 1-公开
     */
    private Integer isPublic;

    /**
     * 是否默认简历 0-否 1-是
     */
    private Integer isDefault;
}
