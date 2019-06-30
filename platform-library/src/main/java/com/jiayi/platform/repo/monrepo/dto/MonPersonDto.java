package com.jiayi.platform.repo.monrepo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jiayi.platform.basic.enums.CollectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonPersonDto {
    private Long uid;
    private String name;
    private String sex;
    private Integer age;
    private String birthplace;
    private String address;
    private Integer monitorType;
    private String certCode;
    private String phone;
    private String description;
    private String objectValues;
    private List<MonObjDesc> monObjList;
    private Long repoId;
    private Long createAt;
    private Long updateAt;


    @JsonSerialize(using = ToStringSerializer.class)
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectValues() {
        return objectValues;
    }

    public void setObjectValues(String objectValues) {
        if (objectValues == null) objectValues = "";
        this.objectValues = objectValues;

        List<String> objects = Arrays.asList(objectValues.split(","));
        List<MonObjDesc> monObjList = new ArrayList<>();
        for (String obj : objects) {
            obj = obj.trim();
            int pos = obj.indexOf(" ");
            if (pos != -1) {
                MonObjDesc objDesc = new MonObjDesc();
                objDesc.setObjType(CollectType.getByLabel(obj.substring(0, pos)).code());
                objDesc.setObjValue(obj.substring(pos).trim());
                monObjList.add(objDesc);
            }
        }
        setMonObjList(monObjList);
    }

    public List<MonObjDesc> getMonObjList() {
        return monObjList;
    }

    public void setMonObjList(List<MonObjDesc> monObjList) {
        this.monObjList = monObjList;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
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

}
