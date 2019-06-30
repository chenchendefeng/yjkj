package com.jiayi.platform.judge.query;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多特征分析、亲密关系分析轨迹列表共用Query
 */
@Getter
@Setter
@ToString
public class MultiFeatureDetailQuery extends PageBaseQuery {
    private Integer objectType;
    private String objectValue;
    private Long beginDate;
    private Long endDate;
    private Integer matchCoefficient; // 亲密关系分析无匹配系数
    private Integer matchObjectType;
    private String matchObjectValue;
}
