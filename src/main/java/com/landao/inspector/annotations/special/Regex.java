package com.landao.inspector.annotations.special;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Regex {

    @AliasFor("pattern")
    String value() default "";

    @AliasFor("value")
    String pattern() default "";

}
