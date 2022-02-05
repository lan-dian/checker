package com.landao.inspector.utils;

import com.landao.inspector.InspectorProperties;
import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.model.collection.IllegalsHolder;
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
        getIllegalsHolder().add(fieldName,illegalReason);
    }

    public static IllegalsHolder getIllegalsHolder(){
        return illegalsHolder.get();
    }

    public static void clear(){
        illegalsHolder.remove();
    }

    public static boolean illegal(){
        return getIllegalsHolder().illegal();
    }

    public static boolean isFastFail(){
        InspectMode inspectMode = inspectorProperties.getInspectMode();
        return inspectMode==InspectMode.FastFailed;
    }

}
