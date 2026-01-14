package com.pidu.application.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 申报列表VO
 */
@Data
public class ApplicationVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 申报编号
     */
    private String applicationNo;

    /**
     * 申报类型
     */
    private Integer applicationType;

    /**
     * 申报类型名称
     */
    private String applicationTypeName;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 申报标题
     */
    private String title;

    /**
     * 申报金额
     */
    private BigDecimal amount;

    /**
     * 当前审核节点
     */
    private Integer currentNode;

    /**
     * 当前审核节点名称
     */
    private String currentNodeName;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 审核状态名称
     */
    private String statusName;

    /**
     * 申报时间
     */
    private LocalDateTime createTime;

    /**
     * 审核完成时间
     */
    private LocalDateTime auditTime;
}
