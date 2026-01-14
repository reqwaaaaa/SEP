package com.pidu.application.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 审核DTO
 */
@Data
public class AuditDTO {

    /**
     * 审核结果 1-通过 2-不通过 3-候补 4-退回修改
     */
    @NotNull(message = "审核结果不能为空")
    @Min(value = 1, message = "审核结果不正确")
    @Max(value = 4, message = "审核结果不正确")
    private Integer auditResult;

    /**
     * 审核意见
     */
    private String auditRemark;
}
