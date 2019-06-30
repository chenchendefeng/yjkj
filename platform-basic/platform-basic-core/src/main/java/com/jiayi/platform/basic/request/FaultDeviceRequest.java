package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaultDeviceRequest {

    private Integer page;
    private Integer size;

    @ApiModelProperty(value = "设备编码")
    private String code;
    @ApiModelProperty(value = "ip")
    private String ip;
    @ApiModelProperty(value = "设备故障类型", example = "1：未创建，2：时间错误")
    private Integer type;

}
