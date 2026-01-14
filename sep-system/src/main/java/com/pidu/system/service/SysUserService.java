package com.pidu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pidu.common.entity.LoginUser;
import com.pidu.system.dto.LoginDTO;
import com.pidu.system.dto.RegisterDTO;
import com.pidu.system.dto.UserQueryDTO;
import com.pidu.system.dto.ProfileDTO;
import com.pidu.system.entity.SysUser;
import com.pidu.common.entity.PageResult;
import com.pidu.system.vo.LoginVO;
import com.pidu.system.vo.UserVO;
import com.pidu.system.vo.ProfileVO;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 获取当前登录用户信息
     */
    LoginUser getLoginUserInfo(Long userId);

    /**
     * 分页查询用户
     */
    PageResult<UserVO> pageUsers(UserQueryDTO queryDTO);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    SysUser getByPhone(String phone);

    /**
     * 获取个人信息
     */
    ProfileVO getProfile(Long userId);

    /**
     * 更新个人信息
     */
    void updateProfile(Long userId, ProfileDTO profileDTO);

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStats(Long userId);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 启用/禁用用户
     */
    void updateStatus(Long userId, Integer status);
}
