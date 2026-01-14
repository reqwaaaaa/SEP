package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户考试记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_user_exam")
public class UserExam extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 用时（秒）
     */
    private Integer usedTime;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 是否通过 0-未通过 1-通过
     */
    private Integer isPassed;

    /**
     * 答题详情（JSON）
     */
    private String answerDetail;

    /**
     * 状态 1-考试中 2-已交卷 3-已批阅
     */
    private Integer status;
}
