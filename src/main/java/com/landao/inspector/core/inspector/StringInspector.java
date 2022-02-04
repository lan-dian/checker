package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.InspectField;
import com.landao.inspector.annotations.special.Email;
import com.landao.inspector.annotations.special.Regex;
import com.landao.inspector.annotations.special.TelePhone;
import com.landao.inspector.annotations.special.group.AddGroup;
import com.landao.inspector.annotations.special.group.Id;
import com.landao.inspector.annotations.special.group.UpdateGroup;
import com.landao.inspector.model.enums.TrimType;
import com.landao.inspector.model.exception.InspectIllegalException;
import com.landao.inspector.utils.InspectUtils;
import com.landao.inspector.utils.InspectorManager;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.regex.Pattern;


public class StringInspector implements Inspector {


    @Override
    public String inspect(Field field, Object bean,Class<?> group) {

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
                    String idValue = (String)ReflectionUtils.getField(field, bean);
                    if(!StringUtils.hasText(idValue)){
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
        String fieldValue = (String) ReflectionUtils.getField(field, bean);
        if (nullable != null && fieldValue == null) {
            return null;//通过
        }
        //不可为null
        if (!StringUtils.hasText(fieldValue)) {
            return InspectorManager.getFieldName(inspectField, bean) + "不能为空";
        }
        //剪枝
        TrimType trimType = inspectField.trimType();
        if (trimType == TrimType.All) {
            fieldValue = StringUtils.trimWhitespace(fieldValue);
        } else if (trimType == TrimType.Trail) {
            fieldValue = StringUtils.trimTrailingWhitespace(fieldValue);
        } else if (trimType == TrimType.Head) {
            fieldValue = StringUtils.trimLeadingWhitespace(fieldValue);
        }
        //长度校验
        int length = fieldValue.length();
        long min = inspectField.min();
        long max = inspectField.max();

        if (min > 0 && length < min) {
            return InspectorManager.getFieldName(inspectField, bean) + "必须在"+min+"-"+max+"个字符之间";
        }
        if(length>max){
            return InspectorManager.getFieldName(inspectField,bean)+"不能多于"+max+"个字符";
        }
        //email
        Email email = AnnotationUtils.findAnnotation(field, Email.class);
        if(email!=null){
            if(!InspectUtils.isEmail(fieldValue)){
                return InspectorManager.getFieldName(inspectField,bean)+"不合法";
            }
        }
        //telephone
        TelePhone telePhone = AnnotationUtils.findAnnotation(field, TelePhone.class);
        if(telePhone!=null){
            if(!InspectUtils.isTelephone(fieldValue)){
                return InspectorManager.getFieldName(inspectField,bean)+"不合法";
            }
        }
        //regex
        Regex regex = AnnotationUtils.findAnnotation(field, Regex.class);
        if(regex!=null){
            if(!Pattern.matches(regex.pattern(),fieldValue)){
                return InspectorManager.getFieldName(inspectField,bean)+"不合法";
            }
        }
        //设置裁剪后的值
        ReflectionUtils.setField(field,bean,fieldValue);
        return null;
    }

}
