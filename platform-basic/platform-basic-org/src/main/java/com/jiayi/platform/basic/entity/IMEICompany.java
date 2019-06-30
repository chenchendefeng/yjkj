package com.jiayi.platform.basic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "t_imei_company")
public class IMEICompany {

    /**
     * IMEI 前8位编码
     */
    @Id
    private String code;

    /**
     * 注册团体、公司名称
     */
    @Column(name="organization_name")
    private String organizationName;
    /**
     * 手机型号
     */
    private String model;

    /**
     * 注册团体、公司中文名称
     */
    private String version;

    /**
     * 注册团体、公司名称中文地址
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    @Column(name = "check_imei")
    private String checkImei;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCheckImei() {
        return checkImei;
    }

    public void setCheckImei(String checkImei) {
        this.checkImei = checkImei;
    }

    @Override
    public String toString() {
        return "IMEICompany [code=" + code + ", organization=" + organizationName
                + "]";
    }
}
