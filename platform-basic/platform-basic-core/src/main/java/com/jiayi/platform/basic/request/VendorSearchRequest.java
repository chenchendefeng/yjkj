package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VendorSearchRequest {
    private Integer page;
    private Integer size;
    @ApiModelProperty(value = "供应商名字", example = "甲易")
    private String name;
    @ApiModelProperty(value = "供应商代码", example = "1001")
    private String code;
}