package com.landao.checker.utils;

import com.landao.checker.CheckerProperties;
import com.landao.checker.model.collection.IllegalsHolder;
import com.landao.checker.model.enums.CheckMode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 系统内部工具类
 */
public class CheckerManager {

    public static CheckerProperties checkerProperties;

    @Autowired
    public void setInspectorProperties(CheckerProperties checkerProperties){
        CheckerManager.checkerProperties = checkerProperties;
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
        CheckMode checkMode = checkerProperties.getInspectMode();
        return checkMode == CheckMode.FastFailed;
    }

    public static boolean isInteger(Class<?> fieldType) {
        return Integer.class.equals(fieldType) || int.class.equals(fieldType);
    }

    public static boolean isLong(Class<?> fieldType) {
        return Long.class.equals(fieldType) || long.class.equals(fieldType);
    }

    public static boolean isString(Class<?> fieldType) {
        return String.class.equals(fieldType);
    }

}
