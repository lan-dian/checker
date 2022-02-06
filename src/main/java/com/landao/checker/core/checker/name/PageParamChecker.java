package com.landao.checker.core.checker.name;

import com.landao.checker.core.checker.Checker;
import com.landao.checker.model.FeedBack;
import com.landao.checker.utils.CheckerManager;

import java.lang.reflect.Parameter;

@Checker
public class PageParamChecker implements ParamChecker {

    @Override
    public boolean supportParameter(String parameterName) {
        return "page".equals(parameterName) || "offset".equals(parameterName);
    }

    @Override
    public FeedBack handleParameter(Parameter parameter, Object value) {
        if(value==null){
            return FeedBack.pass();
        }
        Class<?> valueType = value.getClass();
        if(CheckerManager.isInteger(valueType)){
            int page = (Integer) value;
            if(page<=0){
                String parameterName = parameter.getName();
                return FeedBack.illegal(parameterName,parameterName+"不合法");
            }
        }else if(CheckerManager.isLong(valueType)){
            long page = (Long) value;
            if(page<=0){
                String parameterName = parameter.getName();
                return FeedBack.illegal(parameterName,parameterName+"不合法");
            }
        }

        return FeedBack.pass();
    }
}
