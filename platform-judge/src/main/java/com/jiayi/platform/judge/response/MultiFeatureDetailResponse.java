package com.jiayi.platform.judge.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiFeatureDetailResponse {
    private Long recordDate;
    private Long beginTime;
    private Long endTime;
    private String beginAddress = "";
    private String endAddress = "";
    private Double coefficient;
    private Integer matchNum;
}
