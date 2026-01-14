package com.pidu.application.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 申报详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationDetailVO extends ApplicationVO {

    /**
     * 申报说明
     */
    private String description;

    /**
     * 附件材料
     */
    private List<String> attachments;

    /**
     * 最终审核意见
     */
    private String finalRemark;

    /**
     * 审核记录
     */
    private List<AuditRecordVO> auditRecords;
}
