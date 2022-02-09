package com.landao.checker.core.checker.type;


import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.special.Before;
import com.landao.checker.annotations.special.After;
import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Checker
public class DateTimeTypeChecker extends AbstractDateTypeChecker {

    @Override
    public FeedBack specialTypeCheck(String displayName, AnnotatedElement annotatedElement, Object value, String fieldName, Class<?> group) {
        LocalDateTime fieldValue=(LocalDateTime) value;

        After after = AnnotationUtils.findAnnotation(annotatedElement, After.class);
        if(after !=null){
            boolean onlyCheckDate = after.onlyCheckDate();
            boolean containsNow = after.containsNow();
            if(onlyCheckDate){
                LocalDate now = LocalDate.now();
                LocalDate date = fieldValue.toLocalDate();
                if(date.isBefore(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能为今天之前的时间");
                }else if(containsNow && date.equals(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能包括今天的时间");
                }
            }else {
                LocalDateTime now = LocalDateTime.now();
                if(fieldValue.isBefore(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能为现在之前的时间");
                }else if(containsNow && fieldValue.equals(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能包括现在的时间");
                }
            }
            return FeedBack.pass();
        }
        Before before = AnnotationUtils.findAnnotation(annotatedElement, Before.class);
        if(before !=null){
            boolean onlyCheckDate = before.onlyCheckDate();
            boolean containsNow = before.containsNow();
            if(onlyCheckDate){
                LocalDate now = LocalDate.now();
                LocalDate date = fieldValue.toLocalDate();
                if(date.isAfter(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能为今天之后的时间");
                }else if(containsNow && date.equals(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能包括今天的时间");
                }
            }else {
                LocalDateTime now = LocalDateTime.now();
                if(fieldValue.isAfter(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能为现在之后的时间");
                }else if(containsNow && fieldValue.equals(now)){
                    return FeedBack.illegal(fieldName,displayName+"不能包括现在的时间");
                }
            }
            return FeedBack.pass();
        }

        return FeedBack.pass();
    }

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(LocalDateTime.class);
    }
}
