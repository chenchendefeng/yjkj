package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAnalysisResponse {
//    private String objectTypeName;
    private String objectId;
    private String objectValue;
    private Integer matchCount;
    private Long minHappenDate;
    private Long maxHappenDate;
    private String desc;
    private String imsiImei = "";
}
