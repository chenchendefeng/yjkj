package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackMergeDto {
    private String objectTypeName;
    private String objectValue;
    private Long recordAt;
    private Long deviceId;
    private Long latitude;
    private Long longitude;
    private String imsiImei = "";
}
