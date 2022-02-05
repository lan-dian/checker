package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.core.Handler;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;

@Handler
public class LongInspector extends AbstractInspector{

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(Long.class).addChain(long.class);
    }

    @Override
    public FeedBack specialInspect(AnnotatedElement annotatedElement, Object value,String beanName,String fieldName, Class<?> group) {
        Inspected inspected = AnnotationUtils.findAnnotation(annotatedElement, Inspected.class);
        if (inspected == null) {
            return FeedBack.pass();
        }
        String displayName=getDisplayName(beanName,inspected.name());
        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,displayName+"不能为空");
        }

        long fieldValue=(Long) value;

        long min = inspected.min();
        long max = inspected.max();
        if(fieldValue<min || fieldValue>max){
            return FeedBack.illegal(fieldName,displayName+"必须在"+min+"-"+max+"之间");
        }

        return FeedBack.pass();
    }

}
