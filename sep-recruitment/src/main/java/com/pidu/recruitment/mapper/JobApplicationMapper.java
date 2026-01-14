package com.pidu.recruitment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pidu.recruitment.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 职位申请Mapper
 */
@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {

    /**
     * 检查是否已投递
     */
    @Select("SELECT COUNT(*) FROM rec_job_application WHERE job_id = #{jobId} AND user_id = #{userId} AND deleted = 0")
    int countByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);
}
