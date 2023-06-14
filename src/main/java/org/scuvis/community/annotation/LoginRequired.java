package org.scuvis.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被这个注解声明的方法，需要登录后才能访问
 *
 *
 *
 * 有这个需求的方法包括/user/setting、/user/upload
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
