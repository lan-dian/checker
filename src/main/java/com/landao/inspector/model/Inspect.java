package com.landao.inspector.model;

import com.landao.inspector.annotations.special.TelePhone;

import java.util.Objects;

/**
 * 你可以自定义认证aop顺序
 */
public interface Inspect {

    default void inspect(Class<?> group){

    }

}
