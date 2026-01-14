package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户课程（报名记录）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_user_course")
public class UserCourse extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 学习进度（百分比）
     */
    private Integer progress;

    /**
     * 已学习课时数
     */
    private Integer learnedLessonCount;

    /**
     * 总学习时长（秒）
     */
    private Integer totalLearnTime;

    /**
     * 最后学习的课时ID
     */
    private Long lastLessonId;

    /**
     * 最后学习时间
     */
    private LocalDateTime lastLearnTime;

    /**
     * 是否完成 0-未完成 1-已完成
     */
    private Integer isCompleted;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 证书URL
     */
    private String certificateUrl;
}
