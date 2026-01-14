package com.pidu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pidu.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * 用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 查询用户角色标识集合
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    Set<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /**
     * 查询用户权限标识集合
     */
    @Select("SELECT DISTINCT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.deleted = 0 " +
            "AND m.permission IS NOT NULL AND m.permission != ''")
    Set<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 统计用户学习课程数
     */
    @Select("SELECT COUNT(DISTINCT course_id) FROM learn_user_course WHERE user_id = #{userId}")
    Integer countUserCourses(@Param("userId") Long userId);

    /**
     * 统计用户参加考试数
     */
    @Select("SELECT COUNT(*) FROM learn_user_exam WHERE user_id = #{userId}")
    Integer countUserExams(@Param("userId") Long userId);

    /**
     * 统计用户投递简历数
     */
    @Select("SELECT COUNT(*) FROM rec_job_application WHERE user_id = #{userId}")
    Integer countUserApplications(@Param("userId") Long userId);
}
