package com.landao.checker.utils;

import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.CheckBean;
import com.landao.checker.annotations.special.Email;
import com.landao.checker.annotations.special.Regex;
import com.landao.checker.annotations.special.TelePhone;
import com.landao.checker.annotations.special.group.AddGroup;
import com.landao.checker.annotations.special.group.UpdateGroup;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 校验参数是否通过,返回的都是boolean值
 */
public abstract class CheckUtils {

    public static final String emailPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    public static final String telephonyPattern = "^(13[0-9]|14[5|7]|15[0|12356789]|18[0|12356789])\\d{8}$";

    /**
     * id不为空并且大于0
     *
     * @param id 主键id
     */
    public static boolean checkId(Long id) {
        return id != null && id > 0;
    }

    /**
     * id不为空并且大于0
     *
     * @param id 主键id
     */
    public static boolean checkId(Integer id) {
        return id != null && id > 0;
    }

    /**
     * 统计数字的位数
     *
     * @param num 数字
     */
    public static int countLength(int num) {
        if (num < 0) {
            num = -num;
        }
        int count = 0;
        while (num > 0) {
            count++;
            num /= 10;
        }
        return count;
    }

    /**
     * 统计数字的位数
     *
     * @param num 数字
     */
    public static int countLength(long num) {
        if (num < 0) {
            num = -num;
        }
        int count = 0;
        while (num > 0) {
            count++;
            num /= 10;
        }
        return count;
    }

    public static boolean isEmail(String str) {
        return Pattern.matches(emailPattern, str);
    }

    public static boolean isTelephone(String str) {
        return Pattern.matches(telephonyPattern, str);
    }

    public static boolean isAddGroup(Class<?> group) {
        return AddGroup.class.equals(group);
    }

    public static boolean isUpdateGroup(Class<?> group) {
        return UpdateGroup.class.equals(group);
    }

    public static boolean isGroup(Class<?> group, Class<?> specialGroup) {
        return ObjectUtils.nullSafeEquals(group, specialGroup);
    }

    /**
     * 获得对象的描述
     */
    public static String getDescribe(Object obj,String... ignores) {
        StringBuilder result = new StringBuilder();

        Set<String> ignored = new HashSet<>(Arrays.asList(ignores));

        Class<?> clazz = obj.getClass();
        CheckBean checkBean = AnnotationUtils.findAnnotation(clazz, CheckBean.class);
        if(checkBean!=null){
            result = new StringBuilder(checkBean.name() + ": ");
        }
        for (Field field : clazz.getDeclaredFields()) {
            if(ignored.contains(field.getName())){
                continue;
            }
            String fieldName = getFieldName(field);
            ReflectionUtils.makeAccessible(field);
            Object value = ReflectionUtils.getField(field, obj);
            result.append(fieldName).append(":").append(value).append("  ");
        }
        return result.toString();
    }



    public static String getDifferent(Object oldBean,Object newBean,String... ignores){
        Class<?> clazz = oldBean.getClass();
        StringBuilder result= new StringBuilder();
        boolean hasDifferent=false;

        Set<String> ignored = new HashSet<>(Arrays.asList(ignores));

        for (Field field : clazz.getDeclaredFields()) {
            if(ignored.contains(field.getName())){
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            Object oldValue = ReflectionUtils.getField(field, oldBean);
            Object newValue = ReflectionUtils.getField(field, newBean);

            String difference = getDifference(oldValue, newValue,field);
            if(difference!=null){
                result.append(difference).append("  ");
                hasDifferent=true;
            }
        }
        if(!hasDifferent){
            return "无不同点";
        }
        return result.toString();
    }

    private static String getDifference(Object oldValue,Object newValue,Field field){
        if(oldValue==null && newValue==null){
            return null;
        }
        if(oldValue==null){
            return "`新增`"+getFieldName(field)+":"+newValue;
        }else if(newValue==null){
            return "`删除`"+getFieldName(field)+":"+oldValue;
        }else {
            if(oldValue.equals(newValue)){
                return null;
            }else {
                return getFieldName(field)+":"+oldValue + "->" + newValue;
            }
        }
    }

    private static String getFieldName(Field field){
        Check check = AnnotationUtils.findAnnotation(field, Check.class);
        if(check!=null){
            return check.name();
        }
        TelePhone telePhone = AnnotationUtils.findAnnotation(field, TelePhone.class);
        if(telePhone!=null){
            return "电话";
        }
        Email email = AnnotationUtils.findAnnotation(field, Email.class);
        if(email!=null){
            return "邮箱";
        }
        Regex regex = AnnotationUtils.findAnnotation(field, Regex.class);
        if(regex!=null){
            return regex.name();
        }
        return field.getName();
    }



}
