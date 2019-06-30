package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AppearCollisionDto {
    private String objectValue;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Long fromDeviceId;
    private Long toDeviceId;
    private String imsiImei = "";
}
