package com.jiayi.platform.basic.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceStatusInfoDto {
    private final static String KEYPREFIX = "DEVICE_STATIC_";
    private Long deviceId;
    private Integer isOnline;
    private Integer isActive;
    private Integer isQulified;
    private Long earliest;
    private Long latest;
    private Integer threshold;
    private Integer average;

    public DeviceStatusInfoDto() {
        super();
    }

    public DeviceStatusInfoDto(Long deviceId, Integer isOnline, Integer isActive, Integer isQulified, Long earliest,
                               Long latest, Integer threshold, Integer average) {
        super();
        this.deviceId = deviceId;
        this.isOnline = isOnline;
        this.isActive = isActive;
        this.isQulified = isQulified;
        this.earliest = earliest;
        this.latest = latest;
        this.threshold = threshold;
        this.average = average;
    }

    public static String getKeyprefix() {
        return KEYPREFIX;
    }

    public static String getKey(Long deviceId) {
        return KEYPREFIX + deviceId;
    }

}
