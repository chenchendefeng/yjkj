package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackQueryDetailResponse {
    private String objectTypeName;
    private String objectValue;
    private Long recordAt;
    private Double longitude;
    private Double latitude;
    private String deviceId;
    private String deviceAddress = "";

    private String apMac = "";
    private String apName = "";
    private String power = "";
    private String channel = "";
    private String imeiCode = "";
    private String imsiCode = "";
    private String imsiImei = "";

    private String operator = "";
    private String factory = "";
    private String district = "";

}
