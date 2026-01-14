package com.pidu.application.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 申报保存DTO
 */
@Data
public class ApplicationSaveDTO {

    /**
     * 申报类型 1-高技能人才培训补贴 2-获奖项目启动资金补贴 3-创业项目启动资金补贴 4-技能大师工作室
     */
    @NotNull(message = "申报类型不能为空")
    @Min(value = 1, message = "申报类型不正确")
    @Max(value = 4, message = "申报类型不正确")
    private Integer applicationType;

    /**
     * 申报标题
     */
    @NotBlank(message = "申报标题不能为空")
    @Size(max = 100, message = "申报标题不能超过100个字符")
    private String title;

    /**
     * 申报金额
     */
    @NotNull(message = "申报金额不能为空")
    @DecimalMin(value = "0.01", message = "申报金额必须大于0")
    private BigDecimal amount;

    /**
     * 申报说明
     */
    @NotBlank(message = "申报说明不能为空")
    private String description;

    /**
     * 附件材料URL列表
     */
    @NotEmpty(message = "请上传附件材料")
    private List<String> attachments;
}
