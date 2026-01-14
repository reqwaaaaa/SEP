package com.pidu.common.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserType {
    
    JOB_SEEKER(1, "求职者", "社会个人用户"),
    STUDENT(2, "在校学生", "高校用户"),
    ENTERPRISE_HR(3, "企业HR", "企业用户"),
    COUNSELOR(4, "辅导员", "高校用户"),
    TRAINER(5, "培训讲师", "高校用户"),
    ADMIN(6, "管理员", "郫都区人社局工作人员");

    private final int code;
    private final String name;
    private final String description;

    UserType(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static UserType fromCode(int code) {
        for (UserType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的用户类型: " + code);
    }
}
