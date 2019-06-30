package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrackCompareResponse {
    private Long srcTime; // 源轨迹时间
    private Long desTime; // 目标轨迹时间
    private Long timeDistance; // 源与目标轨迹时间差
    private Double srcLng;
    private Double srcLat;
    private Double desLng;
    private Double desLat;
    private Double distance; // 源与目标轨迹距离差
    private String srcAddress;
    private String desAddress;
}
