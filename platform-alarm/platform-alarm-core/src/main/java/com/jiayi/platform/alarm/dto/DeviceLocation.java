package com.jiayi.platform.alarm.dto;

public class DeviceLocation {
    private String id;
    private Double longitude;
    private Double latitude;

    public DeviceLocation() {
        super();
    }

    public DeviceLocation(String id, Double longitude, Double latitude) {
        super();
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "DeviceLocation [id=" + id + ", longitude=" + longitude + ", latitude=" + latitude + "]";
    }
}
