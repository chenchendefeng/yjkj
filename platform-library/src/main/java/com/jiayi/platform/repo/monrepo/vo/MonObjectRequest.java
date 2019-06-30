package com.jiayi.platform.repo.monrepo.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MonObjectRequest {
    @NotBlank(message = "物品名称不能为空")
    @ApiModelProperty(value = "物品名称", example = "车牌")
    private String objectName;
    @NotNull(message = "布控实体类型不能为空")
    @ApiModelProperty(value = "布控实体类型", example = "0 other/1 mac/2 carno/3 imei/4 imsi")
    private Integer objectType;
    @NotBlank(message = "布控实体值不能为空")
    @ApiModelProperty(value = "布控实体值", example = "粤B 9329239")
    private String objectValue;
    @ApiModelProperty(value = "厂商描述", example = "奥迪")
    private String vendorDesc;
    @ApiModelProperty(value = "描述", example = "蓝色手机背壳")
    private String description;
    @NotNull(message = "所属库ID不能为空")
    @ApiModelProperty(value = "所属库ID", example = "43324234132")
    private Long repoId;
    @NotNull(message = "操作员ID不能为空")
    @ApiModelProperty(value = "操作员ID", example = "21")
    private Integer userId;
    @ApiModelProperty(value = "布控人员ID", example = "33324234132")
    private Long personId; //可以为null
    @ApiModelProperty(value = "用户姓名", example = "贾六")
    private String name;
    @ApiModelProperty(value = "证件号码", example = "434303030340344333s")
    private String certCode;
    @ApiModelProperty(value = "手机号码", example = "139439432932s")
    private String phone;
    @ApiModelProperty(value = "家庭住址", example = "深圳市宝安区灵芝")
    private String address;

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
        if (objectValue == null) objectValue = "";
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

    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
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
