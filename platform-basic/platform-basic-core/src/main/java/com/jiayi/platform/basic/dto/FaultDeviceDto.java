package com.jiayi.platform.basic.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: platform
 * @description: 故障设备统计
 * @author: Mr.liang
 * @create: 2018-09-29 16:22
 **/
@Getter
@Setter
public class FaultDeviceDto {
    @ApiModelProperty(value = "数据源", name = "数据源", example = "审计设备")
    private String src;
    @ApiModelProperty(value = "设备编码", name = "设备编码")
    private String code;
    @ApiModelProperty(value = "设备最早采集时间", name = "设备最早采集时间", example = "1497600216000")
    private Long startTime;
    @ApiModelProperty(value = "设备(最晚采集)时间", name = "设备(最晚采集)时间", example = "1537393500000")
    private Long endTime;
    @ApiModelProperty(value = "ip地址", name = "ip地址", example = "192.168.0.100")
    private String ipPort;
    @ApiModelProperty(value = "服务器时间", name = "服务器时间", example = "1537393500000")
    private Long recordTime;
    @ApiModelProperty(value = "设备地址", name = "设备地址")
    private String address;
    @ApiModelProperty(value = "录入状态", name = "录入状态", example = "0:已创建，1：未创建")
    private Integer noCreate;
    @ApiModelProperty(value = "时间差", name = "时间差", example = "正数：小于，负数：大于")
    private Long diffTime;

    public FaultDeviceDto() {
    }

    public FaultDeviceDto(String src, String code, Long startTime, Long endTime, String ipPort, Long recordTime, String address, Integer noCreate, Long diffTime) {
        this.src = src;
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ipPort = ipPort;
        this.recordTime = recordTime;
        this.address = address;
        this.noCreate = noCreate;
        this.diffTime = diffTime;
    }
}
