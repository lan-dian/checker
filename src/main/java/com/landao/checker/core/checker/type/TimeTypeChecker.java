package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.special.Before;
import com.landao.checker.annotations.special.After;
import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalTime;

@Checker
public class TimeTypeChecker extends AbstractDateTypeChecker {

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(LocalTime.class);
    }


    @Override
    public FeedBack specialTypeCheck(String displayName, AnnotatedElement annotatedElement, Object value, String fieldName, Class<?> group) {
        LocalTime fieldValue=(LocalTime) value;

        LocalTime now = LocalTime.now();
        After after = AnnotationUtils.findAnnotation(annotatedElement, After.class);
        if(after !=null){
            boolean containsNow = after.containsNow();
            if(fieldValue.isBefore(now)){
               return FeedBack.illegal(fieldName,displayName+"不能在现在之前");
            }else if(containsNow && fieldValue.equals(now)){
                return FeedBack.illegal(fieldName,displayName+"不能包括现在");
            }
            return FeedBack.pass();
        }
        Before before = AnnotationUtils.findAnnotation(annotatedElement, Before.class);
        if(before !=null){
            boolean containsNow = before.containsNow();
            if(fieldValue.isAfter(now)){
                return FeedBack.illegal(fieldName,displayName+"不能在现在之后");
            }else if(containsNow && fieldValue.equals(now)){
                return FeedBack.illegal(fieldName,displayName+"不能包括现在");
            }
            return FeedBack.pass();
        }

        return FeedBack.pass();
    }

}
