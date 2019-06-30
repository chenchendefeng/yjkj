package com.jiayi.platform.basic.dto;

public class DeviceStreamStaticDto {

    private Long deviceId;
    private Integer trackType;
    private Long startTime;
    private Long endTime;
    private Long count;
    private Integer status;

    public DeviceStreamStaticDto() {
        super();
    }

    public DeviceStreamStaticDto(Long deviceId, Integer trackType, Long startTime, Long endTime, Long count,
            Integer status) {
        super();
        this.deviceId = deviceId;
        this.trackType = trackType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.count = count;
        this.status = status;
    }

    public DeviceStreamStaticDto(Long deviceId, Integer trackType, Long startTime, int status) {
        super();
        this.deviceId = deviceId;
        this.trackType = trackType;
        this.startTime = startTime;
        this.status = status;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getTrackType() {
        return trackType;
    }

    public void setTrackType(Integer trackType) {
        this.trackType = trackType;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
