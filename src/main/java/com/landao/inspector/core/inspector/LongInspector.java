package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.special.group.AddGroup;
import com.landao.inspector.annotations.special.group.Id;
import com.landao.inspector.annotations.special.group.UpdateGroup;
import com.landao.inspector.core.AbstractInspector;
import com.landao.inspector.core.Handler;
import com.landao.inspector.core.Inspector;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

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
        String disPlayName=beanName+inspected.name();
        //不可为null
        if (value==null) {
            return FeedBack.illegal(fieldName,disPlayName+"不能为空");
        }

        long fieldValue=(Long) value;

        long min = inspected.min();
        long max = inspected.max();
        if(fieldValue<min || fieldValue>max){
            return FeedBack.illegal(fieldName,disPlayName);
        }

        return FeedBack.pass();
    }

}
