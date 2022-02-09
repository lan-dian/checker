package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.special.group.AddIgnore;
import com.landao.checker.annotations.special.group.Id;
import com.landao.checker.annotations.special.group.UpdateIgnore;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;
import com.landao.checker.model.exception.CheckerException;
import com.landao.checker.utils.CheckUtils;
import com.landao.checker.utils.CheckerManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;


/**
 * 做一些必要的初始化工作
 */
public abstract class AbstractTypeChecker implements TypeChecker {

    @Override
    public TypeSet supportClasses() {
        return supportedChain(new TypeSet());
    }

    public abstract TypeSet supportedChain(TypeSet set);

    @Override
    public FeedBack check(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group) {
        Check check = AnnotationUtils.findAnnotation(annotatedElement, Check.class);
        if(check!=null && check.ignore()){
            return FeedBack.pass();
        }



        if (group != null) {
            if (isAddGroup(group)) {//就处理这两种,其他放行
                if (requireSetNull(annotatedElement)) {
                    return FeedBack.pass(null);
                }
            } else if (isUpdateGroup(group)) {
                UpdateIgnore updateIgnore = AnnotationUtils.findAnnotation(annotatedElement, UpdateIgnore.class);
                if (updateIgnore != null) {
                    return FeedBack.pass(null);
                }
                Id id = AnnotationUtils.findAnnotation(annotatedElement, Id.class);
                if (id != null) {
                    String idName=getIdName(fieldName);
                    if(value==null){
                        return FeedBack.illegal(fieldName,"修改"+beanName+"时必须指明"+idName);
                    }
                    Class<?> valueType = value.getClass();
                    if (CheckerManager.isLong(valueType)) {
                        if ( (Long) value <= 0) {
                            return FeedBack.illegal(fieldName,beanName+idName+"不合法");
                        }
                    } else if (CheckerManager.isInteger(valueType)) {
                        if ((Integer)value <= 0) {
                            return FeedBack.illegal(fieldName,beanName+idName+"不合法");
                        }
                    } else if (CheckerManager.isString(valueType)) {
                        if (!StringUtils.hasText((String) value)) {
                            return FeedBack.illegal(fieldName,beanName+idName+"不合法");
                        }
                    }else {
                        throw new CheckerException("不推荐以"+valueType.getName()+"作为id");
                    }
                }
            }
        }

        Nullable nullable = AnnotationUtils.findAnnotation(annotatedElement, Nullable.class);
        if(nullable!=null){
            if(value==null){
                return FeedBack.pass();
            }
            if(CheckerManager.isString(value.getClass())){
                if(!StringUtils.hasText((String) value)){
                    return FeedBack.pass(null);
                }
            }
        }

        return commonTypeCheck(annotatedElement, value, beanName, fieldName, group);
    }

    public abstract FeedBack commonTypeCheck(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group);

    protected String getDisplayName(String beanName,String fieldName){
        if(StringUtils.hasText(beanName)){
            return beanName+"的"+fieldName;
        }else {
            return fieldName;
        }
    }

    private String getIdName(String fieldName){
        return fieldName.split("\\.")[1];
    }

    //简化工具类,直接内部调用
    protected final boolean isAddGroup(Class<?> group) {
        return CheckUtils.isAddGroup(group);
    }

    protected final boolean isUpdateGroup(Class<?> group) {
        return CheckUtils.isUpdateGroup(group);
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



}
