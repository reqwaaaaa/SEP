package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 课程课时
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_course_lesson")
public class CourseLesson extends BaseEntity {

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 章节ID
     */
    private Long chapterId;

    /**
     * 课时名称
     */
    private String lessonName;

    /**
     * 课时类型 1-视频 2-文档 3-音频
     */
    private Integer lessonType;

    /**
     * 资源URL
     */
    private String resourceUrl;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 是否可试看 0-否 1-是
     */
    private Integer isFree;

    /**
     * 排序
     */
    private Integer sort;
}
