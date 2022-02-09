package com.landao.checker.utils;

import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.CheckBean;
import com.landao.checker.annotations.special.Email;
import com.landao.checker.annotations.special.Regex;
import com.landao.checker.annotations.special.TelePhone;
import com.landao.checker.annotations.special.group.AddGroup;
import com.landao.checker.annotations.special.group.UpdateGroup;
import com.landao.checker.model.enums.TrimType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 校验参数是否通过,返回的都是boolean值
 */
public abstract class CheckUtils {

    public static final String emailPattern = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    public static final String telephonyPattern = "^[1][358]\\\\d{9}$";

    /**
     * id不为空并且大于0
     *
     * @param id 主键id
     */
    public static void id(Long id, String fieldName, String name) {
        if (id == null || id < 0) {
            throwIllegal(fieldName, name + "不合法");
        }
    }

    /**
     * id不为空并且大于0
     *
     * @param id 主键id
     */
    public static void id(Integer id, String fieldName, String name) {
        if (id == null || id < 0) {
            throwIllegal(fieldName, name + "不合法");
        }
    }

    public static void lengthEqual(int num, int length, String fieldName, String name) {
        if (countLength(num) != length) {
            throwIllegal(fieldName, name + "长度必须为" + length + "位");
        }
    }

    public static void lengthEqual(long num, int length, String fieldName, String name) {
        if (countLength(num) != length) {
            throwIllegal(fieldName, name + "长度必须为" + length + "位");
        }
    }

    public static void moreThan(int num, int target, String fieldName, String name) {
        if (num < target) {
            throwIllegal(fieldName, name + "不能小于" + target);
        }
    }

    public static void moreThan(long num, int target, String fieldName, String name) {
        if (num < target) {
            throwIllegal(fieldName, name + "不能小于" + target);
        }
    }

    public static void more(int num, int target, String fieldName, String name) {
        if (num <= target) {
            throwIllegal(fieldName, name + "必须大于" + target);
        }
    }

    public static void more(long num, int target, String fieldName, String name) {
        if (num <= target) {
            throwIllegal(fieldName, name + "必须大于" + target);
        }
    }

    public static void lessEqual(int num, int target, String fieldName, String name) {
        if (num > target) {
            throwIllegal(fieldName, name + "不能大于" + target);
        }
    }

    public static void lessEqual(long num, int target, String fieldName, String name) {
        if (num > target) {
            throwIllegal(fieldName, name + "不能大于" + target);
        }
    }

    public static void less(int num, int target, String fieldName, String name) {
        if (num >= target) {
            throwIllegal(fieldName, name + "必须小于" + target);
        }
    }

    public static void less(long num, int target, String fieldName, String name) {
        if (num >= target) {
            throwIllegal(fieldName, name + "必须小于" + target);
        }
    }

    public static void moreThan(int num, int target, String fieldName, String name,String targetName) {
        if (num < target) {
            throwIllegal(fieldName, name + "不能小于" + targetName);
        }
    }

    public static void moreThan(long num, int target, String fieldName, String name,String targetName) {
        if (num < target) {
            throwIllegal(fieldName, name + "不能小于" + targetName);
        }
    }

    public static void more(int num, int target, String fieldName, String name,String targetName) {
        if (num <= target) {
            throwIllegal(fieldName, name + "必须大于" + targetName);
        }
    }

    public static void more(long num, int target, String fieldName, String name,String targetName) {
        if (num <= target) {
            throwIllegal(fieldName, name + "必须大于" + targetName);
        }
    }

    public static void lessEqual(int num, int target, String fieldName, String name, String targetName) {
        if (num > target) {
            throwIllegal(fieldName, name + "不能大于" + targetName);
        }
    }

    public static void lessEqual(long num, int target, String fieldName, String name, String targetName) {
        if (num > target) {
            throwIllegal(fieldName, name + "不能大于" + targetName);
        }
    }

    public static void less(int num, int target, String fieldName, String name,String targetName) {
        if (num >= target) {
            throwIllegal(fieldName, name + "必须小于" + targetName);
        }
    }

    public static void less(long num, int target, String fieldName, String name,String targetName) {
        if (num >= target) {
            throwIllegal(fieldName, name + "必须小于" + targetName);
        }
    }

