package com.jiayi.platform.repo.monrepo.vo;

import com.jiayi.platform.repo.monrepo.dto.MonObjDesc;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MonPersonRequest {
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名", example = "张三")
    private String name;
    @ApiModelProperty(value = "性别", example = "男")
    private String sex;
    @ApiModelProperty(value = "年龄", example = "20")
    private Integer age;
    @ApiModelProperty(value = "籍贯", example = "广东中山")
    private String birthplace;
    @ApiModelProperty(value = "住址", example = "深圳市宝安区新安街道洪浪北")
    private String address;
    @ApiModelProperty(value = "证件号码", example = "44032239320322323s", dataType = "String")
    private String certCode;
    @ApiModelProperty(value = "手机号码", example = "1813329329s")
    private String phone;
    @ApiModelProperty(value = "备注", example = "身高176，体重140")
    private String description;
    @ApiModelProperty(value = "布控类型", example = "1 维稳/2 前科/3 黄赌毒/4 盗窃/5 抢夺/6 重型")
    private Integer monitorType;
    @NotNull(message = "所属库ID不能为空")
    @ApiModelProperty(value = "所属库ID", example = "32432323223")
    private Long repoId;
    @NotNull(message = "操作员ID不能为空")
    @ApiModelProperty(value = "操作员ID", example = "21")
    private Integer userId;
    @ApiModelProperty(value = "布控要素数组")
    private List<MonObjDesc> monObjList;

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

    public List<MonObjDesc> getMonObjList() {
        return monObjList;
    }
    public void setMonObjList(List<MonObjDesc> monObjList) {
        this.monObjList = monObjList;
    }
}
