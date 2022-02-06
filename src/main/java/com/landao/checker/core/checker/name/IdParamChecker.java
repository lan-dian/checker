package com.landao.checker.core.checker.name;

import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.model.exception.CheckerException;
import com.landao.checker.utils.CheckerManager;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;

@Checker
public class IdParamChecker implements ParamChecker {


    @Override
    public boolean supportParameter(String parameterName) {
        return (parameterName.endsWith("Id") || parameterName.endsWith("id"));
    }

    @Override
    public FeedBack handleParameter(Parameter parameter, Object value) {
        if(value==null){
            return FeedBack.pass();
        }
        String fieldName=parameter.getName();

        Class<?> valueType = value.getClass();
        if (CheckerManager.isLong(valueType)) {
            if ( (Long) value <= 0) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        } else if (CheckerManager.isInteger(valueType)) {
            if ((Integer)value <= 0) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        } else if (CheckerManager.isString(valueType)) {
            if (!StringUtils.hasText((String) value)) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        }else {
            throw new CheckerException("不推荐以"+valueType.getName()+"作为id");
        }

        return FeedBack.pass();
    }
}
