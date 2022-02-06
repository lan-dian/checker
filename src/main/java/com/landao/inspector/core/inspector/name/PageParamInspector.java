package com.landao.inspector.core.inspector.name;

import com.landao.inspector.core.Inspector;
import com.landao.inspector.model.FeedBack;

import java.lang.reflect.Parameter;

@Inspector
public class PageParamInspector implements ParamInspector{

    @Override
    public boolean supportParameter(String parameterName) {
        return "page".equals(parameterName) || "offset".equals(parameterName);
    }

    @Override
    public FeedBack handleParameter(Parameter parameter, Object value) {
        if(value==null){
            return FeedBack.pass();
        }
        long page = (Long) value;
        if(page<=0){
            String parameterName = parameter.getName();
            return FeedBack.illegal(parameterName,parameterName+"不合法");
        }

        return FeedBack.pass();
    }
}
