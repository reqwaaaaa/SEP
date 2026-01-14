package com.pidu.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pidu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 系统菜单/权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 菜单类型 1-目录 2-菜单 3-按钮
     */
    private Integer menuType;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否可见 0-隐藏 1-显示
     */
    private Integer visible;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子菜单（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysMenu> children;
}
