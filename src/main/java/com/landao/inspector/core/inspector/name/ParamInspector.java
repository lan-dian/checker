package com.landao.inspector.core.inspector.name;

import com.landao.inspector.model.FeedBack;

import javax.websocket.Extension;
import java.lang.reflect.Parameter;

public interface ParamInspector {

    boolean supportParameter(String parameterName);

    FeedBack handleParameter(Parameter parameter,Object value);

}
