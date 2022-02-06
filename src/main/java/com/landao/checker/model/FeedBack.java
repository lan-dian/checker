package com.landao.checker.model;

import com.landao.checker.utils.CheckerManager;

public class FeedBack {

    private Boolean requireNewValue;

    private Object newValue;

    public static FeedBack pass(){
        FeedBack feedBack = new FeedBack();
        feedBack.requireNewValue=false;
        feedBack.newValue=null;
        return feedBack;
    }

    public static FeedBack pass(Object value){
        FeedBack feedBack = new FeedBack();
        feedBack.requireNewValue=true;
        feedBack.newValue=value;
        return feedBack;
    }

    public static FeedBack illegal(String fieldName,String illegalReason){
        FeedBack feedBack = new FeedBack();
        feedBack.requireNewValue=false;
        feedBack.newValue=null;
        CheckerManager.addIllegal(fieldName,illegalReason);
        return feedBack;
    }

    public boolean requiresNewValue(){
        return requireNewValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
