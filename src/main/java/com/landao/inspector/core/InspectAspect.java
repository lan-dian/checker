package com.landao.inspector.core;

import com.landao.inspector.InspectorProperties;
import com.landao.inspector.annotations.InspectField;
import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.special.group.SpecialInspect;
import com.landao.inspector.core.inspector.Inspector;
import com.landao.inspector.core.inspector.IntegerInspector;
import com.landao.inspector.core.inspector.LongInspector;
import com.landao.inspector.core.inspector.StringInspector;
import com.landao.inspector.model.IllegalsHolder;
import com.landao.inspector.model.Inspect;
import com.landao.inspector.model.enums.InspectMode;
import com.landao.inspector.model.exception.InspectIllegalException;
import com.landao.inspector.model.exception.InspectorException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Aspect
public class InspectAspect implements Ordered {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private InspectorProperties inspectorProperties;

    @SuppressWarnings("all")
    private Map<Class, Inspector> inspectorCache=new HashMap<Class, Inspector>(){{
        put(Integer.class,new IntegerInspector());
        put(String.class,new StringInspector());
        put(Long.class,new LongInspector());
    }};

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" + "||" + "@within(org.springframework.stereotype.Controller)")
    public void doPointCut() {

    }

    @Before("doPointCut()")
    public void inspect(JoinPoint joinPoint) {
        //获取所有参数
        Object[] parameters = joinPoint.getArgs();
        for (Object parameter : parameters) {
            if(parameter==null){
                continue;
            }
            Class<?> parameterClass = parameter.getClass();
            if(parameter instanceof Inspect){
                Class<?> group = inspectBean(parameter, joinPoint);
                Inspect inspect = (Inspect) parameter;
                inspect.inspect(group);
            }else if(isRequestBody(parameterClass) || isInspectField(parameterClass)){
                inspectBean(parameter,joinPoint);
            }
        }
    }

    private Class<?> inspectBean(Object parameter,JoinPoint joinPoint){
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        SpecialInspect specialInspect = AnnotationUtils.findAnnotation(method, SpecialInspect.class);
        Class<?> group=null;
        if(specialInspect!=null){
            group=specialInspect.value();
        }
        inspect(parameter,group);
        return group;
    }



    /**
     * 处理没有特殊情况的校验
     * @param bean 需要校验的对象
     */
    public void inspect(Object bean,Class<?> group){
        Class<?> beanClass = bean.getClass();

        Field[] fields = beanClass.getDeclaredFields();
        IllegalsHolder illegalsHolder =new IllegalsHolder(fields.length);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            Inspector inspector = inspectorCache.get(fieldType);

            if(inspector!=null){
                String inspect = inspector.inspect(field,bean,group);
                if(inspect!=null){
                    illegalsHolder.add(field.getName(),inspect);
                }
            }else {
                throw new InspectorException("不支持的类型:"+fieldType.getName());
            }
        }
        if(illegalsHolder.illegal()){
            throw new InspectIllegalException(illegalsHolder);
        }
    }

    private boolean isRequestBody(Class<?> parameterType){
        RequestBody requestBody = AnnotationUtils.findAnnotation(parameterType, RequestBody.class);
        if (requestBody != null) {
            return true;
        }
        InspectBean inspectBean = AnnotationUtils.findAnnotation(parameterType, InspectBean.class);
        if (inspectBean != null) {
            return true;
        }
        return false;
    }


    private boolean isInspectField(Class<?> parameterType){
        InspectField inspectField = AnnotationUtils.findAnnotation(parameterType, InspectField.class);
        return inspectField != null;
    }

    @Override
    public int getOrder() {
        return inspectorProperties.getInterceptor().getOrder();
    }

}
