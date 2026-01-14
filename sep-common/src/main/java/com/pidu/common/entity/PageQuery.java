package com.pidu.common.entity;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {

    /**
     * 当前页码
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式 asc/desc
     */
    private String orderType = "desc";

    /**
     * 获取偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 是否升序
     */
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(orderType);
    }
}
