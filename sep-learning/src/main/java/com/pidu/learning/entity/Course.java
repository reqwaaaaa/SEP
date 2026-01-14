package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 课程信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_course")
public class Course extends BaseEntity {

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程封面
     */
    private String coverUrl;

    /**
     * 课程简介
     */
    private String introduction;

    /**
     * 课程详情
     */
    private String description;

    /**
     * 课程分类ID
     */
    private Long categoryId;

    /**
     * 讲师ID
     */
    private Long teacherId;

    /**
     * 所属组织ID（高校ID）
     */
    private Long orgId;

    /**
     * 课程类型 1-公共课程 2-本校课程
     */
    private Integer courseType;

    /**
     * 课程难度 1-入门 2-初级 3-中级 4-高级
     */
    private Integer difficulty;

    /**
     * 课程时长（分钟）
     */
    private Integer duration;

    /**
     * 章节数
     */
    private Integer chapterCount;

    /**
     * 课时数
     */
    private Integer lessonCount;

    /**
     * 学习人数
     */
    private Integer studentCount;

    /**
     * 评分（1-5分）
     */
    private BigDecimal rating;

    /**
     * 评价人数
     */
    private Integer ratingCount;

    /**
     * 是否免费 0-收费 1-免费
     */
    private Integer isFree;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 状态 0-草稿 1-待审核 2-已发布 3-已下架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
