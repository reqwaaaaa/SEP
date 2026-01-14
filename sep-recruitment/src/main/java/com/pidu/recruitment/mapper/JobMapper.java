package com.pidu.recruitment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pidu.recruitment.entity.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 职位Mapper
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 增加浏览次数
     */
    @Update("UPDATE rec_job SET view_count = view_count + 1 WHERE id = #{jobId}")
    int incrementViewCount(@Param("jobId") Long jobId);

    /**
     * 增加投递次数
     */
    @Update("UPDATE rec_job SET apply_count = apply_count + 1 WHERE id = #{jobId}")
    int incrementApplyCount(@Param("jobId") Long jobId);
}
