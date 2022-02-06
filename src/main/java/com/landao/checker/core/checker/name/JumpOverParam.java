package com.landao.checker.core.checker.name;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JumpOverParam {

    @AliasFor("onlyFor")
    String[] value() default {};

    @AliasFor("value")
    String[] onlyFor() default {};

}
