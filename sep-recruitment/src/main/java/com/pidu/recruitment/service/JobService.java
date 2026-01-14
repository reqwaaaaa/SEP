package com.pidu.recruitment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pidu.common.entity.PageResult;
import com.pidu.recruitment.dto.JobQueryDTO;
import com.pidu.recruitment.dto.JobSaveDTO;
import com.pidu.recruitment.entity.Job;
import com.pidu.recruitment.vo.JobDetailVO;
import com.pidu.recruitment.vo.JobVO;

/**
 * 职位服务接口
 */
public interface JobService extends IService<Job> {

    /**
     * 分页查询职位（前台）
     */
    PageResult<JobVO> pageJobs(JobQueryDTO queryDTO);

    /**
     * 获取职位详情
     */
    JobDetailVO getJobDetail(Long jobId);

    /**
     * 发布职位
     */
    Long publishJob(JobSaveDTO saveDTO);

    /**
     * 更新职位
     */
    void updateJob(Long jobId, JobSaveDTO saveDTO);

    /**
     * 上架/下架职位
     */
    void updateJobStatus(Long jobId, Integer status);

    /**
     * 删除职位
     */
    void deleteJob(Long jobId);

    /**
     * 分页查询企业发布的职位
     */
    PageResult<JobVO> pageEnterpriseJobs(Long enterpriseId, JobQueryDTO queryDTO);
}
