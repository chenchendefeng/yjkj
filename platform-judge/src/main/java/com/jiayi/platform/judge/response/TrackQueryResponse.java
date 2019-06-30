package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackQueryResponse {
    private String objectTypeName;
    private String objectId;
    private String objectValue;
    private Long minHappenAt;
    private Long maxHappenAt;
    private String beginDeviceAddress = "";
    private String endDeviceAddress = "";
    private String beginPlaceAddress = "";
    private String endPlaceAddress = "";
    private Integer trackCount;
    private Integer deviceCount;
    private String desc = "";
    private String imsiImei = "";
}
