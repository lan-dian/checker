package com.landao.checker.annotations.special;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface After {

    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    boolean containsNow() default false;

    /**
     * 对于datetime仅仅检查日期部分
     */
    boolean onlyCheckDate() default true;

}
