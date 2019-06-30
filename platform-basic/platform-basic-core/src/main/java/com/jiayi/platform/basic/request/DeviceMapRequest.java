package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeviceMapRequest {

    @ApiModelProperty(value = "在线状态,0离线 1在线")
    private List<Integer> onlineStatus;
    @ApiModelProperty(value = "设备类型")
    private List<Integer> types;
    private Long startTime;
    private Long endTime;
}
