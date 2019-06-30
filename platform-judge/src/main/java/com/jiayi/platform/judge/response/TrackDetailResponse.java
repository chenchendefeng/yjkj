package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackDetailResponse {
    private String objectId;
    private String objectValue;
    private String address = "";
    private Long recordAt;
    private Double latitude;
    private Double longitude;
    private String deviceId;
    private String deviceName = "";
    private String imsiImei = "";
}
