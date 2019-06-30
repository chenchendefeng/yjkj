package com.jiayi.platform.repo.monrepo.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jiayi.platform.repo.monrepo.dto.MonObjDesc;

import java.util.List;

public class MonitorPerson {
    private Long uid;
    private String name;
    private String sex;
    private Integer age;
    private String birthplace;
    private String address;
    private String certCode;
    private String phone;
    private String description;
    private Integer monitorType;
//    @ManyToOne
//    @JoinColumn(name = "repo_id", referencedColumnName = "uid", insertable = false, updatable = false)
//    private MonitorRepo repo;
    private Long repoId;
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private User user;
    private Integer userId;
    private Long createAt;
    private Long updateAt;
    private List<MonObjDesc> monObjList;
    private String md5;

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

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
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

    public List<MonObjDesc> getMonObjList() {
        return monObjList;
    }

    public void setMonObjList(List<MonObjDesc> monObjList) {
        this.monObjList = monObjList;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public String toString() {
        return "MonitorPerson{" + "name='" + name + '\'' + ", sex='" + sex + '\'' + ", age="
                + age + ", birthplace='" + birthplace + '\'' + ", address='" + address + '\'' + ", certCode='"
                + certCode + '\'' + ", phone='" + phone + '\'' + ", description='" + description + '\''
                + ", monitorType=" + monitorType + ", repoId=" + repoId + "}";
    }
}