package com.landao.checker.annotations.special;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface NotBefore {

    boolean containsNow() default false;

    /**
     * 对于datetime仅仅检查日期部分
     */
    boolean onlyCheckDate() default true;

}
