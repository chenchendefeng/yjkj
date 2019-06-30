package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackMergeResponse {
    private String objectTypeName;
    private String objectId;
    private String objectValue;
    private Long recordAt;
    private String deviceAddress = "";
    private String deviceId;
    private Double latitude;
    private Double longitude;
    private String objDesc = "";
    private String imsiImei = "";
}
