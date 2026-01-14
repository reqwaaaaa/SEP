package com.pidu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pidu.auth.service.TokenService;
import com.pidu.common.entity.LoginUser;
import com.pidu.common.entity.PageResult;
import com.pidu.common.enums.UserType;
import com.pidu.common.exception.BusinessException;
import com.pidu.common.result.ResultCode;
import com.pidu.common.util.JwtUtil;
import com.pidu.common.util.RedisUtil;
import com.pidu.system.dto.LoginDTO;
import com.pidu.system.dto.RegisterDTO;
import com.pidu.system.dto.UserQueryDTO;
import com.pidu.system.dto.ProfileDTO;
import com.pidu.system.entity.SysOrg;
import com.pidu.system.entity.SysUser;
import com.pidu.system.mapper.SysOrgMapper;
import com.pidu.system.mapper.SysUserMapper;
import com.pidu.system.service.SysUserService;
import com.pidu.system.vo.LoginVO;
import com.pidu.system.vo.UserVO;
import com.pidu.system.vo.ProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysOrgMapper orgMapper;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final HttpServletRequest request;

    private static final String LOGIN_FAIL_KEY = "login:fail:";
    private static final int MAX_LOGIN_FAIL_COUNT = 5;
    private static final int LOCK_MINUTES = 30;

    @Value("${jwt.expiration:86400000}")
    private long tokenExpiration;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        
        // 检查登录失败次数
        checkLoginFailCount(username);

        // 查询用户
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            user = userMapper.selectByPhone(username);
        }
        if (user == null) {
            recordLoginFail(username);
            throw new BusinessException(ResultCode.ACCOUNT_NOT_EXIST);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }
        if (user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED);
        }

        // 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            recordLoginFail(username);
            int remainCount = MAX_LOGIN_FAIL_COUNT - getLoginFailCount(username);
            throw new BusinessException(ResultCode.PASSWORD_ERROR.getCode(), 
                    "密码错误，还剩" + remainCount + "次机会");
        }

        // 清除登录失败记录
        clearLoginFail(username);

        // 更新登录信息
        user.setLastLoginIp(getClientIp());
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginFailCount(0);
        user.setLockTime(null);
        userMapper.updateById(user);

        // 构建登录用户信息
        LoginUser loginUser = getLoginUserInfo(user.getId());
        loginUser.setLoginIp(getClientIp());

        // 生成Token
        String accessToken = tokenService.createToken(loginUser);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(tokenExpiration / 1000);
        loginVO.setUserInfo(convertToUserVO(user, loginUser.getRoles(), loginUser.getPermissions()));

        return loginVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (userMapper.selectByPhone(registerDTO.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCrypt.hashpw(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setUserType(registerDTO.getUserType());
        user.setOrgId(registerDTO.getOrgId());
        user.setStatus(1);
        user.setLoginFailCount(0);

        userMapper.insert(user);
    }

    @Override
    public void logout(String token) {
        tokenService.deleteToken(token);
    }

    @Override
    public LoginUser getLoginUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setRealName(user.getRealName());
        loginUser.setUserType(user.getUserType());
        loginUser.setOrgId(user.getOrgId());

        // 查询组织名称
        if (user.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(user.getOrgId());
            if (org != null) {
                loginUser.setOrgName(org.getOrgName());
            }
        }

        // 查询角色和权限
        Set<String> roles = userMapper.selectRoleKeysByUserId(userId);
        Set<String> permissions = userMapper.selectPermissionsByUserId(userId);
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);

        return loginUser;
    }

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO queryDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryDTO.getUsername()), SysUser::getUsername, queryDTO.getUsername())
                .like(StringUtils.hasText(queryDTO.getRealName()), SysUser::getRealName, queryDTO.getRealName())
                .like(StringUtils.hasText(queryDTO.getPhone()), SysUser::getPhone, queryDTO.getPhone())
                .eq(queryDTO.getUserType() != null, SysUser::getUserType, queryDTO.getUserType())
                .eq(queryDTO.getOrgId() != null, SysUser::getOrgId, queryDTO.getOrgId())
                .eq(queryDTO.getStatus() != null, SysUser::getStatus, queryDTO.getStatus())
                .orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<SysUser> result = userMapper.selectPage(page, wrapper);

        List<UserVO> voList = result.getRecords().stream()
                .map(user -> convertToUserVO(user, null, null))
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public SysUser getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);

        // 强制下线
        tokenService.forceLogout(userId);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        user.setPassword(BCrypt.hashpw(newPassword));
        user.setLoginFailCount(0);
        user.setLockTime(null);
        userMapper.updateById(user);

        // 强制下线
        tokenService.forceLogout(userId);
    }

    @Override
    public void updateStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        user.setStatus(status);
        userMapper.updateById(user);

        if (status == 0) {
            // 禁用时强制下线
            tokenService.forceLogout(userId);
        }
    }

    @Override
    public ProfileVO getProfile(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        ProfileVO profile = new ProfileVO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setRealName(user.getRealName());
        profile.setUserType(user.getUserType());
        profile.setUserTypeName(UserType.fromCode(user.getUserType()).getName());
        profile.setGender(user.getGender());
        profile.setPhone(user.getPhone());
        profile.setEmail(user.getEmail());
        profile.setAvatar(user.getAvatar());
        profile.setIntroduction(user.getIntroduction());
        profile.setOrgId(user.getOrgId());
        profile.setCreateTime(user.getCreateTime());
        profile.setLastLoginTime(user.getLastLoginTime());
        profile.setLastLoginIp(user.getLastLoginIp());

        // 查询组织名称
        if (user.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(user.getOrgId());
            if (org != null) {
                profile.setOrgName(org.getOrgName());
            }
        }

        // 获取统计数据
        Map<String, Object> stats = getUserStats(userId);
        profile.setCourseCount((Integer) stats.getOrDefault("courseCount", 0));
        profile.setExamCount((Integer) stats.getOrDefault("examCount", 0));
        profile.setApplicationCount((Integer) stats.getOrDefault("applicationCount", 0));

        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long userId, ProfileDTO profileDTO) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST);
        }

        // 检查手机号是否被其他用户使用
        if (StringUtils.hasText(profileDTO.getPhone())) {
            SysUser existUser = userMapper.selectByPhone(profileDTO.getPhone());
            if (existUser != null && !existUser.getId().equals(userId)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            user.setPhone(profileDTO.getPhone());
        }

        if (StringUtils.hasText(profileDTO.getRealName())) {
            user.setRealName(profileDTO.getRealName());
        }
        if (profileDTO.getGender() != null) {
            user.setGender(profileDTO.getGender());
        }
        if (StringUtils.hasText(profileDTO.getEmail())) {
            user.setEmail(profileDTO.getEmail());
        }
        if (StringUtils.hasText(profileDTO.getIntroduction())) {
            user.setIntroduction(profileDTO.getIntroduction());
        }
        if (StringUtils.hasText(profileDTO.getAvatar())) {
            user.setAvatar(profileDTO.getAvatar());
        }

        userMapper.updateById(user);
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 这里简化处理，实际应该查询相关表
        // 学习课程数
        Integer courseCount = userMapper.countUserCourses(userId);
        stats.put("courseCount", courseCount != null ? courseCount : 0);
        
        // 参加考试数
        Integer examCount = userMapper.countUserExams(userId);
        stats.put("examCount", examCount != null ? examCount : 0);
        
        // 投递简历数
        Integer applicationCount = userMapper.countUserApplications(userId);
        stats.put("applicationCount", applicationCount != null ? applicationCount : 0);
        
        return stats;
    }

    private UserVO convertToUserVO(SysUser user, Set<String> roles, Set<String> permissions) {
        UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
        vo.setUserTypeName(UserType.fromCode(user.getUserType()).getName());
        vo.setRoles(roles);
        vo.setPermissions(permissions);

        if (user.getOrgId() != null) {
            SysOrg org = orgMapper.selectById(user.getOrgId());
            if (org != null) {
                vo.setOrgName(org.getOrgName());
            }
        }

        return vo;
    }

    private void checkLoginFailCount(String username) {
        int failCount = getLoginFailCount(username);
        if (failCount >= MAX_LOGIN_FAIL_COUNT) {
            throw new BusinessException(ResultCode.ACCOUNT_LOCKED.getCode(),
                    "登录失败次数过多，账号已锁定" + LOCK_MINUTES + "分钟");
        }
    }

    private int getLoginFailCount(String username) {
        Object count = redisUtil.get(LOGIN_FAIL_KEY + username);
        return count == null ? 0 : (int) count;
    }

    private void recordLoginFail(String username) {
        String key = LOGIN_FAIL_KEY + username;
        int count = getLoginFailCount(username) + 1;
        redisUtil.set(key, count, LOCK_MINUTES, TimeUnit.MINUTES);
    }

    private void clearLoginFail(String username) {
        redisUtil.delete(LOGIN_FAIL_KEY + username);
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
