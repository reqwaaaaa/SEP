package com.pidu.learning.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试题目
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("learn_exam_question")
public class ExamQuestion extends BaseEntity {

    /**
     * 考试ID
     */
    private Long examId;

    /**
     * 题目类型 1-单选 2-多选 3-判断 4-填空 5-简答
     */
    private Integer questionType;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 选项（JSON数组，选择题使用）
     */
    private String options;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 答案解析
     */
    private String analysis;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 排序
     */
    private Integer sort;
}
