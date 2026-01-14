package com.pidu.system.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.pidu.auth.annotation.RequireLogin;
import com.pidu.common.result.Result;
import com.pidu.common.util.SecurityContextUtil;
import com.pidu.system.dto.LoginDTO;
import com.pidu.system.dto.RegisterDTO;
import com.pidu.system.entity.SysUser;
import com.pidu.system.service.SysUserService;
import com.pidu.common.entity.LoginUser;
import com.pidu.system.vo.LoginVO;
import com.pidu.system.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = userService.login(loginDTO);
        return Result.success(loginVO);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success();
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    @RequireLogin
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            userService.logout(token);
        }
        return Result.success();
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    @RequireLogin
    public Result<UserVO> getCurrentUserInfo() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        LoginUser loginUser = userService.getLoginUserInfo(userId);
        
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

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    @ApiOperation("初始化管理员密码(临时)")
    @GetMapping("/init-admin")
    public Result<String> initAdmin() {
        SysUser user = userService.getByUsername("admin");
        if (user != null) {
            String newHash = BCrypt.hashpw("admin123");
            userService.resetPassword(user.getId(), "admin123");
            return Result.success("Admin password reset to admin123, hash: " + newHash);
        }
        return Result.fail("Admin user not found");
    }
}
