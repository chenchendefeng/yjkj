package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FollowCollisionDto {
    private String objectValue;
    private Integer matchCount;
    private Integer uniqueDevCount;
    private Integer trackCount;
    private Long minHappenAt;
    private Long maxHappenAt;
    private Long fromDeviceId;
    private Long toDeviceId;
    private String imsiImei = "";
}
