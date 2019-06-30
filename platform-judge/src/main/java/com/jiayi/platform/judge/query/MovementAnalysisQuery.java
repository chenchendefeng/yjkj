package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class MovementAnalysisQuery extends PageBaseQuery {
//    private String objectTypeName;
    private Integer objectType;
    private String objectValue;
    private Long beginDate;
    private Long endDate;
    private Long totalDays;
    private Set<Long> gridCodeList;
}