    public static void positive(int num, String fieldName, String name) {
        if (num <= 0) {
            throwIllegal(fieldName, name + "必须为正数");
        }
    }

    public static void positive(long num, String fieldName, String name) {
        if (num <= 0) {
            throwIllegal(fieldName, name + "必须为正数");
        }
    }

    public static void notNegative(int num, String fieldName, String name) {
        if (num < 0) {
            throwIllegal(fieldName, name + "不能为负数");
        }
    }

    public static void notNegative(long num, String fieldName, String name) {
        if (num < 0) {
            throwIllegal(fieldName, name + "不能为负数");
        }
    }

    public static String string(String str, int maxLength, String fieldName, String name) {
        return string(str, maxLength, TrimType.All, fieldName, name);
    }


    public static String string(String str, int maxLength, TrimType trimType, String fieldName, String name) {
        if (!StringUtils.hasText(str)) {
            throwIllegal(fieldName, name + "不能为空");
        } else {
            str = trimString(str, trimType);
            if (str.length() > maxLength) {
                throwIllegal(fieldName, name + "不能超过" + maxLength + "个字符");
            }
        }
        return str;
    }

    public static String string(String str, int minLength, int maxLength, String fieldName, String name) {
        return string(str,minLength,maxLength,TrimType.All,fieldName,name);
    }

    public static String string(String str, int minLength, int maxLength, TrimType trimType, String fieldName, String name) {
        if (!StringUtils.hasText(str)) {
            throwIllegal(fieldName, name + "不能为空");
        } else {
            str = trimString(str, trimType);
            int length = str.length();
            if (length<minLength || length > maxLength) {
                throwIllegal(fieldName, name + "必须在" + minLength+"-"+maxLength + "个字符之间");
            }
        }
        return str;
    }

    public static void in(String str,String fieldName,String name,String... ins){
        for (String in : ins) {
            if(Objects.equals(in,str)){
                return;
            }
        }
        throwIllegal(fieldName,name+"必须为"+ Arrays.toString(ins)+"之一");
    }

