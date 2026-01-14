package com.pidu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pidu.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询用户菜单列表
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.status = 1 AND m.deleted = 0 " +
            "AND m.menu_type IN (1, 2) " +
            "ORDER BY m.sort")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
