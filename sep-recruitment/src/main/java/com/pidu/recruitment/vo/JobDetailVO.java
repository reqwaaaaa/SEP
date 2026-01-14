package com.pidu.recruitment.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 职位详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JobDetailVO extends JobVO {

    /**
     * 招聘人数
     */
    private Integer recruitNum;

    /**
     * 职位描述
     */
    private String description;

    /**
     * 任职要求
     */
    private String requirement;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 企业简介
     */
    private String enterpriseIntro;

    /**
     * 企业地址
     */
    private String enterpriseAddress;

    /**
     * 当前用户是否已投递
     */
    private Boolean hasApplied;
}
