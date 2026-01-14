package com.pidu.application.dto;

import com.pidu.common.entity.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 申报查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationQueryDTO extends PageQuery {

    /**
     * 申报编号
     */
    private String applicationNo;

    /**
     * 申报类型
     */
    private Integer applicationType;

    /**
     * 申报标题
     */
    private String title;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 当前审核节点
     */
    private Integer currentNode;

    /**
     * 企业ID
     */
    private Long enterpriseId;
}
