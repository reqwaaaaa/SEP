package com.pidu.system.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度为3-20个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户类型 1-求职者 2-在校学生 3-企业HR
     */
    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型不正确")
    @Max(value = 3, message = "用户类型不正确")
    private Integer userType;

    /**
     * 所属组织ID（学生/企业HR必填）
     */
    private Long orgId;

    /**
     * 短信验证码（可选）
     */
    private String smsCode;
}
