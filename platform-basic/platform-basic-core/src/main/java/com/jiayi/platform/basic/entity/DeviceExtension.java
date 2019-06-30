package com.jiayi.platform.basic.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : qinxiaoni
 * @date : 2019-04-10 18:36
 */
@Entity
@Table(name = "t_device_extension")
@Getter
@Setter
@ToString
public class DeviceExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String src;
    private String code;
//    private String type;// 设备子类型
    private Integer model;// 设备型号
    private String subwayStationInfo;
    private String subwayLineInfo;
    private String subwayVehicleInfo;
    private String subwayCompartmentNum;
    private String subwayCarCode;
    private Long collectionInterval;
    private Long collectionRadius;
    private String installerName;
    private String installerPhone;
    private String installFloor;
    private String installRoom;
    private String installType;
    private Date lastConnectTime;
    private String remark;
    private String dId;
    private String ssid;
    private String authType;// 认证方式
    private String authSrc;// 认证方式数据源
    private String internetEnvironment;// 终端上网环境
    private String itvAccount;
    private Integer fixed;
}
