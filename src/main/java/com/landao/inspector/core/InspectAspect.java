package com.landao.inspector.core;

import com.landao.inspector.InspectorProperties;
import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.special.group.SpecialInspect;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.IllegalsHolder;
import com.landao.inspector.model.Inspect;
import com.landao.inspector.model.exception.InspectIllegalException;
import com.landao.inspector.model.exception.InspectorException;
import com.landao.inspector.utils.InspectorManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Aspect
public class InspectAspect implements Ordered {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private InspectorProperties inspectorProperties;

    private final Map<Class<?>, Inspector> inspectors =new HashMap<>();

    @PostConstruct
    public void initInspectors(){
        Map<String, Inspector> inspectors = applicationContext.getBeansOfType(Inspector.class);
        for (Inspector inspector : inspectors.values()) {
            Set<Class<?>> classes = inspector.supportedClasses();
            for (Class<?> clazz : classes) {
                this.inspectors.put(clazz,inspector);
            }
        }
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" + "||" + "@within(org.springframework.stereotype.Controller)")
    public void doPointCut() {

    }

    @Around("doPointCut()")
    public Object inspectBean(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取分组
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        SpecialInspect specialInspect = AnnotationUtils.findAnnotation(method, SpecialInspect.class);
        Class<?> group=null;
        if(specialInspect!=null){
            group=specialInspect.value();
        }

        Parameter[] parameters = method.getParameters();
        //获取所有参数
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object arg=args[i];
            if(arg==null){//如果参数为空
                //这种情况除非程序员指定了可以为空,不然spring自己就会报错
                continue;
            }
            Class<?> argType = arg.getClass();
            if(arg instanceof MultipartFile){//如果是文件类型的
                continue;
            }
            if(arg instanceof MultipartFile[]){
                continue;
            }
            if(arg instanceof Inspect){
                inspectBean(arg,group);
                Inspect inspect = (Inspect) arg;
                inspect.inspect(group);
            }else if(isRequestBody(argType) || isInspectField(argType)){
                inspectBean(arg,group);
            }else if(inspectors.containsKey(argType)){
                Parameter parameter = parameters[i];
                FeedBack feedBack = inspectors.get(argType).inspect(parameter, arg, "", parameter.getName(), group);
                if(feedBack.requiresNewValue()){
                    args[i]=feedBack.getNewValue();
                }
            }
        }

        if(InspectorManager.illegal()){
            throw new InspectIllegalException(InspectorManager.getIllegalsHolder());
        }

        try {
            return joinPoint.proceed(args);
        } finally {
            InspectorManager.clear();//清理,防止线程池出错
        }
    }



    /**
     * 处理没有特殊情况的校验
     * @param bean 需要校验的对象
     */
    public void inspectBean(Object bean, Class<?> group){
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            Inspector inspector = inspectors.get(fieldType);
            if(inspector!=null){
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, bean);
                String beanName="";
                InspectBean inspectBean = AnnotationUtils.findAnnotation(beanClass, InspectBean.class);
                if(inspectBean!=null){
                    beanName=inspectBean.name();
                }
                FeedBack feedBack = inspector.inspect(field, value, beanName, getFieldName(bean, field), group);
                if(feedBack.requiresNewValue()){
                    ReflectionUtils.setField(field,bean,feedBack.getNewValue());
                }
            }else {
                throw new InspectorException("不支持的类型:"+fieldType.getName());
            }
        }
    }

    private String getFieldName(Object bean, Field field){
        return bean.getClass().getSimpleName() +"."+field.getName();
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
        Inspected inspected = AnnotationUtils.findAnnotation(parameterType, Inspected.class);
        return inspected != null;
    }

    @Override
    public int getOrder() {
        return inspectorProperties.getInterceptor().getOrder();
    }

}
