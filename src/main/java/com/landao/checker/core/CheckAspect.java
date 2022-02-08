package com.landao.checker.core;

import com.landao.checker.CheckerProperties;
import com.landao.checker.annotations.Check;
import com.landao.checker.annotations.CheckBean;
import com.landao.checker.annotations.special.group.CheckGroup;
import com.landao.checker.core.checker.name.JumpOverParam;
import com.landao.checker.core.checker.name.ParamChecker;
import com.landao.checker.core.checker.type.TypeChecker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.exception.CheckIllegalException;
import com.landao.checker.utils.CheckUtils;
import com.landao.checker.utils.CheckerManager;
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
public class CheckAspect implements Ordered {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CheckerProperties checkerProperties;

    private final Map<Class<?>, TypeChecker> typeInspectors =new HashMap<>();

    private final List<ParamChecker> paramCheckers =new ArrayList<>();

    @PostConstruct
    public void initInspectors(){
        Map<String, TypeChecker> typeInspectors = applicationContext.getBeansOfType(TypeChecker.class);
        for (TypeChecker typeChecker : typeInspectors.values()) {
            Set<Class<?>> classes = typeChecker.supportClasses();
            for (Class<?> clazz : classes) {
                this.typeInspectors.put(clazz, typeChecker);
            }
        }
        Map<String, ParamChecker> paramInspectors = applicationContext.getBeansOfType(ParamChecker.class);
        this.paramCheckers.addAll(paramInspectors.values());
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" + "||" + "@within(org.springframework.stereotype.Controller)")
    public void doPointCut() {

    }

    @Around("doPointCut()")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        CheckerManager.clear();

        //获取分组
        MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckGroup checkGroup = AnnotationUtils.findAnnotation(method, CheckGroup.class);
        Class<?> group=null;
        if(checkGroup !=null){
            group= checkGroup.value();
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
            if(arg instanceof Checked){
                CheckBean checkBean = AnnotationUtils.findAnnotation(argType, CheckBean.class);
                checkBean(arg,"", checkBean,group);
                Checked checked = (Checked) arg;
                if(CheckUtils.isAddGroup(group)){
                    checked.addCheck("");
                }else if(CheckUtils.isUpdateGroup(group)){
                    checked.updateCheck("");
                }
                checked.check(group,"");
            }else if(isRequestBody(argType) || isInspectField(argType)){
                CheckBean checkBean = AnnotationUtils.findAnnotation(argType, CheckBean.class);
                checkBean(arg,"", checkBean,group);
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
                    for (ParamChecker paramChecker : paramCheckers) {
                        if(paramChecker.supportParameter(parameterName)){
                            FeedBack feedBack = paramChecker.handleParameter(parameter, arg);
                            if(feedBack.requiresNewValue()){
                                args[i]=feedBack.getNewValue();
                            }
                            hasParamInspector=true;
                            break;
                        }
                    }
                }
                if(!hasParamInspector){//依据类型根据用户自定义的处理
                    FeedBack feedBack = typeInspectors.get(argType).check(parameter, arg, "", parameter.getName(), group);
                    if(feedBack.requiresNewValue()){
                        args[i]=feedBack.getNewValue();
                    }
                }
            }
        }

        if(CheckerManager.illegal()){
            throw new CheckIllegalException(CheckerManager.getIllegalsHolder());
        }

        try {
            return joinPoint.proceed(args);
        } finally {
            CheckerManager.clear();//清理,防止线程池出错
        }
    }




    /**
     * 处理没有特殊情况的校验
     * @param bean 需要校验的对象
     */
    public void checkBean(Object bean, String className, CheckBean checkBean, Class<?> group){
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();

        String beanName="";
        if(checkBean !=null){
            beanName= checkBean.name();
        }

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            TypeChecker typeChecker = typeInspectors.get(fieldType);
            if(typeChecker !=null){
                ReflectionUtils.makeAccessible(field);
                Object value = ReflectionUtils.getField(field, bean);
                FeedBack feedBack = typeChecker.check(field, value, beanName,className+field.getName(), group);
                if(feedBack.requiresNewValue()){
                    ReflectionUtils.setField(field,bean,feedBack.getNewValue());
                }
            }else if(annotatedInspectBean(field)){//嵌套
                className=field.getName()+".";
                ReflectionUtils.makeAccessible(field);
                Object innerBean = ReflectionUtils.getField(field, bean);
                CheckBean innerCheckBean = AnnotationUtils.findAnnotation(field, CheckBean.class);
                if(innerBean==null){
                    assert innerCheckBean !=null;
                    if(!innerCheckBean.nullable()){
                        addIllegal(className, innerCheckBean.name()+"不能为空");
                    }
                }else {
                    checkBean(innerBean,className, innerCheckBean,group);
                    if(innerBean instanceof Checked){
                        Checked checked = (Checked) innerBean;
                        checked.check(group,className);
                    }
                }
            }
        }
    }

    private void addIllegal(String fieldName , String illegalReason) {
        CheckerManager.addIllegal(fieldName, illegalReason);
    }

    private boolean annotatedInspectBean(Field field){
        CheckBean checkBean = AnnotationUtils.findAnnotation(field, CheckBean.class);
        return checkBean !=null;
    }

    private boolean isRequestBody(Class<?> parameterType){
        RequestBody requestBody = AnnotationUtils.findAnnotation(parameterType, RequestBody.class);
        if (requestBody != null) {
            return true;
        }
        CheckBean checkBean = AnnotationUtils.findAnnotation(parameterType, CheckBean.class);
        if (checkBean != null) {
            return true;
        }
        return false;
    }


    private boolean isInspectField(Class<?> parameterType){
        Check check = AnnotationUtils.findAnnotation(parameterType, Check.class);
        return check != null;
    }

    @Override
    public int getOrder() {
        return checkerProperties.getInterceptor().getOrder();
    }

}
