package com.pidu.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    
    // 认证相关 1xxx
    UNAUTHORIZED(1001, "未登录或token已过期"),
    TOKEN_INVALID(1002, "token无效"),
    TOKEN_EXPIRED(1003, "token已过期"),
    ACCESS_DENIED(1004, "权限不足"),
    ACCOUNT_DISABLED(1005, "账号已被禁用"),
    ACCOUNT_LOCKED(1006, "账号已被锁定"),
    PASSWORD_ERROR(1007, "密码错误"),
    ACCOUNT_NOT_EXIST(1008, "账号不存在"),
    
    // 参数校验 2xxx
    PARAM_ERROR(2001, "参数错误"),
    PARAM_MISSING(2002, "缺少必要参数"),
    PARAM_TYPE_ERROR(2003, "参数类型错误"),
    
    // 业务异常 3xxx
    DATA_NOT_EXIST(3001, "数据不存在"),
    DATA_ALREADY_EXIST(3002, "数据已存在"),
    DATA_SAVE_ERROR(3003, "数据保存失败"),
    DATA_UPDATE_ERROR(3004, "数据更新失败"),
    DATA_DELETE_ERROR(3005, "数据删除失败"),
    
    // 文件相关 4xxx
    FILE_UPLOAD_ERROR(4001, "文件上传失败"),
    FILE_NOT_EXIST(4002, "文件不存在"),
    FILE_TYPE_ERROR(4003, "文件类型不支持"),
    FILE_SIZE_EXCEED(4004, "文件大小超出限制"),
    
    // 系统异常 5xxx
    SYSTEM_ERROR(5001, "系统异常"),
    SERVICE_UNAVAILABLE(5002, "服务不可用"),
    RATE_LIMIT_EXCEEDED(5003, "请求过于频繁");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