    public static void before(LocalDate date,LocalDate target,String fieldName,String name){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+target+"之前");
        }
    }

    public static void before(LocalTime date,LocalTime target,String fieldName,String name){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+target+"之前");
        }
    }

    public static void before(LocalDateTime date, LocalDateTime target, String fieldName, String name){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+target+"之前");
        }
    }

    public static void beforeEqual(LocalDate date,LocalDate target,String fieldName,String name){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+target);
        }
    }

    public static void beforeEqual(LocalTime date,LocalTime target,String fieldName,String name){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+target);
        }
    }

    public static void beforeEqual(LocalDateTime date, LocalDateTime target, String fieldName, String name){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+target);
        }
    }

    public static void after(LocalDate date,LocalDate target,String fieldName,String name){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+target+"之后");
        }
    }

    public static void after(LocalTime date,LocalTime target,String fieldName,String name){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+target+"之后");
        }
    }

    public static void after(LocalDateTime date, LocalDateTime target, String fieldName, String name){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+target+"之后");
        }
    }

    public static void afterEqual(LocalDate date,LocalDate target,String fieldName,String name){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+target);
        }
    }

    public static void afterEqual(LocalTime date,LocalTime target,String fieldName,String name){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+target);
        }
    }

    public static void afterEqual(LocalDateTime date, LocalDateTime target, String fieldName, String name){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+target);
        }
    }

    public static void before(LocalDate date,LocalDate target,String fieldName,String name,String targetName){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之前");
        }
    }

    public static void before(LocalTime date,LocalTime target,String fieldName,String name,String targetName){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之前");
        }
    }

    public static void before(LocalDateTime date, LocalDateTime target, String fieldName, String name,String targetName){
        if(date.compareTo(target)>=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之前");
        }
    }

    public static void beforeEqual(LocalDate date,LocalDate target,String fieldName,String name,String targetName){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+targetName);
        }
    }

    public static void beforeEqual(LocalTime date,LocalTime target,String fieldName,String name,String targetName){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+targetName);
        }
    }

    public static void beforeEqual(LocalDateTime date, LocalDateTime target, String fieldName, String name,String targetName){
        if(date.compareTo(target)>0){
            throwIllegal(fieldName,name+"不能晚于"+targetName);
        }
    }

    public static void after(LocalDate date,LocalDate target,String fieldName,String name,String targetName){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之后");
        }
    }

    public static void after(LocalTime date,LocalTime target,String fieldName,String name,String targetName){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之后");
        }
    }

    public static void after(LocalDateTime date, LocalDateTime target, String fieldName, String name,String targetName){
        if(date.compareTo(target)<=0){
            throwIllegal(fieldName,name+"必须在"+targetName+"之后");
        }
    }

    public static void afterEqual(LocalDate date,LocalDate target,String fieldName,String name,String targetName){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+targetName);
        }
    }

    public static void afterEqual(LocalTime date,LocalTime target,String fieldName,String name,String targetName){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+targetName);
        }
    }

    public static void afterEqual(LocalDateTime date, LocalDateTime target, String fieldName, String name,String targetName){
        if(date.compareTo(target)<0){
            throwIllegal(fieldName,name+"不能早于"+targetName);
        }
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
    public static String getDescribe(Object obj, String... ignores) {
        StringBuilder result = new StringBuilder();

        Set<String> ignored = new HashSet<>(Arrays.asList(ignores));

        Class<?> clazz = obj.getClass();
        CheckBean checkBean = AnnotationUtils.findAnnotation(clazz, CheckBean.class);
        if (checkBean != null) {
            result = new StringBuilder(checkBean.name() + ": ");
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (ignored.contains(field.getName())) {
                continue;
            }
            String fieldName = getFieldName(field);
            ReflectionUtils.makeAccessible(field);
            Object value = ReflectionUtils.getField(field, obj);
            result.append(fieldName).append(":").append(value).append("  ");
        }
        return result.toString();
    }


    public static String getDifferent(Object oldBean, Object newBean, String... ignores) {
        Class<?> clazz = oldBean.getClass();
        StringBuilder result = new StringBuilder();
        boolean hasDifferent = false;

        Set<String> ignored = new HashSet<>(Arrays.asList(ignores));

        for (Field field : clazz.getDeclaredFields()) {
            if (ignored.contains(field.getName())) {
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            Object oldValue = ReflectionUtils.getField(field, oldBean);
            Object newValue = ReflectionUtils.getField(field, newBean);

            String difference = getDifference(oldValue, newValue, field);
            if (difference != null) {
                result.append(difference).append("  ");
                hasDifferent = true;
            }
        }
        if (!hasDifferent) {
            return "无不同点";
        }
        return result.toString();
    }

    private static String getDifference(Object oldValue, Object newValue, Field field) {
        if (oldValue == null && newValue == null) {
            return null;
        }
        if (oldValue == null) {
            return "`新增`" + getFieldName(field) + ":" + newValue;
        } else if (newValue == null) {
            return "`删除`" + getFieldName(field) + ":" + oldValue;
        } else {
            if (oldValue.equals(newValue)) {
                return null;
            } else {
                return getFieldName(field) + ":" + oldValue + "->" + newValue;
            }
        }
    }

    private static String getFieldName(Field field) {
        Check check = AnnotationUtils.findAnnotation(field, Check.class);
        if (check != null) {
            return check.name();
        }
        TelePhone telePhone = AnnotationUtils.findAnnotation(field, TelePhone.class);
        if (telePhone != null) {
            return "电话";
        }
        Email email = AnnotationUtils.findAnnotation(field, Email.class);
        if (email != null) {
            return "邮箱";
        }
        Regex regex = AnnotationUtils.findAnnotation(field, Regex.class);
        if (regex != null) {
            return regex.name();
        }
        return field.getName();
    }

    private static void throwIllegal(String fieldName, String reason) {
        CheckerManager.addIllegal(fieldName, reason);
    }

    private static String trimString(String str, TrimType trimType) {
        switch (trimType) {
            case All: {
                str = StringUtils.trimAllWhitespace(str);
                break;
            }
            case Trail: {
                str = StringUtils.trimTrailingWhitespace(str);
                break;
            }
            case Head: {
                str = StringUtils.trimLeadingWhitespace(str);
                break;
            }
        }
        return str;
    }


}
