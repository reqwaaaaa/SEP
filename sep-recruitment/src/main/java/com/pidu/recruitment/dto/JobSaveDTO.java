package com.pidu.recruitment.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 职位保存DTO
 */
@Data
public class JobSaveDTO {

    /**
     * 职位名称
     */
    @NotBlank(message = "职位名称不能为空")
    @Size(max = 50, message = "职位名称不能超过50个字符")
    private String jobName;

    /**
     * 职位类型 1-全职 2-兼职 3-实习
     */
    @NotNull(message = "职位类型不能为空")
    private Integer jobType;

    /**
     * 工作地点
     */
    @NotBlank(message = "工作地点不能为空")
    private String workPlace;

    /**
     * 最低薪资（元/月）
     */
    @NotNull(message = "最低薪资不能为空")
    @DecimalMin(value = "0", message = "薪资不能为负数")
    private BigDecimal salaryMin;

    /**
     * 最高薪资（元/月）
     */
    @NotNull(message = "最高薪资不能为空")
    private BigDecimal salaryMax;

    /**
     * 学历要求
     */
    @NotNull(message = "学历要求不能为空")
    private Integer education;

    /**
     * 工作经验要求
     */
    @NotNull(message = "工作经验要求不能为空")
    private Integer experience;

    /**
     * 招聘人数
     */
    @NotNull(message = "招聘人数不能为空")
    @Min(value = 1, message = "招聘人数至少为1")
    private Integer recruitNum;

    /**
     * 职位描述
     */
    @NotBlank(message = "职位描述不能为空")
    private String description;

    /**
     * 任职要求
     */
    @NotBlank(message = "任职要求不能为空")
    private String requirement;

    /**
     * 福利待遇
     */
    private List<String> benefits;

    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String contactEmail;
}
