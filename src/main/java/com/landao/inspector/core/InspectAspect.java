package com.landao.inspector.core;

import com.landao.inspector.InspectorProperties;
import com.landao.inspector.annotations.Inspected;
import com.landao.inspector.annotations.InspectBean;
import com.landao.inspector.annotations.special.group.SpecialInspect;
import com.landao.inspector.core.inspector.name.JumpOverParam;
import com.landao.inspector.core.inspector.name.ParamInspector;
import com.landao.inspector.core.inspector.type.TypeInspector;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.exception.InspectIllegalException;
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
import java.util.*;
import java.util.stream.Collectors;


@Aspect
public class InspectAspect implements Ordered {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private InspectorProperties inspectorProperties;

    private final Map<Class<?>, TypeInspector> typeInspectors =new HashMap<>();

    private final List<ParamInspector> paramInspectors=new ArrayList<>();

    @PostConstruct
    public void initInspectors(){
        Map<String, TypeInspector> typeInspectors = applicationContext.getBeansOfType(TypeInspector.class);
        for (TypeInspector typeInspector : typeInspectors.values()) {
            Set<Class<?>> classes = typeInspector.supportClasses();
            for (Class<?> clazz : classes) {
                this.typeInspectors.put(clazz, typeInspector);
            }
        }
        Map<String, ParamInspector> paramInspectors = applicationContext.getBeansOfType(ParamInspector.class);
        this.paramInspectors.addAll(paramInspectors.values());
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" + "||" + "@within(org.springframework.stereotype.Controller)")
    public void doPointCut() {

    }

    @Around("doPointCut()")
    public Object inspect(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取分组
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        SpecialInspect specialInspect = AnnotationUtils.findAnnotation(method, SpecialInspect.class);
        Class<?> group=null;
        if(specialInspect!=null){
            group=specialInspect.value();
        }

        Set<String> paramBan=Collections.emptySet();
        JumpOverParam jumpOverParam = AnnotationUtils.findAnnotation(method, JumpOverParam.class);
        if(jumpOverParam!=null){
            paramBan = Arrays.stream(jumpOverParam.onlyFor()).collect(Collectors.toSet());
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
                InspectBean inspectBean = AnnotationUtils.findAnnotation(argType, InspectBean.class);
                inspectBean(arg,"",inspectBean,group);
                Inspect inspect = (Inspect) arg;
                inspect.inspect(group,"");
            }else if(isRequestBody(argType) || isInspectField(argType)){
                InspectBean inspectBean = AnnotationUtils.findAnnotation(argType, InspectBean.class);
                inspectBean(arg,"",inspectBean,group);
            }else if(typeInspectors.containsKey(argType)){
                Parameter parameter = parameters[i];
                String parameterName = parameter.getName();
                boolean beBan=false;
                if(jumpOverParam!=null){
                    if(paramBan.isEmpty()){
                        //全部进制
                        beBan=true;
                    }else if(paramBan.contains(parameterName)){
                        beBan=true;
                    }
                }
                boolean hasParamInspector=false;
                if(!beBan){
                    for (ParamInspector paramInspector : paramInspectors) {
                        if(paramInspector.supportParameter(parameterName)){
                            FeedBack feedBack = paramInspector.handleParameter(parameter, arg);
                            if(feedBack.requiresNewValue()){
                                args[i]=feedBack.getNewValue();
                            }
                            hasParamInspector=true;
                            break;
                        }
                    }
                }
                if(!hasParamInspector){//依据类型根据用户自定义的处理
                    FeedBack feedBack = typeInspectors.get(argType).inspect(parameter, arg, "", parameter.getName(), group);
                    if(feedBack.requiresNewValue()){
                        args[i]=feedBack.getNewValue();
                    }
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
    public void inspectBean(Object bean,String className,InspectBean inspectBean,Class<?> group){
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();

        String beanName="";
        if(inspectBean!=null){
            beanName=inspectBean.name();
        }

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            TypeInspector typeInspector = typeInspectors.get(fieldType);
            if(typeInspector !=null){
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, bean);
                FeedBack feedBack = typeInspector.inspect(field, value, beanName,className+field.getName(), group);
                if(feedBack.requiresNewValue()){
                    ReflectionUtils.setField(field,bean,feedBack.getNewValue());
                }
            }else if(annotatedInspectBean(field)){//嵌套
                className=field.getName()+".";
                ReflectionUtils.makeAccessible(field);
                Object innerBean = ReflectionUtils.getField(field, bean);
                InspectBean innerInspectBean = AnnotationUtils.findAnnotation(field, InspectBean.class);
                if(innerBean==null){
                    assert innerInspectBean!=null;
                    if(!innerInspectBean.nullable()){
                        addIllegal(className,innerInspectBean.name()+"不能为空");
                    }
                }else {
                    inspectBean(innerBean,className,innerInspectBean,group);
                    if(innerBean instanceof Inspect){
                        Inspect inspect = (Inspect) innerBean;
                        inspect.inspect(group,className);
                    }
                }
            }
        }
    }

    private void addIllegal(String fieldName , String illegalReason) {
        InspectorManager.addIllegal(fieldName, illegalReason);
    }

    private boolean annotatedInspectBean(Field field){
        InspectBean inspectBean = AnnotationUtils.findAnnotation(field, InspectBean.class);
        return inspectBean!=null;
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
