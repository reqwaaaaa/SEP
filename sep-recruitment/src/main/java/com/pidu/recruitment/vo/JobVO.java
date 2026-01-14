package com.pidu.recruitment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 职位列表VO
 */
@Data
public class JobVO {

    /**
     * 职位ID
     */
    private Long id;

    /**
     * 职位名称
     */
    private String jobName;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 企业Logo
     */
    private String enterpriseLogo;

    /**
     * 职位类型 1-全职 2-兼职 3-实习
     */
    private Integer jobType;

    /**
     * 职位类型名称
     */
    private String jobTypeName;

    /**
     * 工作地点
     */
    private String workPlace;

    /**
     * 最低薪资
     */
    private BigDecimal salaryMin;

    /**
     * 最高薪资
     */
    private BigDecimal salaryMax;

    /**
     * 薪资描述
     */
    private String salaryDesc;

    /**
     * 学历要求
     */
    private Integer education;

    /**
     * 学历要求名称
     */
    private String educationName;

    /**
     * 工作经验要求
     */
    private Integer experience;

    /**
     * 工作经验名称
     */
    private String experienceName;

    /**
     * 福利待遇
     */
    private List<String> benefits;

    /**
     * 状态
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

    /**
     * 发布时间
     */
    private LocalDateTime createTime;
}
