package com.pidu.auth.annotation;

import java.lang.annotation.*;

/**
 * 需要角色注解
 * 标注在Controller类或方法上，表示需要特定角色才能访问
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 需要的角色标识
     */
    String[] value();

    /**
     * 多个角色之间的逻辑关系
     */
    Logical logical() default Logical.OR;

    enum Logical {
        /**
         * 需要拥有所有角色
         */
        AND,
        /**
         * 拥有任一角色即可
         */
        OR
    }
}
