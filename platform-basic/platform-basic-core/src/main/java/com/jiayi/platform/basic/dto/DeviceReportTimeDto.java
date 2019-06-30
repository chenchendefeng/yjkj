package com.jiayi.platform.basic.dto;

public class DeviceReportTimeDto {
    private Long deviceId;
    private Long earliest;
    private Long latest;

    public DeviceReportTimeDto(Long deviceId, Long earliest, Long latest) {
        super();
        this.deviceId = deviceId;
        this.earliest = earliest;
        this.latest = latest;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getEarliest() {
        return earliest;
    }

    public void setEarliest(Long earliest) {
        this.earliest = earliest;
    }

    public Long getLatest() {
        return latest;
    }

    public void setLatest(Long latest) {
        this.latest = latest;
    }

}
