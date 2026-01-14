package com.pidu.system.controller;

import com.pidu.auth.annotation.RequireLogin;
import com.pidu.auth.annotation.RequirePermission;
import com.pidu.common.entity.LoginUser;
import com.pidu.common.entity.PageResult;
import com.pidu.common.result.Result;
import com.pidu.common.util.SecurityContextUtil;
import com.pidu.system.dto.UserQueryDTO;
import com.pidu.system.dto.ProfileDTO;
import com.pidu.system.dto.ChangePasswordDTO;
import com.pidu.system.entity.SysUser;
import com.pidu.system.service.SysUserService;
import com.pidu.system.vo.UserVO;
import com.pidu.system.vo.ProfileVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
@Validated
public class SysUserController {

    private final SysUserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    @RequirePermission("system:user:list")
    public Result<PageResult<UserVO>> pageUsers(UserQueryDTO queryDTO) {
        PageResult<UserVO> result = userService.pageUsers(queryDTO);
        return Result.success(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @RequirePermission("system:user:query")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        LoginUser loginUser = userService.getLoginUserInfo(id);
        if (loginUser == null) {
            return Result.fail("用户不存在");
        }
        
        UserVO userVO = new UserVO();
        userVO.setId(loginUser.getUserId());
        userVO.setUsername(loginUser.getUsername());
        userVO.setRealName(loginUser.getRealName());
        userVO.setUserType(loginUser.getUserType());
        userVO.setOrgId(loginUser.getOrgId());
        userVO.setOrgName(loginUser.getOrgName());
        userVO.setRoles(loginUser.getRoles());
        userVO.setPermissions(loginUser.getPermissions());
        
        return Result.success(userVO);
    }

    @ApiOperation("获取当前用户个人信息")
    @GetMapping("/profile")
    @RequireLogin
    public Result<ProfileVO> getProfile() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        ProfileVO profile = userService.getProfile(userId);
        return Result.success(profile);
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/profile")
    @RequireLogin
    public Result<Void> updateProfile(@RequestBody @Valid ProfileDTO profileDTO) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        userService.updateProfile(userId, profileDTO);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    @RequireLogin
    public Result<Void> updatePassword(
            @RequestParam @NotBlank(message = "原密码不能为空") String oldPassword,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @ApiOperation("修改密码(JSON)")
    @PostMapping("/change-password")
    @RequireLogin
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        userService.updatePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }

    @ApiOperation("获取我的学习数据统计")
    @GetMapping("/my-stats")
    @RequireLogin
    public Result<Map<String, Object>> getMyStats() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Map<String, Object> stats = userService.getUserStats(userId);
        return Result.success(stats);
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/reset-password")
    @RequirePermission("system:user:resetPwd")
    public Result<Void> resetPassword(
            @PathVariable Long id,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @ApiOperation("启用/禁用用户")
    @PutMapping("/{id}/status")
    @RequirePermission("system:user:edit")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam @NotNull(message = "状态不能为空") Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
}
