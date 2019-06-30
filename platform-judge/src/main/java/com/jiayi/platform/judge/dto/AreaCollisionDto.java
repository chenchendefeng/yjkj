package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AreaCollisionDto {
    private String objectValue;
    private Integer matchCount;
    private String matchCondition;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Long fromDeviceId;
    private Long toDeviceId;
    private String imsiImei = "";
}
