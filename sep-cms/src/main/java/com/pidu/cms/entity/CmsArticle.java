package com.pidu.cms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文章内容
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cms_article")
public class CmsArticle extends BaseEntity {

    /**
     * 所属站点ID
     */
    private Long siteId;

    /**
     * 所属栏目ID
     */
    private Long channelId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer isTop;

    /**
     * 是否推荐 0-否 1-是
     */
    private Integer isRecommend;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 状态 0-草稿 1-待审核 2-已发布 3-已下架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
