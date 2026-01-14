package com.pidu.common.enums;

import lombok.Getter;

/**
 * 审核状态枚举
 */
@Getter
public enum AuditStatus {
    
    PENDING(0, "待审核"),
    APPROVED(1, "审核通过"),
    REJECTED(2, "审核不通过"),
    WAITING(3, "候补");

    private final int code;
    private final String name;

    AuditStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AuditStatus fromCode(int code) {
        for (AuditStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的审核状态: " + code);
    }
}
