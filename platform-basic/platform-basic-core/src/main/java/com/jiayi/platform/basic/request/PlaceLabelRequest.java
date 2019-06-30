package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class PlaceLabelRequest {

    @NotBlank(message = "标签名称不能为空")
    @ApiModelProperty(value = "标签名称", required = true)
    private String name;
    @ApiModelProperty(value = "父级标签code,当type=0时,pcode=0")
    private String pcode;
    @NotNull(message = "标签类型不能为空")
    @ApiModelProperty(value = "类型 0标签类别 1一级标签 2二级标签", required = true)
    private Integer type;
    @ApiModelProperty(value = "备注")
    private String remark;
}
