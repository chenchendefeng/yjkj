package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class SrcRequest {
    @ApiModelProperty(value = "数据源名称", example = "wifi围栏")
    @NotBlank(message = "数据源名称不能为空")
    private String name;
    @ApiModelProperty(value = "数据源编码", example = "1001")
    @NotBlank(message = "数据源编码不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z_]{1,}$", message = "只能包含数字、字母、下划线")
    private String code;
    @ApiModelProperty(value = "设备供应商", example = "1")
    private Integer vendorId;
    @ApiModelProperty(value = "数据类型，1接入，2分发", example = "1")
    private Integer dataType;
    @ApiModelProperty(value = "数据源描述", example = "描述信息")
    private String description;
}
