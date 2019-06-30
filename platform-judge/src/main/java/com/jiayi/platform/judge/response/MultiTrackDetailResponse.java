package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiTrackDetailResponse {
    private String objectId;
    private Long originRecordAt;
    private Long matchRecordAt;
    private String originAddress = "";
    private String matchAddress = "";
    private Integer matchDistance;
    private Double latitude;
    private Double longitude;
    private Double originLatitude;
    private Double originLongitude;
    private String deviceId;
    private String deviceName = "";
    private String imsiImei = "";
}
