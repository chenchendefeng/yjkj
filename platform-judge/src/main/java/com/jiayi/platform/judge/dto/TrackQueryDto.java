package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackQueryDto {
    private String objectTypeName;
    private String objectValue;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Long fromDeviceId;
    private Long toDeviceId;
    private Integer matchCount;
    private Integer uniqueDevCount;
    private String imsiImei = "";
}
