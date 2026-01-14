package com.pidu.recruitment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pidu.recruitment.entity.Resume;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 简历Mapper
 */
@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {

    /**
     * 获取用户默认简历
     */
    @Select("SELECT * FROM rec_resume WHERE user_id = #{userId} AND is_default = 1 AND deleted = 0 LIMIT 1")
    Resume selectDefaultByUserId(@Param("userId") Long userId);
}
