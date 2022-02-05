package com.landao.inspector.model.collection;


import com.landao.inspector.model.exception.InspectIllegalException;
import com.landao.inspector.utils.InspectorManager;

import java.util.ArrayList;

/**
 * 不合法参数包装类
 */
public class IllegalsHolder extends ArrayList<IllegalField> {

    @SuppressWarnings("all")
    public boolean add(String fieldName,String illegalReason){
        if(InspectorManager.isFastFail()){//说明已经产生一个了
            throw new InspectIllegalException(this);
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
