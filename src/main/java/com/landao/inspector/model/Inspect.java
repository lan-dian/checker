package com.landao.inspector.model;

import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.special.TelePhone;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;

/**
 * 你可以自定义认证aop顺序
 */
public interface Inspect {

    /**
     * 重写这个接口实现代码级别的自定义注解
     * @param group 当前检查的分组会传递给你
     */
    void inspect(Class<?> group);

    //不去实现下面的方法,这些是为了方便你直接使用的
    default boolean isAddGroup(Class<?> group){
        return InspectUtils.isAddGroup(group);
    }

    default boolean isUpdateGroup(Class<?> group){
        return InspectUtils.isUpdateGroup(group);
    }

    default void addIllegal(String fieldName,String illegalReason){
        InspectorManager.addIllegal(this.getClass().getSimpleName()+"."+fieldName,illegalReason);
    }

}
