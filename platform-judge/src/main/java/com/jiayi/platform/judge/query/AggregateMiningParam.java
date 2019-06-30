package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AggregateMiningParam {
    private List<String> objTypes;
    private Long startTime;
    private Long endTime;
    private String tableName;
    private String objTypeFieldName;
    private String startTimeFieldName;
    private String endTimeFieldName;
}
