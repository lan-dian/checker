package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.InspectField;
import com.landao.inspector.annotations.special.group.AddGroup;
import com.landao.inspector.annotations.special.group.Id;
import com.landao.inspector.annotations.special.group.UpdateGroup;
import com.landao.inspector.model.exception.InspectIllegalException;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class LongInspector implements Inspector{

    @Override
    public String inspect(Field field, Object bean, Class<?> group) {
        if(group!=null){
            if(group.equals(AddGroup.class)){
                Id id = AnnotationUtils.findAnnotation(field, Id.class);
                if(id!=null){
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field,bean,null);
                }
            }else if(group.equals(UpdateGroup.class)){
                Id id = AnnotationUtils.findAnnotation(field, Id.class);
                if(id!=null){
                    ReflectionUtils.makeAccessible(field);
                    Long idValue = (Long)ReflectionUtils.getField(field, bean);
                    if(!InspectUtils.checkId(idValue)){
                        InspectorManager.addIllegal("id","修改时必选传递id");
                    }
                }
            }
        }
        InspectField inspectField = AnnotationUtils.findAnnotation(field, InspectField.class);
        if (inspectField == null) {
            return null;
        }
        //检查是否可以为null
        Nullable nullable = AnnotationUtils.findAnnotation(field, Nullable.class);
        ReflectionUtils.makeAccessible(field);
        Integer fieldValue = (Integer) ReflectionUtils.getField(field, bean);
        if (nullable != null && fieldValue == null) {
            return null;//通过
        }
        //不可为null
        if (fieldValue==null) {
            return InspectorManager.getFieldName(inspectField, bean) + "不能为空";
        }

        long min = inspectField.min();
        long max = inspectField.max();
        if(fieldValue<min || fieldValue>max){
            return InspectorManager.getFieldName(inspectField,bean)+"必须在"+min+"-"+max+"之间";
        }

        return null;
    }

}
