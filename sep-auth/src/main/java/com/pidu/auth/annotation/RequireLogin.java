package com.pidu.auth.annotation;

import java.lang.annotation.*;

/**
 * 需要登录注解
 * 标注在Controller类或方法上，表示需要登录才能访问
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireLogin {
}
