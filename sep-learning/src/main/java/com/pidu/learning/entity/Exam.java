package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 考试信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_exam")
public class Exam extends BaseEntity {

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 关联课程ID
     */
    private Long courseId;

    /**
     * 考试说明
     */
    private String description;

    /**
     * 考试时长（分钟）
     */
    private Integer duration;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 及格分数
     */
    private Integer passScore;

    /**
     * 题目数量
     */
    private Integer questionCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 允许考试次数 0-不限
     */
    private Integer allowTimes;

    /**
     * 是否显示答案 0-不显示 1-交卷后显示
     */
    private Integer showAnswer;

    /**
     * 状态 0-草稿 1-已发布 2-已结束
     */
    private Integer status;
}
