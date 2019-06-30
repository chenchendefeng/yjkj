package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class DeviceInfoRequest {

    @NotNull(message = "实体类型不能为空")
    @ApiModelProperty(value = "实体类型:1mac 2carno 3imei 4imsi", example = "1")
    private Integer type;
    @NotBlank(message = "实体值不能为空")
    @ApiModelProperty(value = "实体值", example = "DC55835A9775")
    private String code;
}
