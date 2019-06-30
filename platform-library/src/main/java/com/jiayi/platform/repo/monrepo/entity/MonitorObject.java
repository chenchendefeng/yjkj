package com.jiayi.platform.repo.monrepo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class MonitorObject {
    private Long uid;
    private String objectName;
    private Integer objectType;
    private String objectValue;
    private String vendorDesc;
    private String description;
    private Long personId;
    private Long repoId;
    private Integer userId;
    private Long createAt;
    private Long updateAt;
    private String name;
    private String certCode;
    private String phone;
    private String address;
    private String md5;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public String getVendorDesc() {
        return vendorDesc;
    }

    public void setVendorDesc(String vendorDesc) {
        this.vendorDesc = vendorDesc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "MonitorObject{" + "objectName='" + objectName + '\'' + ", objectType="
                + objectType + ", objectValue='" + objectValue + '\'' + ", vendorDesc='" + vendorDesc + '\''
                + ", description='" + description + '\''+ ", personId" + personId + ", repoId=" + repoId
                + ", name='" + name + '\'' + ", certCode='" + certCode + '\''
                + ", phone='" + phone + '\'' + ", address='" + address + '\'' + "}";
    }
}