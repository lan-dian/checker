package com.landao.inspector.core.inspector.type;

import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public interface TypeInspector {

    TypeSet supportClasses();

    FeedBack inspect(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group);

}
