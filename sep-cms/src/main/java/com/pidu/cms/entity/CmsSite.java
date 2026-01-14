package com.pidu.cms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站点信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cms_site")
public class CmsSite extends BaseEntity {

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点类型 1-主站 2-高校子站 3-企业子站
     */
    private Integer siteType;

    /**
     * 关联组织ID
     */
    private Long orgId;

    /**
     * 站点域名
     */
    private String domain;

    /**
     * 站点Logo
     */
    private String logo;

    /**
     * 站点描述
     */
    private String description;

    /**
     * SEO关键词
     */
    private String keywords;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
