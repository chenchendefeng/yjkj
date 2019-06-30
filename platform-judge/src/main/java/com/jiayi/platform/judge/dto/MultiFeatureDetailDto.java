package com.jiayi.platform.judge.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多特征分析、亲密关系分析轨迹列表共用dto
 */
@Getter
@Setter
@ToString
public class MultiFeatureDetailDto {
    private Long recordDate;
    private Long beginTime;
    private Long endTime;
    private Long beginDeviceId;
    private Long endDeviceId;
    private Integer coefficient; // 亲密关系分析无匹配系数
    private Integer matchNum;
}
