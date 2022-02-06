package com.landao.checker.core;

import com.landao.checker.utils.CheckUtils;
import com.landao.checker.utils.CheckerManager;
import org.springframework.util.StringUtils;

/**
 * 你可以自定义认证aop顺序
 */
public interface Checked {

    /**
     * 重写这个接口实现代码级别的自定义注解
     * @param group 分组
     * @param supperName 超类链的名称
     * @apiNote 使用getFieldName组合
     */
    void check(Class<?> group, String supperName);

    //不去实现下面的方法,这些是为了方便你直接使用的
    default boolean isAddGroup(Class<?> group){
        return CheckUtils.isAddGroup(group);
    }

    default boolean isUpdateGroup(Class<?> group){
        return CheckUtils.isUpdateGroup(group);
    }

    default void addIllegal(String fieldName,String illegalReason){
        CheckerManager.addIllegal(fieldName,illegalReason);
    }

    default String getFieldName(String supperName,String fieldName){
        if(StringUtils.hasText(supperName)){
            return supperName+fieldName;
        }else {
            return fieldName;
        }
    }

}
