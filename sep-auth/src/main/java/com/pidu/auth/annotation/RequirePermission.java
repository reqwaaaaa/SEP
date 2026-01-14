package com.pidu.auth.annotation;

import java.lang.annotation.*;

/**
 * 需要权限注解
 * 标注在Controller类或方法上，表示需要特定权限才能访问
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限标识
     */
    String[] value();

    /**
     * 多个权限之间的逻辑关系
     */
    Logical logical() default Logical.OR;

    enum Logical {
        /**
         * 需要拥有所有权限
         */
        AND,
        /**
         * 拥有任一权限即可
         */
        OR
    }
}
