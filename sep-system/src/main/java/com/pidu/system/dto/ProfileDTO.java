package com.pidu.system.dto;

import lombok.Data;

/**
 * 个人信息更新DTO
 */
@Data
public class ProfileDTO {
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 性别 1-男 2-女
     */
    private Integer gender;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 个人简介
     */
    private String introduction;
    
    /**
     * 头像URL
     */
    private String avatar;
}
