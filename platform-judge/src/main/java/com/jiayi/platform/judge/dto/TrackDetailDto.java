package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackDetailDto {
    private String objectValue;
    private Long recordAt;
    private Long latitude;
    private Long longitude;
    private Long deviceId;
    private String imsiImei = "";
}
