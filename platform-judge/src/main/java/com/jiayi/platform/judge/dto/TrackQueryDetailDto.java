package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackQueryDetailDto {
    private String objectTypeName;
    private String objectValue;
    private Long recordAt;
    private Long longitude;
    private Long latitude;
    private Long deviceId;

    private String apMac = "";
    private String apName = "";
    private Integer power;
    private Integer channel;
    private String imeiCode = "";
    private String imsiCode = "";
    private String imsiImei = "";
}
