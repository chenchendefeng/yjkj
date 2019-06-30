package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 多特征分析、亲密关系分析共用Query
 */
@Getter
@Setter
@ToString
public class MultiFeatureAnalysisQuery extends PageBaseQuery {
    private Integer objectType;
    private List<Integer> resultTypeList;
    private String objectValue;
    private Long beginDate;
    private Long endDate;
    private Integer matchDays;
    private Integer matchCoefficient; // 亲密关系分析无匹配系数
}
