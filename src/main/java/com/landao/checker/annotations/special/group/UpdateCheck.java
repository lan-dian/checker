package com.landao.checker.annotations.special.group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@CheckGroup(value = UpdateGroup.class)
public @interface UpdateCheck {



}
