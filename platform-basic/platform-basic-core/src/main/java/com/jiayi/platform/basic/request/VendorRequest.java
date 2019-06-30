package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VendorRequest {
    @NotBlank(message = "厂商组织机构编码不能为空")
    @ApiModelProperty(value = "厂商组织机构编码", example = "AB1253898") //由web前端生成，唯一
    private String code;
    @NotBlank(message = "供应商名称不能为空")
    @ApiModelProperty(value = "供应商名称", example = "深圳甲易科技有限公司")
    private String name;
//    @NotBlank(message = "供应商简称不能为空")
//    @ApiModelProperty(value = "供应商简称", example = "甲易")
//    private String shortName;
    @NotBlank(message = "供应商地址不能为空")
    @ApiModelProperty(value = "地址", example = "深圳市宝安区")
    private String address;
    @NotBlank(message = "联系人不能为空")
    @ApiModelProperty(value = "联系人", example = "蔡总")
    private String contact;
    @NotBlank(message = "联系人电话不能为空")
    @ApiModelProperty(value = "联系人电话", example = "3333")
    private String phone;
    @NotBlank(message = "邮箱信息不能为空")
    @ApiModelProperty(value = "e-mail", example = "bzj@jy.com")
    private String email;
    @ApiModelProperty(value = "其他信息", example = "")
    private String exInfo;
}
