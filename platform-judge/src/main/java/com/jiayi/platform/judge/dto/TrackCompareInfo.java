package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class TrackCompareInfo {
    private String objectValue;
    private Long recordAt;
    private Long deviceId;
    private Integer longitude;
    private Integer latitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackCompareInfo that = (TrackCompareInfo) o;
        return Objects.equals(objectValue, that.objectValue) &&
                Objects.equals(recordAt, that.recordAt) &&
                Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(latitude, that.latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectValue, recordAt, deviceId, longitude, latitude);
    }
}
