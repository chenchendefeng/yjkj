package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiTrackDetailDto {
    private String objectValue;
    private Long originRecordAt;
    private Long matchRecordAt;
    private Long latitude;
    private Long longitude;
    private Long refLatitude;
    private Long refLongitude;
    private Long deviceId;
    private Long originDeviceId;
    private String imsiImei = "";
}
