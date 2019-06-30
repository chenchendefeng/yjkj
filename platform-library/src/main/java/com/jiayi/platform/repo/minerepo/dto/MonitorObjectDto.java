package com.jiayi.platform.repo.minerepo.dto;

import com.jiayi.platform.basic.enums.CollectType;

public class MonitorObjectDto {

    private String objectName;
    private String objectType;
    private String objectValue;
    private String name;
    private String certCode;
    private String phone;
    private String address;

    public MonitorObjectDto() {
    }

    public MonitorObjectDto(String objectName, String objectType, String objectValue, String name, String certCode, String phone, String address) {
        this.objectName = objectName;
        this.objectType = objectType;
        this.objectValue = objectValue;
        this.name = name;
        this.certCode = certCode;
        this.phone = phone;
        this.address = address;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return CollectType.getByCode(Integer.valueOf(objectType)).desc();
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
