package com.jiayi.platform.alarm.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlarmSuspectInfo {

    @ApiModelProperty(value = "预警时间")
    private Long alarmTime;
    @ApiModelProperty(value = "预警对象")
    private String name;
    @ApiModelProperty(value = "特征类型")
    private String objectType;
    @ApiModelProperty(value = "特征值")
    private String objectValue;
    @ApiModelProperty(value = "预警地点设备id")
    private String deviceId;
    @ApiModelProperty(value = "设备地址")
    private String address;
    @ApiModelProperty(value = "纬度")
    private double lat;
    @ApiModelProperty(value = "经度")
    private double lng;
}
