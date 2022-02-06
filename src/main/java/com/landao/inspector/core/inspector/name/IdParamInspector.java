package com.landao.inspector.core.inspector.name;

import com.landao.inspector.core.Inspector;
import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.exception.InspectorException;
import com.landao.inspector.utils.InspectorManager;
import org.springframework.util.StringUtils;

import java.lang.reflect.Parameter;

@Inspector
public class IdParamInspector implements ParamInspector {


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
        if (InspectorManager.isLong(valueType)) {
            if ( (Long) value <= 0) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        } else if (InspectorManager.isInteger(valueType)) {
            if ((Integer)value <= 0) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        } else if (InspectorManager.isString(valueType)) {
            if (!StringUtils.hasText((String) value)) {
                return FeedBack.illegal(fieldName,fieldName+"不合法");
            }
        }else {
            throw new InspectorException("不推荐以"+valueType.getName()+"作为id");
        }

        return FeedBack.pass();
    }
}
