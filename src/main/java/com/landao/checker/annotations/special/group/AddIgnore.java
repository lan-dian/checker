package com.landao.checker.annotations.special.group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 添加的时候把这个字段设置为空
 * @apiNote 在添加的时候,这些字段想让系统自动决定,用户不可决定
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CheckIgnore(AddGroup.class)
public @interface AddIgnore {

}
