package com.pidu.application.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审核记录VO
 */
@Data
public class AuditRecordVO {

    /**
     * 审核节点
     */
    private Integer auditNode;

    /**
     * 审核节点名称
     */
    private String auditNodeName;

    /**
     * 审核人姓名
     */
    private String auditorName;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 审核结果名称
     */
    private String auditResultName;

    /**
     * 审核意见
     */
    private String auditRemark;

    /**
     * 审核时间
     */
    private LocalDateTime createTime;
}
