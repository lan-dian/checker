package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.Check;
import com.landao.checker.model.FeedBack;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

public abstract class AbstractNotNullTypeChecker extends AbstractTypeChecker{

    @Override
    public FeedBack commonTypeCheck(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group) {
        Check check = AnnotationUtils.findAnnotation(annotatedElement, Check.class);
        if (check == null) {
            return FeedBack.pass();
        }
        String displayName=getDisplayName(beanName, check.name());
        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,displayName+"不能为空");
        }

        return specialTypeCheck(check,displayName,annotatedElement, value, fieldName, group);
    }

    public abstract FeedBack specialTypeCheck(Check check,String displayName,AnnotatedElement annotatedElement, Object value, String fieldName, Class<?> group);

}
