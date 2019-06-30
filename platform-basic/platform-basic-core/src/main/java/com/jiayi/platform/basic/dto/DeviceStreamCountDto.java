package com.jiayi.platform.basic.dto;

public class DeviceStreamCountDto {

    private Long deviceId;
    private Integer trackType;
    private Long recordDate;
    private Long limitCount;

    public DeviceStreamCountDto() {
        super();
    }

    public DeviceStreamCountDto(Long deviceId, Integer trackType, Long recordDate, Long limitCount) {
        super();
        this.deviceId = deviceId;
        this.trackType = trackType;
        this.recordDate = recordDate;
        this.limitCount = limitCount;
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

    public Long getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Long recordDate) {
        this.recordDate = recordDate;
    }

    public Long getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(Long limitCount) {
        this.limitCount = limitCount;
    }

}
