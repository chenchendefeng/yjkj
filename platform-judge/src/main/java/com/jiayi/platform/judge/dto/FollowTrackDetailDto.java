package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FollowTrackDetailDto {
    private String objectValue;
    private Long originRecordAt;
    private Long matchRecordAt;
    private Long latitude;
    private Long longitude;
    private Long deviceId;
    private String imsiImei = "";
}
