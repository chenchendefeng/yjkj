package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceSearchRequest {

    private Integer page = 0;
    private Integer size = 10;

    @ApiModelProperty(value = "所属区县id", example = "440306")
    private String districtId;
    @ApiModelProperty(value = "所属部门id", example = "5")
    private Integer departmentId;
    @ApiModelProperty(value = "场所id", example = "855")
    private String placeId;
    //	@ApiModelProperty(value = "场所分类id", example = "2")
//	private Long placeTagId;
//	private Integer srcId;
    @ApiModelProperty(value = "设备状态：0离线，1在线；", example = "0")
    private Integer isOnline;
    //	@ApiModelProperty(value = "合格状态：0不合格，1合格，2待定", example = "0")
//	private Integer isQualified;
    @ApiModelProperty(value = "安装时间（开始）", example = "1554792483000")
    private String beginDate;
    @ApiModelProperty(value = "安装时间（结束）", example = "1554792663000")
    private String endDate;
    @ApiModelProperty(value = "设备编码")
    private String code;
    @ApiModelProperty(value = "设备名称或地址", example = "测试A1")
    private String name;
    //	@ApiModelProperty(value = "设备编码或名称", example = "测试A1")
//	private String keyWord;
    @ApiModelProperty(value = "设备类型id", example = "1")
    private Integer type;
    @ApiModelProperty(value = "供应商id", example = "3")
    private Long vendorId;
    @ApiModelProperty(value = "设备ip", example = "113.114.66.12")
    private String ip;
    @ApiModelProperty(value = "是否忽略空部门的设备", example = "true")
    private boolean ignoreNull;
}
