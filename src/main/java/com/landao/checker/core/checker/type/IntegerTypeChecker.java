package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.Check;
import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

@Checker
public class IntegerTypeChecker extends AbstractTypeChecker {

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(Integer.class).addChain(int.class);
    }

    @Override
    public FeedBack specialInspect(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group) {
        Check check = AnnotationUtils.findAnnotation(annotatedElement, Check.class);
        if (check == null) {
            return FeedBack.pass();
        }
        String displayName=getDisplayName(beanName, check.name());
        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,displayName+"不能为空");
        }

        int fieldValue=(Integer) value;

        long min = check.min();
        long max = check.max();
        if(fieldValue<min || fieldValue>max){
            return FeedBack.illegal(fieldName,displayName+"必须在"+min+"-"+max+"之间");
        }

        return FeedBack.pass();
    }

}
