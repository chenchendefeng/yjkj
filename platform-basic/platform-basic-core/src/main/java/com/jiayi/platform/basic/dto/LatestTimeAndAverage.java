package com.jiayi.platform.basic.dto;

public class LatestTimeAndAverage {
    private Long deviceId;
    private double average;

    public LatestTimeAndAverage() {
        super();
    }

    public LatestTimeAndAverage(Long deviceId, Long latest, double average) {
        super();
        this.deviceId = deviceId;
        this.average = average;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

}
