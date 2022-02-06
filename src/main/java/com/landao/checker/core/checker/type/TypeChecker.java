package com.landao.checker.core.checker.type;

import com.landao.checker.model.FeedBack;
import com.landao.checker.model.collection.TypeSet;

import java.lang.reflect.AnnotatedElement;

public interface TypeChecker {

    TypeSet supportClasses();

    FeedBack check(AnnotatedElement annotatedElement, Object value, String beanName, String fieldName, Class<?> group);

}
