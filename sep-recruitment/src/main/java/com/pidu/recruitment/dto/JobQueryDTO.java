package com.pidu.recruitment.dto;

import com.pidu.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 职位查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobQueryDTO extends PageQuery {

    /**
     * 关键词（职位名称/企业名称）
     */
    private String keyword;

    /**
     * 职位类型 1-全职 2-兼职 3-实习
     */
    private Integer jobType;

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
     * 学历要求
     */
    private Integer education;

    /**
     * 工作经验要求
     */
    private Integer experience;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 状态
     */
    private Integer status;
}
