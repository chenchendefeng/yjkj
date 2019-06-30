package com.jiayi.platform.repo.monrepo.dto;

public class MonObjDesc {
    private Integer objType = -1;
    private String  objValue = "";

    public MonObjDesc() {}

    public MonObjDesc(Integer objType, String  objValue) {
        this.objType  = objType;
        if (objValue == null) objValue = "";
        this.objValue = objValue.trim();
    }

    public Integer getObjType() {
        return objType;
    }

    public void setObjType(Integer objType) {
        this.objType = objType;
    }

    public String getObjValue() {
        return objValue;
    }

    public void setObjValue(String objValue) {
        if (objValue == null) objValue = "";
        this.objValue = objValue.trim();
    }
}
