package com.landao.inspector.utils;

import com.landao.inspector.InspectorProperties;
import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.InspectField;
import com.landao.inspector.model.IllegalsHolder;
import com.landao.inspector.model.enums.InspectMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 系统内部工具类
 */
public class InspectorManager {

    public static InspectorProperties inspectorProperties;

    @Autowired
    public void setInspectorProperties(InspectorProperties inspectorProperties){
        InspectorManager.inspectorProperties=inspectorProperties;
    }

    private static final ThreadLocal<IllegalsHolder> illegalsHolder=ThreadLocal.withInitial(IllegalsHolder::new);

    public static void addIllegal(String fieldName,String illegalReason){
        illegalsHolder.get().add(fieldName,illegalReason);
    }

    public static boolean illegal(){
        return illegalsHolder.get().illegal();
    }


    public static String getFieldName(InspectField inspectField, Object obj){
        String result="";
        InspectBean inspectBean = AnnotationUtils.findAnnotation(obj.getClass(), InspectBean.class);
        if(inspectBean!=null){
            result=inspectBean.name();
        }
        return result+inspectField.name();
    }

    public static boolean isFastFail(){
        InspectMode inspectMode = inspectorProperties.getInspectMode();
        return inspectMode==InspectMode.FastFailed;
    }

}
