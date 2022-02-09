package com.landao.checker.core.checker.type;

import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.special.NotAfter;
import com.landao.checker.annotations.special.NotBefore;
import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.AnnotatedElement;
import java.time.LocalDate;

@Checker
public class DateTypeChecker extends AbstractNotNullTypeChecker{

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(LocalDate.class);
    }


    @Override
    public FeedBack specialTypeCheck(Check check, String displayName, AnnotatedElement annotatedElement, Object value, String fieldName, Class<?> group) {
        LocalDate fieldValue=(LocalDate) value;

        LocalDate now = LocalDate.now();
        NotBefore notBefore = AnnotationUtils.findAnnotation(annotatedElement, NotBefore.class);
        if(notBefore!=null){
            boolean containsNow = notBefore.containsNow();
            if(fieldValue.isBefore(now)){
               return FeedBack.illegal(fieldName,displayName+"不能在今天之前");
            }else if(containsNow && fieldValue.equals(now)){
                return FeedBack.illegal(fieldName,displayName+"不能包括今天");
            }
            return FeedBack.pass();
        }
        NotAfter notAfter = AnnotationUtils.findAnnotation(annotatedElement, NotAfter.class);
        if(notAfter!=null){
            boolean containsNow = notAfter.containsNow();
            if(fieldValue.isAfter(now)){
                return FeedBack.illegal(fieldName,displayName+"不能在今天之后");
            }else if(containsNow && fieldValue.equals(now)){
                return FeedBack.illegal(fieldName,displayName+"不能包括今天");
            }
            return FeedBack.pass();
        }

        return FeedBack.pass();
    }

}
