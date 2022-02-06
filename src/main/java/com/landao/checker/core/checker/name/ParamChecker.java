package com.landao.checker.core.checker.name;

import com.landao.checker.model.FeedBack;

import java.lang.reflect.Parameter;

public interface ParamChecker {

    boolean supportParameter(String parameterName);

    FeedBack handleParameter(Parameter parameter,Object value);

}
