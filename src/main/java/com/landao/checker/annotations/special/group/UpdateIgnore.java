package com.landao.checker.annotations.special.group;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 修改的时候把这个设置为null
 * @apiNote 不能让用户修改这个字段,比如密码不能在修改用户信息中修改
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CheckGroup(UpdateGroup.class)
public @interface UpdateIgnore {


}
