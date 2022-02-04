package com.landao.inspector.utils;

import org.springframework.util.ReflectionUtils;

import java.util.regex.Pattern;

/**
 * 校验参数是否通过,返回的都是boolean值
 */
public abstract class InspectUtils {

    public static final String emailPattern="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    public static final String telephonyPattern="^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    /**
     * id不为空并且大于0
     * @param id 主键id
     */
    public static boolean checkId(Long id){
        return id != null && id > 0;
    }

    /**
     * id不为空并且大于0
     * @param id 主键id
     */
    public static boolean checkId(Integer id){
        return id != null && id > 0;
    }

    /**
     * 统计数字的位数
     * @param num 数字
     */
    public static int countLength(int num){
        if(num<0){
            num=-num;
        }
        int count=0;
        while (num>0){
            count++;
            num/=10;
        }
        return count;
    }

    /**
     * 统计数字的位数
     * @param num 数字
     */
    public static int countLength(long num){
        if(num<0){
            num=-num;
        }
        int count=0;
        while (num>0){
            count++;
            num/=10;
        }
        return count;
    }

    public static boolean isEmail(String str){
        return Pattern.matches(emailPattern, str);
    }

    public static boolean isTelephone(String str){
        return Pattern.matches(telephonyPattern,str);
    }






}
