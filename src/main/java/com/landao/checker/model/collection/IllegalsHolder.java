package com.landao.checker.model.collection;


import com.landao.checker.model.exception.CheckIllegalException;
import com.landao.checker.utils.CheckerManager;

import java.util.ArrayList;

/**
 * 不合法参数包装类
 */
public class IllegalsHolder extends ArrayList<IllegalField> {

    @SuppressWarnings("all")
    public boolean add(String fieldName,String illegalReason){
        if(CheckerManager.isFastFail()){//说明已经产生一个了
            throw new CheckIllegalException(this);
        }
        return add(new IllegalField(fieldName,illegalReason));
    }

    public boolean illegal(){
        return !isEmpty();
    }

    public IllegalsHolder() {
        super();
    }

    public IllegalsHolder(int initialCapacity) {
        super(initialCapacity);
    }

}
