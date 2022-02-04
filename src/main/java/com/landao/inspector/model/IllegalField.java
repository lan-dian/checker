package com.landao.inspector.model;

import java.util.List;


public class IllegalField {

    private String fieldName;

    private String illegalReason;

    public IllegalField() {
    }

    public IllegalField(String fieldName, String illegalReason) {
        this.fieldName = fieldName;
        this.illegalReason = illegalReason;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getIllegalReason() {
        return illegalReason;
    }

    public void setIllegalReason(String illegalReason) {
        this.illegalReason = illegalReason;
    }

}
