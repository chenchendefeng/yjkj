package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多特征分析、亲密关系分析共用dto
 */
@Getter
@Setter
@ToString
public class MultiFeatureAnalysisDto {
    private Integer objectType;
    private String objectValue;
    private Integer matchDays;
    private Integer matchCount; // 亲密关系分析无匹配系数
    private Long matchTracks;
}
