package com.jiayi.platform.basic.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long placeId;
    private Double longitude = null;
    private Double latitude = null;
    private String name;
    private String address;
    private Integer isOnline;
    private Integer isActive;
    private Integer isQulified;
    private String earliest;//yyyy-MM-dd HH:mm:ss
    private String latest;//yyyy-MM-dd HH:mm:ss
    private Long dataEndTime;
    private String deviceCode;
    private Integer deviceTypeId;
    private String subTypeName;
    private String mainTypeName;
    private String mac;

    public DeviceVo() {
    }

    public DeviceVo(Long id, Double longitude, Double latitude, Long placeId, String name) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeId = placeId;
        this.name = name;
    }

    public DeviceVo(Long id, Double longitude, Double latitude, Long placeId, String name, Integer isOnline, Integer isActive, Integer isQulified, String earliest, String latest, String deviceCode, Integer deviceTypeId) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeId = placeId;
        this.name = name;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.isQulified = isQulified;
        this.earliest = earliest;
        this.latest = latest;
        this.deviceCode = deviceCode;
        this.deviceTypeId = deviceTypeId;
    }
    
}
