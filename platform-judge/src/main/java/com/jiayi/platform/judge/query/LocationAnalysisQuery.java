package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class LocationAnalysisQuery extends PageBaseQuery {
//    private String objectTypeName;
    private Integer objectType;
    private String objectValue;
    private Long beginDate;
    private Long endDate;
    private Integer density;
    private Set<Long> gridCodeList;
    private Long origin;
    private Integer countTime;
}
