package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AreaCollisionResponse {
    private String objectId;
    private String objectValue;
    private Integer matchCount;
    private String matchCondition;
    private Long beginDate;
    private Long endDate;
    private String beginAddress = "";
    private String endAddress = "";
    private String desc;
    private boolean eRecord;
    private String imsiImei = "";
}
