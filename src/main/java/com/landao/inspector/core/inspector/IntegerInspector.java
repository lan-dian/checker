package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.core.Handler;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

@Handler
public class IntegerInspector extends AbstractInspector{

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(Integer.class).addChain(int.class);
    }

    @Override
    public FeedBack specialInspect(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group) {
        Inspected inspected = AnnotationUtils.findAnnotation(annotatedElement, Inspected.class);
        if (inspected == null) {
            return FeedBack.pass();
        }
        String disPlayName=beanName+inspected.name();
        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,disPlayName+"不能为空");
        }

        int fieldValue=(Integer) value;

        long min = inspected.min();
        long max = inspected.max();
        if(fieldValue<min || fieldValue>max){
            return FeedBack.illegal(fieldName,disPlayName+"必须在"+min+"-"+max+"之间");
        }

        return FeedBack.pass();
    }

}
