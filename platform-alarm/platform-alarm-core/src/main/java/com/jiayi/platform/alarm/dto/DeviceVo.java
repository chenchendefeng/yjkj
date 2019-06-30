package com.jiayi.platform.alarm.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class DeviceVo {
    Long id;
    Long placeId;
    Double longitude = null;
    Double latitude = null;
    String name;

    public DeviceVo() {
    }

    public DeviceVo(Long id, Double longitude, Double latitude, Long placeId, String name) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeId = placeId;
        this.name = name;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @JsonSerialize(using = ToStringSerializer.class)
    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
