package com.landao.inspector.core.inspector;

import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.special.Email;
import com.landao.inspector.annotations.special.Regex;
import com.landao.inspector.annotations.special.TelePhone;
import com.landao.inspector.core.Handler;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;
import com.landao.inspector.model.enums.TrimType;
import com.landao.inspector.utils.InspectUtils;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;

@Handler
public class StringInspector extends AbstractInspector {

    @Override
    public TypeSet supportedChain(TypeSet set) {
        return set.addChain(String.class);
    }

    @Override
    public FeedBack specialInspect(AnnotatedElement annotatedElement, Object value,String beanName,String fieldName, Class<?> group) {
        String fieldValue=(String) value;
        //todo xss 校验

        //email
        Email email = AnnotationUtils.findAnnotation(annotatedElement, Email.class);
        if(email!=null){
            if(!InspectUtils.isEmail(fieldValue)){
                return FeedBack.illegal(fieldName,beanName+"邮箱格式不合法");
            }
        }
        //telephone
        TelePhone telePhone = AnnotationUtils.findAnnotation(annotatedElement, TelePhone.class);
        if(telePhone!=null){
            if(!InspectUtils.isTelephone(fieldValue)){
                return FeedBack.illegal(fieldName,beanName+"电话格式不合法");
            }
        }
        //regex
        Regex regex = AnnotationUtils.findAnnotation(annotatedElement, Regex.class);
        if(regex!=null){
            if(!Pattern.matches(regex.pattern(),fieldValue)){
                return FeedBack.illegal(fieldName,beanName+regex.name()+"不合法");
            }
        }

        Inspected inspected = AnnotationUtils.findAnnotation(annotatedElement, Inspected.class);
        if (inspected == null) {//说明用户想自己检查
            return FeedBack.pass();
        }
        String displayName=getDisplayName(beanName,inspected.name());

        //不可为null
        if (!StringUtils.hasText(fieldValue)) {
            return FeedBack.illegal(fieldName,displayName+"不能为空");
        }
        //剪枝
        TrimType trimType = inspected.trimType();
        if (trimType == TrimType.All) {
            fieldValue = StringUtils.trimWhitespace(fieldValue);
        } else if (trimType == TrimType.Trail) {
            fieldValue = StringUtils.trimTrailingWhitespace(fieldValue);
        } else if (trimType == TrimType.Head) {
            fieldValue = StringUtils.trimLeadingWhitespace(fieldValue);
        }
        //长度校验
        int length = fieldValue.length();
        long min = inspected.min();
        long max = inspected.max();

        if (min > 0 && length < min) {
            return FeedBack.illegal(fieldName,displayName+"必须在"+min+"-"+max+"个字符之间");
        }
        if(length>max){
            return FeedBack.illegal(fieldName,displayName+"不能多于"+max+"个字符");
        }

        //设置裁剪后的值
        return FeedBack.pass(fieldValue);

    }


}
