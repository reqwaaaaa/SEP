package com.pidu.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 职位申请/简历投递
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_job_application")
public class JobApplication extends BaseEntity {

    /**
     * 职位ID
     */
    private Long jobId;

    /**
     * 简历ID
     */
    private Long resumeId;

    /**
     * 求职者用户ID
     */
    private Long userId;

    /**
     * 企业ID
     */
    private Long enterpriseId;

    /**
     * 申请状态 1-待查看 2-已查看 3-通过筛选 4-面试邀请 5-已录用 6-不合适
     */
    private Integer status;

    /**
     * HR备注
     */
    private String hrRemark;

    /**
     * 面试时间
     */
    private LocalDateTime interviewTime;

    /**
     * 面试地点
     */
    private String interviewPlace;

    /**
     * 面试备注
     */
    private String interviewRemark;

    /**
     * 求职者是否已读 0-未读 1-已读
     */
    private Integer userRead;

    /**
     * HR是否已读 0-未读 1-已读
     */
    private Integer hrRead;
}
