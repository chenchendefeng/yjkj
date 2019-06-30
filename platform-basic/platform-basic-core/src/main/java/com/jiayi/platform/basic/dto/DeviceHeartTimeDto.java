package com.jiayi.platform.basic.dto;

public class DeviceHeartTimeDto {
    private Long deviceId;
    private Long heartBeatTime;

    public DeviceHeartTimeDto(Long deviceId, Long heartBeatTime) {
        super();
        this.deviceId = deviceId;
        this.heartBeatTime = heartBeatTime;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(Long heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

}
