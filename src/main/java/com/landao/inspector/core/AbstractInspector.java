package com.landao.inspector.core;

import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.special.group.AddIgnore;
import com.landao.inspector.annotations.special.group.Id;
import com.landao.inspector.annotations.special.group.UpdateIgnore;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;
import com.landao.inspector.model.exception.InspectorException;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * 做一些必要的初始化工作
 */
public abstract class AbstractInspector implements Inspector {

    @Override
    public TypeSet supportedClasses() {
        return supportedChain(new TypeSet());
    }

    public abstract TypeSet supportedChain(TypeSet set);

    @Override
    public FeedBack inspect(AnnotatedElement field, Object value, String className, String fieldName, Class<?> group) {
        if (group != null) {
            if (isAddGroup(group)) {//就处理这两种,其他放行
                if (requireSetNull(field)) {
                    return FeedBack.pass(null);
                }
            } else if (isUpdateGroup(group)) {
                UpdateIgnore updateIgnore = AnnotationUtils.findAnnotation(field, UpdateIgnore.class);
                if (updateIgnore != null) {
                    return FeedBack.pass(null);
                }
                Id id = AnnotationUtils.findAnnotation(field, Id.class);
                if (id != null) {
                    String idName=getIdName(fieldName);
                    if(value==null){
                        return FeedBack.illegal(fieldName,"修改"+className+"时必须指明"+idName);
                    }
                    Class<?> valueType = value.getClass();
                    if (isLong(valueType)) {
                        if ( (Long) value <= 0) {
                            return FeedBack.illegal(fieldName,className+idName+"不合法");
                        }
                    } else if (isInteger(valueType)) {
                        if ((Integer)value <= 0) {
                            return FeedBack.illegal(fieldName,className+idName+"不合法");
                        }
                    } else if (isString(valueType)) {
                        if (!StringUtils.hasText((String) value)) {
                            return FeedBack.illegal(fieldName,className+idName+"不合法");
                        }
                    }else {
                        throw new InspectorException("不推荐以"+valueType.getName()+"作为id");
                    }
                }
            }
        }
        Nullable nullable = AnnotationUtils.findAnnotation(field, Nullable.class);

        if (nullable != null && value == null) {
            //没有标注的我不能报错,因为用户可能想自己检查这些未标注的字段
            return FeedBack.pass();
        }
        //能走到这里的,有两种可能,没有标注nullable，字段是否为null不清楚或者字段不为null,所以下面需要注意非空判断
        return specialInspect(field, value, className, fieldName, group);
    }

    public abstract FeedBack specialInspect(AnnotatedElement field, Object value, String className, String fieldName, Class<?> group);


    private String getIdName(String fieldName){
        return fieldName.split("\\.")[1];
    }

    //简化工具类,直接内部调用
    protected final boolean isAddGroup(Class<?> group) {
        return InspectUtils.isAddGroup(group);
    }

    protected final boolean isUpdateGroup(Class<?> group) {
        return InspectUtils.isUpdateGroup(group);
    }

    private String getFieldName(Object bean, Field field){
        return bean.getClass().getSimpleName() +"."+field.getName();
    }

    protected void addIllegal(String name , String illegalReason) {
        InspectorManager.addIllegal(name, illegalReason);
    }

    private boolean requireSetNull(AnnotatedElement field) {
        Id id = AnnotationUtils.findAnnotation(field, Id.class);
        if (id != null) {
            return true;
        }
        AddIgnore addIgnore = AnnotationUtils.findAnnotation(field, AddIgnore.class);
        if (addIgnore != null) {
            return true;
        }
        return false;
    }

    private boolean isInteger(Class<?> fieldType) {
        return Integer.class.equals(fieldType) || int.class.equals(fieldType);
    }

    private boolean isLong(Class<?> fieldType) {
        return Long.class.equals(fieldType) || long.class.equals(fieldType);
    }

    private boolean isString(Class<?> fieldType) {
        return String.class.equals(fieldType);
    }

}
