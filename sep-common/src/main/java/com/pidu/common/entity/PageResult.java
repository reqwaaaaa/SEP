package com.pidu.common.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    public PageResult() {
        this.records = Collections.emptyList();
        this.total = 0;
        this.pageNum = 1;
        this.pageSize = 10;
        this.pages = 0;
    }

    public PageResult(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>();
    }

    public static <T> PageResult<T> of(List<T> records, long total, int pageNum, int pageSize) {
        return new PageResult<>(records, total, pageNum, pageSize);
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return pageNum < pages;
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return pageNum > 1;
    }
}
