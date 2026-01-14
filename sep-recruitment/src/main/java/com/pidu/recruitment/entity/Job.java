package com.pidu.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 职位信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_job")
public class Job extends BaseEntity {

    /**
     * 职位名称
     */
    private String jobName;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 职位类型 1-全职 2-兼职 3-实习
     */
    private Integer jobType;

    /**
     * 工作地点
     */
    private String workPlace;

    /**
     * 最低薪资（元/月）
     */
    private BigDecimal salaryMin;

    /**
     * 最高薪资（元/月）
     */
    private BigDecimal salaryMax;

    /**
     * 学历要求 1-不限 2-大专 3-本科 4-硕士 5-博士
     */
    private Integer education;

    /**
     * 工作经验要求 0-不限 1-1年以下 2-1-3年 3-3-5年 4-5-10年 5-10年以上
     */
    private Integer experience;

    /**
     * 招聘人数
     */
    private Integer recruitNum;

    /**
     * 职位描述
     */
    private String description;

    /**
     * 任职要求
     */
    private String requirement;

    /**
     * 福利待遇（JSON数组）
     */
    private String benefits;

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
     * 状态 0-下架 1-上架 2-已满
     */
    private Integer status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 投递次数
     */
    private Integer applyCount;
}
