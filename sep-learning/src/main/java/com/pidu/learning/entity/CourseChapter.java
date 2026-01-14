package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 课程章节
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_course_chapter")
public class CourseChapter extends BaseEntity {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节名称
     */
    private String chapterName;

    /**
     * 章节简介
     */
    private String introduction;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 课时列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CourseLesson> lessons;
}
