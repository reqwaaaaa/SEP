package com.pidu.system.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 个人信息VO
 */
@Data
public class ProfileVO {
    
    private Long id;
    
    private String username;
    
    private String realName;
    
    private Integer userType;
    
    private String userTypeName;
    
    private Integer gender;
    
    private String phone;
    
    private String email;
    
    private String avatar;
    
    private String introduction;
    
    private Long orgId;
    
    private String orgName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime lastLoginTime;
    
    private String lastLoginIp;
    
    // 学习统计
    private Integer courseCount;
    
    private Integer examCount;
    
    private Integer applicationCount;
}
