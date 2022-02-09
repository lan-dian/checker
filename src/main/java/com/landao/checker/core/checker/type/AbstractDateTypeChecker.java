package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.special.Before;
import com.landao.checker.annotations.special.After;
import com.landao.checker.model.FeedBack;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

public abstract class AbstractDateTypeChecker extends AbstractTypeChecker{

    @Override
    public FeedBack commonTypeCheck(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group) {
        After after = AnnotationUtils.findAnnotation(annotatedElement, After.class);
        String displayName=fieldName;
        if (after != null) {
            displayName=getDisplayName(beanName, after.name());
        }
        Before before = AnnotationUtils.findAnnotation(annotatedElement, Before.class);
        if (before != null) {
            displayName=getDisplayName(beanName, before.name());
        }

        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,displayName+"不能为空");
        }

        return specialTypeCheck(displayName,annotatedElement, value, fieldName, group);
    }

    public abstract FeedBack specialTypeCheck(String displayName,AnnotatedElement annotatedElement, Object value, String fieldName, Class<?> group);

}
