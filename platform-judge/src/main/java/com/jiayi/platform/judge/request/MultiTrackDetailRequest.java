package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper=true)
public class MultiTrackDetailRequest extends JudgeDetailRequest {
    private String objectId;
    private String refObjectTypeName;
    private String refObjectValue;
    private Long beginDate;
    private Long endDate;
    private Integer timeOffset;
    private Integer distance;
}
