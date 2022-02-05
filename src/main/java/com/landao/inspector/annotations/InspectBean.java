package com.landao.inspector.annotations;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以标注在VO类上,也可以不标注,嵌套检测的时候必须标注
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface InspectBean {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean nullable() default false;

}
