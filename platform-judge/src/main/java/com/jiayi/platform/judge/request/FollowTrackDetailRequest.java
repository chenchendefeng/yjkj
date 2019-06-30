package com.jiayi.platform.judge.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper=true)
public class FollowTrackDetailRequest extends JudgeDetailRequest {
    private String objectId;
    private String refObjectValue;
    private Long beginDate;
    private Long endDate;
    private List<Area> areaList;
    private Integer timeOffset;
}
