package com.landao.checker.model.collection;

import java.util.HashSet;

public class TypeSet extends HashSet<Class<?>> {

    public TypeSet() {//一般不会超过两个
        super(2);
    }

    public TypeSet addChain(Class<?> aClass) {
        super.add(aClass);
        return this;
    }

}
