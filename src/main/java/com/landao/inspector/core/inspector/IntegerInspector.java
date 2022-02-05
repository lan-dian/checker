package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.special.group.AddGroup;
import com.landao.inspector.annotations.special.group.Id;
import com.landao.inspector.annotations.special.group.UpdateGroup;
import com.landao.inspector.core.AbstractInspector;
import com.landao.inspector.core.Inspector;
import com.landao.inspector.model.collection.TypeSet;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class IntegerInspector{

    // @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(Integer.class).addChain(int.class);
    }

    // @Override
    public void specialInspect(Field field, Object bean, Object value,Class<?> group) {

    }

/*    @Override
    public void specialInspect(Field field, Object bean) {
        Inspected inspected = AnnotationUtils.findAnnotation(field, Inspected.class);
        if (inspected == null) {
            return false;
        }
        //检查是否可以为null
        Nullable nullable = AnnotationUtils.findAnnotation(field, Nullable.class);
        ReflectionUtils.makeAccessible(field);
        Integer fieldValue = (Integer) ReflectionUtils.getField(field, bean);
        if (nullable != null && fieldValue == null) {
            return false;
        }
        //不可为null
        if (fieldValue==null) {
            InspectorManager.addIllegal(inspected,bean,"不能为空");
            return false;
        }

        long min = inspected.min();
        long max = inspected.max();
        if(fieldValue<min || fieldValue>max){
            InspectorManager.addIllegal(inspected,bean,"必须在"+min+"-"+max+"之间");
            return false;
        }

        return false;
    }*/

}
