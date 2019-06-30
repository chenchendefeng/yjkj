package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAnalysisDto {
    private String objectValue;
    private Integer matchCount;
    private Long minHappenAt;
    private Long maxHappenAt;
    private String imsiImei = "";
}
