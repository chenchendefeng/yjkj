package com.jiayi.platform.basic.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DeviceRequest {

    //    @NotNull(message = "数据来源不能为空")
//    @ApiModelProperty(value = "数据来源", example = "1001")
//    private Long srcId;
    //--------基本信息
    @NotBlank(message = "设备名称不能为空")
    @ApiModelProperty(value = "设备名称", example = "测试设备")
    private String name;
    @ApiModelProperty(value = "设备对应场所")
    private String placeId;
    @NotBlank(message = "场所编码不能为空")
    @ApiModelProperty(value = "场所编码")
    private String placeCode;
    @NotBlank(message = "设备编号不能为空")
    @ApiModelProperty(value = "设备编号", example = "78A351061B74")
    private String code;
    @NotNull(message = "设备子类型不能为空")
    @ApiModelProperty(value = "设备子类型", example = "1")
    private Integer type;
    @NotNull(message = "供应商不能为空")
    @ApiModelProperty(value = "供应商id", example = "1")
    private Integer vendorId;
    @NotNull(message = "数据源不能为空")
    @ApiModelProperty(value = "数据源code", example = "1")
    private String src; //code of source
    @ApiModelProperty(value = "设备型号")
    private Integer model;
    @NotNull(message = "设备安装时间不能为空")
    @ApiModelProperty(value = "设备安装时间", example = "2018-07-16 19:38:16")
    private String installAt;

    @NotNull(message = "设备状态不能为空")
    @ApiModelProperty(value = "设备状态 (0:删除|1:正常|2:暂停) ", example = "1")
    private Integer status;

    //--------地址信息
    @NotBlank(message = "设备安装地址不能为空")
    @ApiModelProperty(value = "设备安装地址(详细地址)", example = "xx街道xx路口")
    private String address;
    @ApiModelProperty(value = "设备经度", example = "112.762360484584")
    private Double longitude;
    @ApiModelProperty(value = "设备纬度", example = "23.577091463941")
    private Double latitude;
    //--------其他信息
    @ApiModelProperty(value = "采集半径（单位：？）", example = "200")
    private Long collectionRadius;
    @ApiModelProperty(value = "采集间隔（单位：？）", example = "1")
    private Long collectionInterval;
    @ApiModelProperty(value = "设备ID")
    private String dId;
    @ApiModelProperty(value = "SSID")
    private String ssid;
    @ApiModelProperty(value = "安装人姓名", example = "张三")
    private String installerName;
    @ApiModelProperty(value = "安装人电话", example = "18819356464")
    private String installerPhone;
    @ApiModelProperty(value = "认证方式")
    private Integer authType;// 0认证码认证 1非认证码认证 2其他
    @ApiModelProperty(value = "实名认证数据来源")
    private Integer authSrc;
    @ApiModelProperty(value = "设备安装类型")
    private Integer installType;//0室内 1室外
    @ApiModelProperty(value = "终端上网环境")
    private Integer InternetEnvironment;
    @ApiModelProperty(value = "楼层")
    private String installFloor;
    @ApiModelProperty(value = "房间号")
    private String installRoom;
    @ApiModelProperty(value = "地铁路线信息")
    private String subwayLineInfo;
    @ApiModelProperty(value = "地铁车辆信息")
    private String subwayVehicleInfo;
    @ApiModelProperty(value = "地铁车厢编号")
    private String subwayCompartmentNum;
    @ApiModelProperty(value = "地铁车牌号码")
    private String subwayCarCode;
    @ApiModelProperty(value = "地铁站点信息")
    private String subwayStationInfo;
    @ApiModelProperty(value = "itv账号")
    private String itvAccount;
    @ApiModelProperty(value = "软件版本号")
    private String softwareVersion;
    @ApiModelProperty(value = "固件版本号")
    private String firmwareVersion;
    @ApiModelProperty(value = "设备采集方式:0固定；1移动")
    private Integer fixed;
}
