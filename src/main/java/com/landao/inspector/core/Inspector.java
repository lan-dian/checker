package com.landao.inspector.core;

import com.landao.inspector.model.FeedBack;
import com.landao.inspector.model.collection.TypeSet;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public interface Inspector{

    TypeSet supportedClasses();

    FeedBack inspect(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group);

}
