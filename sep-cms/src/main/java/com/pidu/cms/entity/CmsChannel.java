package com.pidu.cms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 栏目/频道
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cms_channel")
public class CmsChannel extends BaseEntity {

    /**
     * 所属站点ID
     */
    private Long siteId;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 栏目名称
     */
    private String channelName;

    /**
     * 栏目编码
     */
    private String channelCode;

    /**
     * 栏目类型 1-列表栏目 2-单页栏目 3-外链栏目
     */
    private Integer channelType;

    /**
     * 外链地址
     */
    private String linkUrl;

    /**
     * 栏目图标
     */
    private String icon;

    /**
     * 栏目描述
     */
    private String description;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 是否在导航显示 0-否 1-是
     */
    private Integer showInNav;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子栏目（非数据库字段）
     */
    @TableField(exist = false)
    private List<CmsChannel> children;
}
