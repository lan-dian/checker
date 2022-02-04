package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.InspectField;

import java.lang.reflect.Field;

public interface Inspector{

    /**
     * 校验成功返回null，否则返回错误消息
     */
    String inspect(Field field,Object bean,Class<?> group);

}
