package com.landao.checker.model.exception;


import com.landao.checker.model.collection.IllegalsHolder;
import com.landao.checker.utils.CheckerManager;

public class CheckIllegalException extends RuntimeException{

    public CheckIllegalException(IllegalsHolder illegalsHolder) {
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
