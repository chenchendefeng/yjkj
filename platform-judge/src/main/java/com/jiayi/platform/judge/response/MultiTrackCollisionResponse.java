package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiTrackCollisionResponse {
    private String objectId;
    private String objectValue;
    private Integer matchCount;
    private Integer uniqueDevCount;
    private Integer trackCount;
    private Long beginDate;
    private Long endDate;
    private String beginAddress = "";
    private String endAddress = "";
    private String desc;
    private boolean eRecord;
    private String imsiImei = "";
}
