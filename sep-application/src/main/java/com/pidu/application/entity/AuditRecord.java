package com.pidu.application.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审核记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("app_audit_record")
public class AuditRecord extends BaseEntity {

    /**
     * 申报ID
     */
    private Long applicationId;

    /**
     * 审核节点 1-初审 2-复审 3-终审
     */
    private Integer auditNode;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核人姓名
     */
    private String auditorName;

    /**
     * 审核结果 1-通过 2-不通过 3-候补 4-退回修改
     */
    private Integer auditResult;

    /**
     * 审核意见
     */
    private String auditRemark;
}
