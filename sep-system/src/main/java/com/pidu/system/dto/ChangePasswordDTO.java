package com.pidu.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改密码DTO
 */
@Data
public class ChangePasswordDTO {
    
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
