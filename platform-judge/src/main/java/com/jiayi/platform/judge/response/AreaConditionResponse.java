package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AreaConditionResponse {
    private String objectId;
    private String objectValue;
    private String matchCondition;
    private Long beginDate;
    private Long endDate;
}
