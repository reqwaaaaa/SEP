package com.pidu.application.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 补贴申报
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_subsidy_application")
public class SubsidyApplication extends BaseEntity {

    /**
     * 申报编号
     */
    private String applicationNo;

    /**
     * 申报类型 1-高技能人才培训补贴 2-获奖项目启动资金补贴 3-创业项目启动资金补贴 4-技能大师工作室
     */
    private Integer applicationType;

    /**
     * 申报企业ID
     */
    private Long enterpriseId;

    /**
     * 申报人用户ID
     */
    private Long userId;

    /**
     * 申报标题
     */
    private String title;

    /**
     * 申报金额
     */
    private BigDecimal amount;

    /**
     * 申报说明
     */
    private String description;

    /**
     * 附件材料（JSON数组）
     */
    private String attachments;

    /**
     * 当前审核节点
     */
    private Integer currentNode;

    /**
     * 审核状态 0-待审核 1-审核中 2-审核通过 3-审核不通过 4-候补
     */
    private Integer status;

    /**
     * 最终审核意见
     */
    private String finalRemark;

    /**
     * 审核完成时间
     */
    private LocalDateTime auditTime;
}
