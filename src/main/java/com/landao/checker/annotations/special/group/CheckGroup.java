package com.landao.checker.annotations.special.group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在字段,表示只有这种情况下检测。
 * 标志在controller方法，表示执行该方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
public @interface CheckGroup {

    Class<?> value();

}
