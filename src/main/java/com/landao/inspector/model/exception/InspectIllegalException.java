package com.landao.inspector.model.exception;


import com.landao.inspector.model.collection.IllegalsHolder;

public class InspectIllegalException extends RuntimeException{

    public InspectIllegalException(IllegalsHolder illegalsHolder) {
        this.illegalsHolder = illegalsHolder;
    }

    private IllegalsHolder illegalsHolder;

    public IllegalsHolder getIllegalList() {
        return illegalsHolder;
    }

    public void setIllegalList(IllegalsHolder illegalsHolder) {
        this.illegalsHolder = illegalsHolder;
    }

}
