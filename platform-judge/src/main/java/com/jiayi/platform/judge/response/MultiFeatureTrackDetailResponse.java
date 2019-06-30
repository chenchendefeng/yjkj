package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 多特征分析、亲密关系分析轨迹列表共用Response
 */
@Getter
@Setter
@ToString
public class MultiFeatureTrackDetailResponse {
    private Long srcTime; // 源轨迹时间
    private Long desTime; // 目标轨迹时间
    private Long timeDistance; // 源与目标轨迹时间差
    private Double srcLng;
    private Double srcLat;
    private Double desLng;
    private Double desLat;
    private Double distance; // 源与目标轨迹距离差
    private String status; // 匹配状态
    private String srcAddress;
    private String desAddress;
}
