package com.jiayi.platform.basic.dto;


public class DeviceTimeStreamStaticDto {

    private Long deviceId;
    private String ipPort;
    private Long startTime;
    private Long endTime;
    private Long recordTime;

    public Long getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpPort() { return ipPort; }
    public void setIpPort(String ipPort) { this.ipPort = ipPort; }

    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) {this.startTime = startTime;}

    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) {this.endTime = endTime;}

    public Long getRecordTime() { return recordTime; }
    public void setRecordTime(Long recordTime) { this.recordTime = recordTime; }
}